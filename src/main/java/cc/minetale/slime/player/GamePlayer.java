package cc.minetale.slime.player;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.TeamStyle;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.lobby.GameLobby;
import cc.minetale.slime.rule.*;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.TeamManager;
import cc.minetale.slime.utils.ApplyStrategy;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.util.TriState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static cc.minetale.slime.Slime.TEAM_MANAGER;

@Getter
public class GamePlayer extends FlamePlayer implements SlimeAudience, ILoadoutHolder, IRuleReadable, IRuleWritable {

    @Setter private Game game;

    @Nullable @Setter private GameLobby lobby;

    private final Map<Rule<?>, Object> rules;

    /** If the player dies when they have 0 or fewer lives, they cannot respawn. Anything below 0 means the player is dead. */
    @Setter protected int lives = 0;
    @Setter protected int score = 0;

    private Loadout loadout;

    private IPlayerState state;

    protected GameSpawn currentSpawn; //Spawnpoint this player spawned from last
    @Setter protected GameTeam gameTeam;

    public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        this.rules = Collections.synchronizedMap(new HashMap<>());
    }

    public static GamePlayer fromPlayer(Player player) {
        return (GamePlayer) player;
    }

    @Override
    public void setState(IPlayerState state) {
        GameManager gameManager = this.game.getGameManager();
        GameInfo gameInfo = gameManager.getGameInfo();

        var event = new GamePlayerStateChangeEvent(this.game, this, this.state, state);
        EventDispatcher.call(event);

        state = event.getNewState();
        this.state = state;

        var gamemode = state.getGamemode();
        if(gamemode != null)
            setGameMode(gamemode);

        TeamStyle teamStyle = gameInfo.getTeamStyle();

        var showTeam = state.showTeam();
        if(showTeam == TriState.TRUE && this.gameTeam != null) {
            if(teamStyle == TeamStyle.SPECIFIED) {
                NameplateHandler.addProvider(this, this.gameTeam.getNameplateProvider());
                NameplateHandler.reloadPlayer(this);
            } else if(teamStyle == TeamStyle.ANONYMOUS) {
                MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> player.setTeam(null));

                Collection<String> selfAllMembers = this.gameTeam.getPlayers().stream()
                        .map(Player::getUsername)
                        .toList();

                TeamManager teamManager = this.game.getTeamManager();

                Map<String, GameTeam> otherTeams = new HashMap<>(teamManager.getTeams());
                otherTeams.remove(this.gameTeam.getId());

                Collection<String> otherAllMembers = new LinkedList<>();
                for(Map.Entry<String, GameTeam> ent : otherTeams.entrySet()) {
                    GameTeam enemyTeam = ent.getValue();
                    Collection<String> otherMembers = enemyTeam.getPlayers().stream()
                            .map(Player::getUsername)
                            .toList();

                    otherAllMembers.addAll(otherMembers);
                }

                TeamsPacket selfCreatePacket = TEAM_MANAGER.getTeam("anonymous-self").createTeamsCreationPacket();
                TeamsPacket othersCreatePacket = TEAM_MANAGER.getTeam("anonymous-others").createTeamsCreationPacket();

                TeamsPacket selfPacket = new TeamsPacket("anonymous-self", new TeamsPacket.AddEntitiesToTeamAction(selfAllMembers));
                TeamsPacket othersPacket = new TeamsPacket("anonymous-others", new TeamsPacket.AddEntitiesToTeamAction(otherAllMembers));

                sendPackets(selfCreatePacket, othersCreatePacket,
                        selfPacket, othersPacket);
            }
        } else if(showTeam == TriState.FALSE) {
            NameplateHandler.removeProvider(this, ProviderType.SLIME);
            NameplateHandler.reloadPlayer(this);
        }

        RuleSet ruleSet = state.getRuleSet();
        if(ruleSet != null)
            ruleSet.applyFor(this, state.getRulesApplyStrategy(), state.getRulesAffectChildren());
    }

    public final void setCurrentSpawn(@NotNull GameSpawn spawn) {
        this.currentSpawn = spawn;
        setRespawnPoint(this.currentSpawn.getPosition());
    }

    public final boolean isAlive() {
        return this.lives < 0;
    }

    @Override
    public void setBoundingBox(double x, double y, double z) {
        //Doesn't change the actual bounding box, just the one used for picking up items
        //Changed as Minestom's one is too small
        super.setBoundingBox(x, y, z);
        this.expandedBoundingBox = getBoundingBox().expand(2, 1, 2);
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

    //Rules
    @Override
    public <T> void setRule(Rule<T> rule, T value, ApplyStrategy strategy, boolean affectChildren) {
        if(rule instanceof PlayerRule) {
            if(strategy == ApplyStrategy.ALWAYS) {
                this.rules.put(rule, value);
            } else if(strategy == ApplyStrategy.NOT_SET) {
                this.rules.putIfAbsent(rule, value);
            }
            return;
        } else if(rule instanceof UniversalRule) {
            if(strategy == ApplyStrategy.ALWAYS) {
                this.rules.put(rule, value);
            } else if(strategy == ApplyStrategy.NOT_SET) {
                this.rules.putIfAbsent(rule, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rule<T>, T> T getRule(R rule) {
        return (T) this.rules.get(rule);
    }
}
