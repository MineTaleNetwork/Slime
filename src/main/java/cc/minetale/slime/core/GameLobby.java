package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.loadout.DefaultLoadouts;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.utils.InstanceUtil;
import cc.minetale.slime.utils.sequence.DefaultSequences;
import cc.minetale.slime.utils.sequence.Sequence;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

/**
 * As to avoid confusion. There is only one instance of {@linkplain GameLobby}, <br>
 * but players are separated "logically" into their respective games.
 */
public class GameLobby implements ForwardingAudience {

    public static final GameExtension ACTIVE_GAME = Slime.getActiveGame();

    @Getter private final Instance instance;
    @Getter private final Game game;

    @Getter protected List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    private Sequence countdown;

    public GameLobby(Game game) {
        this.instance = INSTANCE_MANAGER.createSharedInstance(ACTIVE_GAME.getLobbyInstance());
        this.game = game;
    }

    public boolean addPlayer(GamePlayer player) {
        if(player.getLobby() != null || isPlayerInLobby(player)) { return false; }

        if(this.players.contains(player)) { return false; }
        this.players.add(player);
        player.setLobby(this);
        applyLoadout(player);

        startCountdown();

        player.setRespawnPoint(new Pos(0, 64, 0)); //TODO Use GameMap's spawnpoint

        return true;
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getLobby() != this) { return false; }

        player.setLobby(null);
        Loadout.removeIfAny(player);

        if(!this.players.remove(player)) { return false; }

        pauseCountdown();

        return true;
    }

    public boolean isPlayerInLobby(GamePlayer player) {
        return this.players.contains(player);
    }

    public void kickAll() {
        this.players.forEach(Player::remove);
    }

    public void moveAll(Instance newInstance, Pos pos) {
        this.players.forEach(player -> player.setInstance(newInstance, pos));
    }

    protected void startCountdown() {
        if(this.players.size() < Slime.getActiveGame().getMaxPlayers()) { return; }

        var state = this.game.getState();
        if(state.getStage() == Stage.STARTING) { return; }

        state.setStage(Stage.STARTING);

        if(this.countdown != null && this.countdown.isPaused()) {
            this.countdown.resume();
            return;
        }

        this.countdown = DefaultSequences.LOBBY_SEQUENCE
                .onFinish(involved -> this.game.start())
                .build();

        this.countdown.addInvolved(this.players);
        this.countdown.start();
    }

    /** Pauses the countdown, hardcoded the message because that's usually why it happens. */
    public void pauseCountdown() {
        if(this.countdown == null) { return; }
        if(this.players.size() >= Slime.getActiveGame().getMinPlayers() || this.countdown.isPaused()) { return; }

        this.countdown.pause();
        this.countdown.getInvolved().forEach(obj -> {
            if(!(obj instanceof GamePlayer player)) { return; }
            player.sendMessage(Component.text().append(
                    Component.text("» ", NamedTextColor.WHITE),
                    Component.text("Stopping the countdown, because there aren't enough players!", NamedTextColor.RED))
            );
        });

        this.game.getState().setStage(Stage.IN_LOBBY);
    }

    public void resumeCountdown() {
        this.countdown.resume();
    }

    private void applyLoadout(Player player) {
        DefaultLoadouts.LOBBY.forceApplyFor(player);
    }

    public final void remove() {
        InstanceUtil.unregisterSafe(this.instance);
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.players;
    }

}
