package cc.minetale.slime.utils;

import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;

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

}
