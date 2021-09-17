package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.loadout.DefaultLoadouts;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.utils.sequence.SequenceBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * As to avoid confusion. There is only one instance of {@linkplain GameLobby}, <br>
 * but players are separated "logically" into their respective games.
 */
public class GameLobby {

    public static InstanceContainer PARENT_INSTANCE;

    @Getter private final Instance instance;
    @Getter private final Game game;

    protected Set<GamePlayer> players = Collections.synchronizedSet(new HashSet<>());

    public GameLobby(Game game) {
        this.instance = Slime.INSTANCE_MANAGER.createSharedInstance(PARENT_INSTANCE);
        this.game = game;
    }

    public boolean addPlayer(GamePlayer player) {
        if(player.getLobby() != null || isPlayerInLobby(player)) { return false; }

        if(!this.players.add(player)) { return false; }

        player.setLobby(this);

        applyLoadout(player.getHandle());

        startCountdown();

        player.getHandle().setRespawnPoint(new Pos(0, 64, 0)); //TODO Use GameMap's spawnpoint

        return true;
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getLobby() != this) { return false; }
        player.setLobby(null);

        Loadout.removeIfAny(player.getHandle());

        return this.players.remove(player);
    }

    protected boolean startCountdown() {
        //TODO Add back
//        if(!(this.players.size() >= this.game.getMaxPlayers())) { return false; }

        var sequence = new SequenceBuilder(10000)
                .setExperienceBar(true)
                .chatRepeat(1000, Component.text("Starting game in: %d!"))
                .onFinish(involved -> {
                    involved.forEach(obj -> {
                        if(!(obj instanceof Player)) { return; }
                        Player player = (Player) obj;
                        player.sendMessage(Component.text("Starting!!!"));
                    });
                    //TODO Start the game
                })
                .build();

        //TODO Switch to using the GamePlayer when (or if) we make it extend Player
        this.players.forEach(gamePlayer -> sequence.addInvolved(gamePlayer.getHandle()));

        sequence.start();

        return true;
    }

    public boolean isPlayerInLobby(GamePlayer player) {
        return this.players.contains(player);
    }

    private void applyLoadout(Player player) {
        DefaultLoadouts.LOBBY.forceApplyFor(player);
    }

}
