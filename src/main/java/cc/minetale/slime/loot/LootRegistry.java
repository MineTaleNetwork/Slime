package cc.minetale.slime.loot;

import cc.minetale.commonlib.CommonLib;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.Registry;
import net.minestom.server.utils.validate.Check;

import java.io.IOException;
import java.io.InputStream;

public class LootRegistry {

    public static LootTable getLootTable(TableType type) {
        var json = loadJson(type);
        try {
            return CommonLib.getJsonMapper().treeToValue(json, LootTable.class);
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonNode loadJson(TableType type) {
        try (InputStream resourceStream = Registry.class.getClassLoader().getResourceAsStream(type.getName())) {
            Check.notNull(resourceStream, "Resource {0} does not exist!", type);
            return CommonLib.getJsonMapper().readTree(resourceStream);
        } catch (IOException e) {
            MinecraftServer.getExceptionManager().handleException(e);
        }
        return null;
    }

}
