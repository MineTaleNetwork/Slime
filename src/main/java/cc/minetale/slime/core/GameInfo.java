package cc.minetale.slime.core;

import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.LobbyMap;
import cc.minetale.slime.map.MapProvider;
import cc.minetale.slime.map.MapResolver;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.team.TeamProvider;
import cc.minetale.slime.tools.TempMap;
import cc.minetale.slime.utils.Requirement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.network.PlayerProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@Getter @Setter @Accessors(chain = true)
public class GameInfo {

    private final String name;
    private final String id;

    private PlayerProvider playerProvider;
    private TeamProvider teamProvider;

    private MapProvider<? extends GameMap> gameMapProvider;
    private MapResolver<? extends GameMap> gameMapResolver;

    private MapProvider<? extends LobbyMap> lobbyMapProvider;
    private MapResolver<? extends LobbyMap> lobbyMapResolver;

    //Settings for anonymous team style
    private TeamStyle teamStyle;

    private NamedTextColor anonymousSelfColor = NamedTextColor.GREEN, anonymousOthersColor = NamedTextColor.RED;

    private Component anonymousSelfPrefix = Component.text("", NamedTextColor.GREEN),
            anonymousOthersPrefix = Component.text("", NamedTextColor.RED),
            anonymousSelfSuffix = Component.empty(),
            anonymousOthersSuffix = Component.empty();

    //Settings for specified team style
    /** Only used when {@linkplain #teamStyle} is set to {@linkplain TeamStyle#SPECIFIED}. **/
    private List<ITeamType> teamTypes;

    /**
     * Requirements a {@linkplain TempMap} must meet, so it can be saved. <br>
     * You can create your own requirements if you feel like it, for example if you want zones, objectives, spawns with specific settings like IDs. <br>
     * Or use presets from {@linkplain Requirement.Map}.
     */
    private Set<Requirement<TempMap>> mapRequirements;

    private @Nullable GameManager gameManager;

    private GameInfo(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public static GameInfo create(String name, String id) {
        return new GameInfo(name, id);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameMap> MapProvider<T> getGameMapProvider() {
        return (MapProvider<T>) this.gameMapProvider;
    }

    @SuppressWarnings("unchecked")
    public <T extends GameMap> MapResolver<T> getGameMapResolver() {
        return (MapResolver<T>) this.gameMapResolver;
    }

    @SuppressWarnings("unchecked")
    public <T extends LobbyMap> MapProvider<T> getLobbyMapProvider() {
        return (MapProvider<T>) this.lobbyMapProvider;
    }

    @SuppressWarnings("unchecked")
    public <T extends LobbyMap> MapResolver<T> getLobbyMapResolver() {
        return (MapResolver<T>) this.lobbyMapResolver;
    }

}
