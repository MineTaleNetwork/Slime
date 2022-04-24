package cc.minetale.slime.misc.sequence;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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

            //Sounds
            .soundRepeat(5000, 1000, 1000,
                    Sound.sound(Key.key("block.note_block.pling"), Sound.Source.RECORD, 1f, 1f), Sound.Emitter.self(), null)
            .sound(0,
                    Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.RECORD, 1f, 1f), Sound.Emitter.self(), null)

            //Title countdown
            .titleRepeat(3000, 1000, 1000, getCountdownTitle("%d"));

    @NotNull private static Title getCountdownTitle(String count) {
        return Title.title(Component.text(count, NamedTextColor.GOLD, TextDecoration.BOLD), Component.empty(), INSTANT);
    }

    @NotNull private static TextComponent getCountdownComponent(String count, boolean plural) {
        return Component.text()
                .append(Component.text("\u00bb ", NamedTextColor.YELLOW),
                        Component.text("Starting in ", NamedTextColor.GOLD),
                        Component.text(count, NamedTextColor.YELLOW),
                        Component.text(plural ? " seconds..." : " second...", NamedTextColor.GOLD))
                .build();
    }

}
