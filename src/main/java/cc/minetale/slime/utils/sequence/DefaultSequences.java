package cc.minetale.slime.utils.sequence;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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

            //Dingdingding
            .soundRepeat(5000, 1000, 1000,
                    Sound.sound(Key.key("block.note_block.pling"), Sound.Source.RECORD, 1f, 1f), Sound.Emitter.self(), null)
            .sound(0,
                    Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.RECORD, 1f, 1f), Sound.Emitter.self(), null)

            //Title last 3 seconds countdown
            .title(3000, getCountdownTitle("3"))
            .title(2000, getCountdownTitle("2"))
            .title(1000, getCountdownTitle("1"));

    @NotNull private static Title getCountdownTitle(String count) {
        return Title.title(Component.text(count, MC.CC.GOLD.getTextColor(), TextDecoration.BOLD), Component.empty(), INSTANT);
    }

    @NotNull private static TextComponent getCountdownComponent(String count, boolean plural) {
        return Component.text("» ", MC.CC.YELLOW.getTextColor())
                .append(Component.text("Starting in ", MC.CC.GOLD.getTextColor()))
                .append(Component.text(count, MC.CC.YELLOW.getTextColor()))
                .append(Component.text(plural ? " seconds..." : " second...", MC.CC.GOLD.getTextColor()));
    }

}
