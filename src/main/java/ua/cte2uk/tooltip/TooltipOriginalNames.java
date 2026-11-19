package ua.cte2uk.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import ua.cte2uk.Ukrainian;
import ua.cte2uk.mixin.vanilla.ClientTextTooltipAccessor;
import ua.cte2uk.text.OriginalNames;

/**
 * Коли затиснуто Shift, під перекладеною назвою вміння, ефекту чи класу в будь-якій підказці
 * з'являється сірий рядок з англійським оригіналом — щоб назву можна було знайти в англійській вікі.
 */
public final class TooltipOriginalNames {
    private TooltipOriginalNames() {
    }

    public static List<ClientTooltipComponent> withOriginal(List<ClientTooltipComponent> lines) {
        if (lines == null || lines.isEmpty() || !Screen.hasShiftDown() || !Ukrainian.active()) {
            return lines;
        }
        Language language = Language.getInstance();
        Map<String, String> ukToEn = OriginalNames.byUkrainian(language,
            key -> language.has(key) ? language.getOrDefault(key) : null);
        if (ukToEn.isEmpty()) {
            return lines;
        }

        List<String> texts = new ArrayList<>(OriginalNames.TITLE_LINES);
        for (int i = 0; i < Math.min(lines.size(), OriginalNames.TITLE_LINES); i++) {
            texts.add(lines.get(i) instanceof ClientTextTooltip text
                ? plain(((ClientTextTooltipAccessor) text).cte2uk$getText())
                : null);
        }
        int title = OriginalNames.findTitle(texts, ukToEn);
        if (title < 0) {
            return lines;
        }

        String english = ukToEn.get(texts.get(title).trim());
        List<ClientTooltipComponent> result = new ArrayList<>(lines.size() + 1);
        result.addAll(lines);
        result.add(title + 1, ClientTooltipComponent.create(
            Component.literal(english).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC).getVisualOrderText()));
        return result;
    }

    private static String plain(FormattedCharSequence sequence) {
        StringBuilder sb = new StringBuilder();
        sequence.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });
        return sb.toString();
    }
}
