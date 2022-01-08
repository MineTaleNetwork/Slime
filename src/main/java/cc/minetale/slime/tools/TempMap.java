package cc.minetale.slime.tools;

import cc.minetale.buildingtools.Selection;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.LobbyMap;
import cc.minetale.slime.utils.Requirement;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.instance.InstanceContainer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;
import static cc.minetale.slime.Slime.TOOL_MANAGER;

/**
 * Temporary Map used for building purposes.
 */
public class TempMap {

    @Getter private AbstractMap handle;
    @Getter private GameExtension game;

    @Getter private InstanceContainer instance;

    @Getter private Selection area;

    @Getter private boolean isInDatabase;
    @Getter @Accessors(fluent = true) private boolean hasChanged;

    private TempMap(AbstractMap handle, GameExtension game, boolean isInDatabase) {
        this.handle = handle;
        this.game = game;

        this.instance = INSTANCE_MANAGER.createInstanceContainer();
        if(isInDatabase)
            this.handle.setForInstance(this.instance);

        this.area = new Selection(handle.getMinPos(), handle.getMaxPos(), this.instance);

        this.isInDatabase = isInDatabase;
    }

    public static TempMap ofMap(AbstractMap map, GameExtension game, boolean isInDatabase) {
        return new TempMap(map, game, isInDatabase);
    }

    public static TempMap ofMap(AbstractMap map, boolean isInDatabase) {
        var oGame = TOOL_MANAGER.getGame(map.getGamemode());
        if(oGame.isEmpty()) { return null; }
        var game = oGame.get();

        return new TempMap(map, game, isInDatabase);
    }

    public UpdateResult saveSettings() {
        MongoCollection<Document> collection;
        if(this.handle instanceof GameMap) {
            collection = GameMap.getCollection();
        } else if(this.handle instanceof LobbyMap) {
            collection = LobbyMap.getCollection();
        } else {
            return null;
        }
        return collection.replaceOne(this.handle.getFilter(), this.handle.toDocument(), new ReplaceOptions().upsert(true));
    }

    //TODO Use SeaweedFS in production
    public CompletableFuture<Boolean> saveBlocks() {
        var path = this.handle.getFilePath();
        return this.area.save(path);
    }

    public List<Requirement<TempMap>> getUnsatisfiedRequirements() {
        List<Requirement<TempMap>> failedRequirements = new ArrayList<>();
        for(Requirement<TempMap> requirement : this.game.getMapRequirements()) {
            if(!requirement.doesMeetRequirement(this)) {
                failedRequirements.add(requirement);
            }
        }
        return failedRequirements;
    }

}
