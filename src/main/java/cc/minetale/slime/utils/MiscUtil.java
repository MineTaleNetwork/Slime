package cc.minetale.slime.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.item.ItemStack;

import java.util.List;
import java.util.Map;

@UtilityClass
public class MiscUtil {

    public static String toString(Pos pos) {
        return "X: " + pos.x() + " Y: " + pos.y() + " Z: " + pos.z() + " Yaw: " + pos.yaw() + " Pitch: " + pos.pitch();
    }

    public static String toString(Vec vec) {
        return "X: " + vec.x() + " Y: " + vec.y() + " Z: " + vec.z();
    }

    /**
     * Creates a {@linkplain Pos} from the given arguments if all (position and rotation [yaw & pitch]) are optional.
     * If an argument is present/provided then it'll use the value from it, otherwise it'll use the player's value (like Y value or pitch).
     */
    public static Pos getOptionalPosition(CommandContext context, Entity executor,
                                          ArgumentRelativeVec3 posArg, ArgumentFloat yawArg, ArgumentFloat pitchArg) {

        var builderPos = executor.getPosition();

        var vec = context.has(posArg) ? context.get(posArg).from(executor) : builderPos.asVec();
        var yaw = context.getOrDefault(yawArg, builderPos.yaw());
        var pitch = context.getOrDefault(pitchArg, builderPos.pitch());

        return vec.asPosition().withView(yaw, pitch);
    }

    public static Component getInformationMessage(String title, Map<String, Component> info) {
        var builder = Component.text()
                .append(Component.text(title));

        for(Map.Entry<String, Component> ent : info.entrySet()) {
            builder.append(Component.newline(),
                    Component.text(ent.getKey() + ": ", NamedTextColor.GRAY),
                    ent.getValue().colorIfAbsent(NamedTextColor.WHITE));
        }

        return builder.build();
    }

    public static List<ItemStack> getEquipment(EquipmentHandler handler) {
        return List.of(handler.getItemInMainHand(),
                handler.getItemInOffHand(),
                handler.getBoots(),
                handler.getLeggings(),
                handler.getChestplate(),
                handler.getHelmet());
    }

}
