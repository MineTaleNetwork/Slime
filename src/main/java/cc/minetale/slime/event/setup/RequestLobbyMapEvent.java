package cc.minetale.slime.event.setup;

import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.map.LobbyMap;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
public class RequestLobbyMapEvent {

    private final GameInfo game;

    @Setter
    private LobbyMap map; //Proposed map by the GameManager and its current MapResolverStrategy

    public RequestLobbyMapEvent(@NotNull GameInfo game, @NotNull LobbyMap map) {
        this.game = game;
        this.map = map;
    }

}