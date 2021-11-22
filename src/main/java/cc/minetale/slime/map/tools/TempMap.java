package cc.minetale.slime.map.tools;

import cc.minetale.slime.Slime;
import cc.minetale.slime.map.GameMap;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.instance.InstanceContainer;

import java.util.concurrent.CompletableFuture;

/**
 * Temporary Map used for building purposes.
 */
public class TempMap {

    @Getter private GameMap handle;

    @Getter private InstanceContainer instance;

    @Getter private boolean isInDatabase;
    @Getter @Accessors(fluent = true) private boolean hasChanged;

    private TempMap(GameMap handle, boolean isInDatabase) {
        this.handle = handle;

        this.isInDatabase = isInDatabase;

        this.instance = Slime.INSTANCE_MANAGER.createInstanceContainer();
        if(isInDatabase)
            this.handle.setForInstance(this.instance);

        var playArea = handle.getPlayArea();
        playArea.setInstance(this.instance);
    }

    public static TempMap ofMap(GameMap map, boolean isInDatabase) {
        return new TempMap(map, isInDatabase);
    }

    public UpdateResult saveSettings() {
        return GameMap.getCollection().replaceOne(this.handle.getFilter(), this.handle.toDocument(), new ReplaceOptions().upsert(true));
    }

    //TODO Use SeaweedFS in production
    public CompletableFuture<Boolean> saveBlocks() {
        var playArea = this.handle.getPlayArea();
        var path = this.handle.getFilePath();
        return playArea.save(path);
    }

}
