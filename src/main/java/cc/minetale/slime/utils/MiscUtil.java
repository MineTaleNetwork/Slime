package cc.minetale.slime.utils;

import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.rule.PlayerRule;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.EquipmentHandler;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class MiscUtil {

    public static String toString(Pos pos) {
        return "X: " + pos.x() + " Y: " + pos.y() + " Z: " + pos.z() + " Yaw: " + pos.yaw() + " Pitch: " + pos.pitch();
    }

    public static String toString(Vec vec) {
        return "X: " + vec.x() + " Y: " + vec.y() + " Z: " + vec.z();
    }

    /**
     * Creates a {@linkplain Pos} from the given arguments if all (position and rotation [yaw &amp; pitch]) are optional.
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

    public static void dropItemsByDeath(GamePlayer player) {
        var instance = player.getInstance();
        if (instance == null) { return; }

        var settings = player.getRuleOrDefault(PlayerRule.DROP_ITEMS_ON_DEATH);

        var inventory = player.getInventory();

        ItemStack[] arr = inventory.getItemStacks();
        List<ItemStack> list = new ArrayList<>(List.of(arr));

        for(var itemStack : list) {
            var info = settings.modifyDropInfo(player, new PlayerRule.DeathDropInfo(itemStack, list));

            var itemEntity = settings.getDropItemEntity(player, info);
            itemEntity.setInstance(instance, settings.getDropPosition(player, info));
            itemEntity.setVelocity(settings.getDropVelocity(player, info));
            itemEntity.setPickupDelay(settings.getDropPickupDelay(player, info));
        }
    }

    public static int getIndexOfItemStack(@NotNull AbstractInventory inventory, @NotNull ItemStack is) {
        var items = inventory.getItemStacks();
        for(int i = 0; i < items.length; i++) {
            var invIs = items[i];
            if(Objects.equals(is, invIs))
                return i;
        }

        return -1;
    }

}
