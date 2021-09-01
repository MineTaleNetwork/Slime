package cc.minetale.slime.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class GamePlayer<P extends GamePlayer<P,S,G>, S extends GameState<S,P,G>, G extends Game<G,P,S>> {

    @Setter(AccessLevel.PACKAGE)
    private G game;

    @Setter(AccessLevel.PACKAGE)
    private Player handle;

    @Nullable private GameLobby<G,P,S> lobby;

    @Getter @Setter protected int lives = 1;
    @Getter @Setter protected boolean canLoseLives = true;

    @SuppressWarnings("unchecked")
    public P get() {
        return (P) this;
    }

}
