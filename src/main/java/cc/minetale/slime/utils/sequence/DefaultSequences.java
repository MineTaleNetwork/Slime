package cc.minetale.slime.utils.sequence;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;

public final class DefaultSequences {

    public static final SequenceBuilder LOBBY_SEQUENCE = new SequenceBuilder(15000)
            .animateExperienceBar(true)
            .chat(15000,
                    Component.text("» ", MC.CC.YELLOW.getTextColor(), TextDecoration.BOLD)
                            .append(Component.text("Starting in ", MC.CC.GOLD.getTextColor()))
                            .append(Component.text("15", MC.CC.YELLOW.getTextColor()))
                            .append(Component.text(" seconds...", MC.CC.GOLD.getTextColor())))
            .chat(10000,
                    Component.text("» ", MC.CC.YELLOW.getTextColor(), TextDecoration.BOLD)
                            .append(Component.text("Starting in ", MC.CC.GOLD.getTextColor()))
                            .append(Component.text("10", MC.CC.YELLOW.getTextColor()))
                            .append(Component.text(" seconds...", MC.CC.GOLD.getTextColor())))
            .chatRepeat(5000, 1000, 1000,
                    Component.text("» ", MC.CC.YELLOW.getTextColor(), TextDecoration.BOLD)
                            .append(Component.text("Starting in ", MC.CC.GOLD.getTextColor()))
                            .append(Component.text("%d", MC.CC.YELLOW.getTextColor()))
                            .append(Component.text(" seconds...", MC.CC.GOLD.getTextColor())))
            .title(3000, Title.title(Component.text("3", MC.CC.RED.getTextColor(), TextDecoration.BOLD), Component.empty()))
            .title(2000, Title.title(Component.text("2", MC.CC.YELLOW.getTextColor(), TextDecoration.BOLD), Component.empty()))
            .title(1000, Title.title(Component.text("1", MC.CC.WHITE.getTextColor(), TextDecoration.BOLD), Component.empty()));

}
