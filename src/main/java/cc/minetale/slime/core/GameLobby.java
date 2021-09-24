package cc.minetale.slime.core;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.loadout.DefaultLoadouts;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.utils.sequence.DefaultSequences;
import cc.minetale.slime.utils.sequence.Sequence;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

/**
 * As to avoid confusion. There is only one instance of {@linkplain GameLobby}, <br>
 * but players are separated "logically" into their respective games.
 */
public class GameLobby {

    public static InstanceContainer PARENT_INSTANCE;

    @Getter private final Instance instance;
    @Getter private final Game game;

    protected Set<GamePlayer> players = Collections.synchronizedSet(new HashSet<>());

    private Sequence countdown;

    public GameLobby(Game game) {
        this.instance = INSTANCE_MANAGER.createSharedInstance(PARENT_INSTANCE);
        this.game = game;
    }

    public boolean addPlayer(GamePlayer player) {
        if(player.getLobby() != null || isPlayerInLobby(player)) { return false; }

        if(!this.players.add(player)) { return false; }

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

        return this.players.remove(player);
    }

    protected boolean startCountdown() {
        if(this.players.size() < this.game.getMaxPlayers()) { return false; }

        var state = this.game.getState();

        state.setBaseState(BaseState.STARTING);
        countdown = DefaultSequences.LOBBY_SEQUENCE
                .onFinish(involved -> this.game.start())
                .build();

        //TODO Switch to using the GamePlayer when (or if) we make it extend Player
        this.players.forEach(player -> countdown.addInvolved(player));

        countdown.start();

        return true;
    }

    public void pauseCountdown() {
        countdown.pause();
        countdown.getInvolved().forEach(obj -> {
            if(!(obj instanceof Player)) { return; }
            var player = (Player) obj;
            player.sendMessage(
                    Component.text("» ", MC.CC.WHITE.getTextColor(), TextDecoration.BOLD)
                            .append(Component.text("Stopping the countdown, because there aren't enough players!", MC.CC.RED.getTextColor())));
        });
    }

    public void resumeCountdown() {
        countdown.resume();
    }

    public boolean isPlayerInLobby(GamePlayer player) {
        return this.players.contains(player);
    }

    public void kickAll() {
        this.players.forEach(Player::remove);
    }

    private void applyLoadout(Player player) {
        DefaultLoadouts.LOBBY.forceApplyFor(player);
    }

    final void remove() {
        //TODO Unregister instance

        INSTANCE_MANAGER.unregisterInstance(this.instance);
    }

}
