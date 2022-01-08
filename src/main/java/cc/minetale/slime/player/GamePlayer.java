package cc.minetale.slime.player;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.lobby.GameLobby;
import cc.minetale.slime.rule.*;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        var event = new GamePlayerStateChangeEvent(this.game, this, this.state, state);
        EventDispatcher.call(event);

        state = event.getNewState();

        this.state = state;

        setGameMode(state.getGamemode());

        if(state.showTeam() && this.gameTeam != null) {
            NameplateHandler.addProvider(this, this.gameTeam.getNameplateProvider());
        } else {
            NameplateHandler.removeProvider(this, ProviderType.SLIME);
        }
        NameplateHandler.reloadPlayer(this);
    }

    public final void setCurrentSpawn(@NotNull GameSpawn spawn) {
        this.currentSpawn = spawn;
        setRespawnPoint(this.currentSpawn.getPosition());
    }

    public final boolean isAlive() {
        return this.lives < 0;
    }

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
    public <T> void setRule(Rule<T> rule, T value, boolean affectChildren) {
        if(rule instanceof PlayerRule) {
            this.rules.put(rule, value);
            return;
        } else if(rule instanceof UniversalRule) {
            this.rules.put(rule, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getRule(Rule<T> rule) {
        return (T) this.rules.get(rule);
    }
}
