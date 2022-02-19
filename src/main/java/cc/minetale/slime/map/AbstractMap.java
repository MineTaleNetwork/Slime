package cc.minetale.slime.map;

import cc.minetale.magma.MagmaLoader;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.utils.MapUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractMap {

    public abstract String getId();

    public abstract String getName();
    public abstract void setName(String name);

    public abstract String getGamemode();

    public abstract NamespaceID getDimensionID();
    public abstract void setDimension(NamespaceID id);

    public abstract DimensionType getDimension();

    public abstract Vec getMinPos();
    public abstract Vec getMaxPos();

    public abstract boolean isOpen();

    public abstract UpdateResult setStatus(boolean isOpen);

    public abstract Path getFilePath();

    //TODO Use SeaweedFS in production
    public CompletableFuture<Void> setForInstance(InstanceContainer instance) {
        return MagmaLoader.create(getFilePath()).thenAccept(instance::setChunkLoader);
    }

    protected abstract void load(Document document);
    public abstract Document toDocument();

    public Bson getFilter() {
        return MapUtil.getFilter(getGamemode(), getId());
    }

    @AllArgsConstructor
    public enum Type {
        GAME("Map", "map",
                GameInfo::getGameMapProvider,
                GameInfo::getGameMapResolver),

        LOBBY("Lobby", "lobby",
                GameInfo::getLobbyMapProvider,
                GameInfo::getLobbyMapResolver);

        @Getter private final String pascalcase;
        @Getter private final String lowercase;

        //TODO This is kinda cursed?
        private final Function<GameInfo, MapProvider<? extends AbstractMap>> provider;
        private final Function<GameInfo, MapResolver<? extends AbstractMap>> resolver;

        public <T extends AbstractMap> MapProvider<T> getProvider(GameInfo game) {
            return (MapProvider<T>) this.provider.apply(game);
        }

        public <T extends AbstractMap> MapResolver<T> getResolver(GameInfo game) {
            return (MapResolver<T>) this.resolver.apply(game);
        }

    }

}
