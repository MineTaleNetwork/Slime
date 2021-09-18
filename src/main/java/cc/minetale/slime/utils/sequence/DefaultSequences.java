package cc.minetale.slime.utils.sequence;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class DefaultSequences {

    public static final Title.@NotNull Times INSTANT = Title.Times.of(Duration.ZERO, Ticks.duration(20), Ticks.duration(10));

    public static final SequenceBuilder LOBBY_SEQUENCE = new SequenceBuilder(15000)
            .animateExperienceBar(true)

            //Chat countdown
            .chat(15000, getCountdownComponent("15", true))
            .chat(10000, getCountdownComponent("10", true))
            .chatRepeat(5000, 2000, 1000, getCountdownComponent("%d", true))
            .chat(1000, getCountdownComponent("1", false))

            //Title last 3 seconds countdown
            .title(3000, Title.title(Component.text("3", MC.CC.RED.getTextColor(), TextDecoration.BOLD), Component.empty(), INSTANT))
            .title(2000, Title.title(Component.text("2", MC.CC.YELLOW.getTextColor(), TextDecoration.BOLD), Component.empty(), INSTANT))
            .title(1000, Title.title(Component.text("1", MC.CC.WHITE.getTextColor(), TextDecoration.BOLD), Component.empty(), INSTANT));

    @NotNull private static TextComponent getCountdownComponent(String count, boolean plural) {
        return Component.text("» ", MC.CC.YELLOW.getTextColor(), TextDecoration.BOLD)
                .append(Component.text("Starting in ", MC.CC.GOLD.getTextColor()))
                .append(Component.text(count, MC.CC.YELLOW.getTextColor()))
                .append(Component.text(plural ? " seconds..." : " second...", MC.CC.GOLD.getTextColor()));
    }

}
