package cc.minetale.slime.player;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.TeamStyle;
import cc.minetale.slime.entity.IInventoryHolder;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.lobby.GameLobby;
import cc.minetale.slime.perceive.IDelusory;
import cc.minetale.slime.perceive.PerceiveAction;
import cc.minetale.slime.perceive.PerceiveTeam;
import cc.minetale.slime.rule.IRulable;
import cc.minetale.slime.rule.RuleSet;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

@Getter
public class GamePlayer extends FlamePlayer implements SlimeAudience, ILoadoutHolder, IInventoryHolder, IRulable, IDelusory {

    @Setter private Game game;

    @Nullable @Setter private GameLobby lobby;

    private final RuleSet ruleSet = RuleSet.empty();

    @Setter protected GameTeam gameTeam;

    /** If the player dies when they have 0 or fewer lives, they cannot respawn. Anything below 0 means the player is dead. */
    @Setter protected int lives = 0;
    @Setter private long lastDeathTime = -1;

    protected GameSpawn currentSpawn; //Spawnpoint this player spawned from last

    @Setter protected int score = 0;

    private Loadout loadout;

    private PlayerState state;

    //Target -> Team that this player perceives the target as if in
    private final Map<GamePlayer, SortedMap<Integer, PerceiveTeam>> perceivedPlayers = Collections.synchronizedMap(new HashMap<>());
    private final Set<GamePlayer> hiddenTabPlayers = Collections.synchronizedSet(new HashSet<>());

    public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
    }

    public static GamePlayer fromPlayer(Player player) {
        return (GamePlayer) player;
    }

    public final void setCurrentSpawn(@NotNull GameSpawn spawn) {
        this.currentSpawn = spawn;
        setRespawnPoint(this.currentSpawn.getPosition());
    }

    public final boolean isAlive() {
        return this.lives >= 0;
    }

    @Override
    public void setBoundingBox(double x, double y, double z) {
        //Doesn't change the actual bounding box, just the one used for picking up items
        //Changed as Minestom's one is too small
        super.setBoundingBox(x, y, z);
        this.expandedBoundingBox = getBoundingBox().expand(2, 1, 2);
    }

    public Component getGameName(@Nullable GamePlayer viewer) {
        if(viewer != null) {
            var perceivedAs = viewer.targetPerceivedAs(this);
            if(perceivedAs != null) {
                return Component.text().append(
                        perceivedAs.getPrefix(),
                        Component.text(getUsername(), perceivedAs.getColor()),
                        perceivedAs.getSuffix()
                ).build();
            }
        }

        var showTeam = this.state.showTeam();
        if(showTeam == TriState.TRUE) {
            var gameManager = this.game.getGameManager();
            var gameInfo = gameManager.getGameInfo();
            var teamStyle = gameInfo.getTeamStyle();

            if(teamStyle == TeamStyle.SPECIFIED) {
                var handle = this.gameTeam.getHandle();

                return Component.text().append(
                        handle.getPrefix(),
                        Component.text(getUsername()),
                        handle.getSuffix()
                ).build();
            }
            //No need to check if the TeamStyle is ANONYMOUS,
            //as then the perceived team is used which can only be retrieved if the viewer is present
        }

        var profile = getProfile();
        var grant = profile.getGrant();
        var rank = grant.getRank();

        return Component.text().append(
                rank.getPrefix(),
                Component.space(),
                Component.text(getUsername())
        ).build();
    }

    @Override
    public void setState(PlayerState state) {
        GameManager gameManager = this.game.getGameManager();
        GameInfo gameInfo = gameManager.getGameInfo();

        var event = new GamePlayerStateChangeEvent(this.game, this, this.state, state);
        EventDispatcher.call(event);

        state = event.getNewState();
        this.state = state;

        var gamemode = state.getGamemode();
        if(gamemode != null)
            setGameMode(gamemode);

        //Hides the target on this player's tab if the target state's predicate passes the viewer (this player)
        var hidePredicate = state.getHideTab();
        if(hidePredicate != null)
            this.game.hideInTabIf(this, hidePredicate);

        //Rechecks all others predicates for hiding other players (such as ourselves)
        for(var otherPlayer : this.game.getPlayers()) {
            var otherState = otherPlayer.getState();
            var otherPredicate = otherState.getHideTab();
            if(otherPredicate != null) {
                if(otherPredicate.test(this)) {
                    hideTabTarget(otherPlayer);
                } else {
                    showTabTarget(otherPlayer);
                }
            }
        }

        TeamStyle teamStyle = gameInfo.getTeamStyle();

        var showTeam = state.showTeam();
        if(showTeam == TriState.TRUE && this.gameTeam != null) {
            //Remove previous perceive teams
            var rankTeam = PerceiveTeam.getRankTeamFor(this);
            this.game.perceive(this, new PerceiveAction(PerceiveAction.Type.STOP, rankTeam));

            //Set how others should perceive you (or you yourself if action == SELF)
            Collection<PerceiveAction> perceiveState = state.getPerceiveState();
            for(var action : perceiveState) {
                switch(action.target()) {
                    case SELF -> perceive(this, action);
                    case TEAM -> this.gameTeam.perceive(this, action);
                    case GAME -> this.game.perceive(this, action);
                }
            }

            if(teamStyle == TeamStyle.ANONYMOUS) {
                Collection<GamePlayer> selfAllMembers = this.gameTeam.getPlayers();

                Collection<GamePlayer> otherAllMembers = new LinkedList<>();
                for(var player : this.game.getPlayers()) {
                    if(!selfAllMembers.contains(player))
                        otherAllMembers.add(player);
                }

                perceiveTargetsAs(PerceiveTeam.getAnonymousSelf(), 0, selfAllMembers);
                perceiveTargetsAs(PerceiveTeam.getAnonymousOthers(), 0, otherAllMembers);
            }
        } else if(showTeam == TriState.FALSE) {
            //Perceive player as rank
            var team = PerceiveTeam.getRankTeamFor(this);
            this.game.perceive(this, new PerceiveAction(PerceiveAction.Type.START, team));
        }

        RuleSet ruleSet = state.getRuleSet();
        if(ruleSet != null)
            setRules(ruleSet, state.getRulesApplyStrategy(), state.rulesAffectChildren());

        var viewableRule = state.getViewableRule();
        if(viewableRule != null) {
            updateViewableRule(viewableRule);
        } else {
            updateViewableRule();
        }

        var viewerRule = state.getViewerRule();
        if(viewerRule != null) {
            updateViewerRule(viewerRule);
        } else {
            updateViewerRule();
        }
    }

    @Override
    public void perceive(GamePlayer target, PerceiveAction action) {
        switch(action.type()) {
            case START -> {
                var team = action.team();
                var weight = action.weight();

                perceiveTargetAs(team, weight, target);
            }
            case STOP -> {
                var team = action.team();

                removePerceivedTargetTeam(target, team);
            }
            case STOP_ALL -> {
                removePerceivedTarget(target);
            }
        }
    }

    //Loadouts
    @Override
    public Loadout getLoadout() {
        return this.loadout;
    }

    @Override
    public boolean hasLoadout() {
        return this.loadout != null;
    }

    @Override
    public boolean applyLoadout0(Loadout loadout, List<ItemStack> items) {
        this.loadout = loadout;
        this.inventory.copyContents(items.toArray(new ItemStack[PlayerInventory.INVENTORY_SIZE]));
        return true;
    }

    @Override
    public boolean replaceLoadout0(Loadout loadout, List<ItemStack> items) {
        if(!hasLoadout()) { return false; }
        applyLoadout0(loadout, items);
        return true;
    }

    @Override
    public boolean removeLoadout0() {
        if(!hasLoadout()) { return false; }
        this.loadout = null;
        this.inventory.clear();
        return true;
    }

    //Audience
    @Override
    public void setLoadout(Loadout loadout) {
        loadout.setFor(this);
    }

    @Override
    public void applyLoadout(Loadout loadout) {
        loadout.applyFor(this);
    }

    @Override
    public void replaceLoadout(Loadout loadout) {
        loadout.replaceFor(this);
    }

    @Override
    public void removeLoadout() {
        Loadout.removeIfAny(this);
    }

    /**
     * Delegates to {@linkplain #hideTabTarget(GamePlayer)} / {@linkplain #showTabTarget(GamePlayer)}, <br>
     * <br>
     * If you aren't using this method through {@linkplain SlimeAudience}(s), consinder using: <br>
     * {@linkplain #isTargetHiddenInTab(GamePlayer)},
     * {@linkplain #hideTabTarget(GamePlayer)},
     * {@linkplain #showTabTarget(GamePlayer)}. <br>
     * <br>
     * Only used for {@linkplain SlimeAudience}s.
     */
    @Override
    public void hideInTabIf(GamePlayer target, Predicate<GamePlayer> predicate) {
        if(predicate.test(this)) {
            hideTabTarget(target);
        } else {
            showTabTarget(target);
        }
    }

    //Rules
    @Override
    public RuleSet getRuleSet() {
        return this.ruleSet;
    }

    //Perceive Teams
    @Override
    public Map<GamePlayer, SortedMap<Integer, PerceiveTeam>> getAllPerceivedAs() {
        return Collections.unmodifiableMap(this.perceivedPlayers);
    }

    @Override
    public @Nullable PerceiveTeam perceiveTargetAs(PerceiveTeam perceiveAs, int weight, GamePlayer target) {
        SortedMap<Integer, PerceiveTeam> possibleTeams = this.perceivedPlayers
                .computeIfAbsent(target, k -> Collections.synchronizedSortedMap(new TreeMap<>()));

        var currentHighest = targetPerceivedAsTeamWeight(target);
        var currentTeam = targetPerceivedAs(target);
        if(weight != currentHighest) {
            if(weight > currentHighest) {
                //Remove from the team the target is currently perceived as
                if(currentTeam != null) { currentTeam.remove(this, target); }

                //Add the new team for the target to be perceived as
                perceiveAs.add(this, target);
            }

            //Remove any duplicates of the team (if the same team was already at a different weight and thus not perceived)
            removePerceivedTargetTeam(target, perceiveAs);
        } else {
            if(!Objects.equals(perceiveAs, currentTeam)) {
                //Remove from the team the target is currently perceived as
                if(currentTeam != null) { currentTeam.remove(this, target); }

                //Add the new team for the target to be perceived as
                perceiveAs.add(this, target);
            }
        }

        return possibleTeams.put(weight, perceiveAs);
    }

    @Override
    public List<PerceiveTeam> removePerceivedTarget(GamePlayer target) {
        //Remove from the team the target is currently perceived as
        var currentTeam = targetPerceivedAs(target);
        if(currentTeam != null) { currentTeam.remove(this, target); }

        SortedMap<Integer, PerceiveTeam> possibleTeams = this.perceivedPlayers.get(target);

        List<PerceiveTeam> removed;
        if(possibleTeams != null && !possibleTeams.isEmpty()) {
            Collection<PerceiveTeam> teams = possibleTeams.values();
            removed = new ArrayList<>(teams);

            this.perceivedPlayers.remove(target);
        } else {
            removed = Collections.emptyList();
        }

        return removed;
    }

    @Override
    public boolean removePerceivedTargetTeam(GamePlayer target, PerceiveTeam team) {
        SortedMap<Integer, PerceiveTeam> possibleTeams = this.perceivedPlayers.get(target);
        if(possibleTeams == null || possibleTeams.isEmpty()) { return false; }

        //Remove from the provided team if the target is currently perceived as it
        var perceivedAs = targetPerceivedAs(target);
        if(Objects.equals(team, perceivedAs)) {
            team.remove(this, target);
        }

        for(final var it = possibleTeams.values().iterator(); it.hasNext();) {
            var possibleTeam = it.next();
            if(Objects.equals(team, possibleTeam)) {
                it.remove();
                return true;
            }
        }

        return false;
    }

    @Override
    public List<GamePlayer> removeAllPerceivedTeam(PerceiveTeam team) {
        team.removeAll(this);

        List<GamePlayer> removed = new LinkedList<>();

        for(final var playersIt = this.perceivedPlayers.entrySet().iterator(); playersIt.hasNext();) {
            Map.Entry<GamePlayer, SortedMap<Integer, PerceiveTeam>> playersEnt = playersIt.next();

            var player = playersEnt.getKey();
            SortedMap<Integer, PerceiveTeam> possibleTeams = playersEnt.getValue();

            //Highest weight for the perceived team FOR CURRENT PLAYER
            int highestWeight = possibleTeams.lastKey();

            for(final var teamsIt = possibleTeams.entrySet().iterator(); teamsIt.hasNext();) {
                Map.Entry<Integer, PerceiveTeam> teamsEnt = teamsIt.next();

                var weight = teamsEnt.getKey();
                var perceivedAs = teamsEnt.getValue();

                if(Objects.equals(team, perceivedAs)) {
                    removed.add(player);
                    teamsIt.remove();

                    //Remove from the provided team if the player is currently perceived as it
                    if(weight == highestWeight)
                        team.remove(this, player);

                    if(!teamsIt.hasNext())
                        playersIt.remove();

                    break;
                }
            }
        }

        return removed;
    }

    @Override
    public Set<GamePlayer> getHiddenTabPlayers() {
        return Collections.unmodifiableSet(this.hiddenTabPlayers);
    }

    @Override
    public boolean hideTabTarget(GamePlayer target) {
        if(isTargetHiddenInTab(target)) { return false; }

        var packet = target.getRemovePlayerToList();
        sendPacket(packet);

        return this.hiddenTabPlayers.add(target);
    }

    @Override
    public boolean showTabTarget(GamePlayer target) {
        if(!isTargetHiddenInTab(target)) { return false; }

        var packet = target.getAddPlayerToList();
        sendPacket(packet);

        //TODO Is this necessary?
//        refreshTarget(target);

        return this.hiddenTabPlayers.remove(target);
    }

    @Override
    public void refreshTarget(GamePlayer target) {
        final var gameManager = this.game.getGameManager();
        final var gameInfo = gameManager.getGameInfo();

        var state = target.getState();

        //Hides the target on this player's tab if the target state's predicate passes the viewer (this player)
        var hidePredicate = state.getHideTab();
        if(hidePredicate != null) {
            if(hidePredicate.test(this)) {
                hideTabTarget(target);
            } else {
                showTabTarget(target);
            }
        }

        var showTeam = state.showTeam();
        if(showTeam == TriState.TRUE) {
            Collection<PerceiveAction> perceiveState = state.getPerceiveState();
            for(var action : perceiveState) {
                perceive(target, action);
            }

            var teamStyle = gameInfo.getTeamStyle();
            if(teamStyle == TeamStyle.ANONYMOUS) {
                Collection<GamePlayer> selfMembers = this.gameTeam.getPlayers();
                if(selfMembers.contains(target)) {
                    perceiveTargetsAs(PerceiveTeam.getAnonymousSelf(), 0, Set.of(target));
                } else {
                    perceiveTargetsAs(PerceiveTeam.getAnonymousOthers(), 0, Set.of(target));
                }
            }
        } else if(showTeam == TriState.FALSE) {
            var team = PerceiveTeam.getRankTeamFor(target);
            perceiveTargetAs(team, 0, target);
        }
    }

}
