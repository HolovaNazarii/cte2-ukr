package ua.cte2uk.tooltip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class LiteralTranslatorTest {
    private static final Map<String, String> LANG = Map.of(
        "cte2_uk.literal.*.Set Bonus:", "Бонус комплекту:",
        "cte2_uk.literal.mimi.Inverted:", "Інверсія сигналу:",
        "cte2_uk.literal.mimi.Yes", "Так",
        "cte2_uk.literal.seadwellers.When in Water:", "У воді:",
        "cte2_uk.literal.*.+80% Intelligence", "+80% інтелекту");

    private final LiteralTranslator translator = new LiteralTranslator(LANG::get);

    @Test
    void wholeLineKeepsCodesSpacesAndStyle() {
        Component line = Component.literal("§7When in Water: ").withStyle(ChatFormatting.ITALIC);
        Component out = translator.translate(line, "seadwellers");
        assertEquals("§7У воді: ", out.getString());
        assertEquals(line.getStyle(), out.getStyle());
    }

    @Test
    void globalKeyWorksForAnyMod() {
        assertEquals("Бонус комплекту:", translator.translate(Component.literal("  Set Bonus:"), "anything").getString().trim());
        assertEquals("+80% інтелекту", translator.translate(Component.literal("+80% Intelligence"), "x").getString());
    }

    @Test
    void scopedKeyIsIgnoredForOtherMods() {
        assertNull(translator.translate(Component.literal("When in Water:"), "minecraft"));
    }

    @Test
    void translatesFragmentsOfComposedLine() {
        Component line = Component.literal("Inverted: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Yes").withStyle(ChatFormatting.GREEN))
            .append(Component.translatable("some.key"));
        Component out = translator.translate(line, "mimi");
        assertEquals("Інверсія сигналу: Такsome.key", out.getString());
        assertEquals(ChatFormatting.GREEN.getColor(), out.getSiblings().get(0).getStyle().getColor().getValue());
    }

    @Test
    void unknownTextIsLeftAlone() {
        List<Component> lines = new ArrayList<>(List.of(Component.literal("Something else")));
        Component before = lines.get(0);
        translator.translateAll(lines, "mimi", c -> c);
        assertEquals(before, lines.get(0));
        assertNull(translator.translate(Component.literal("§7  "), "mimi"));
    }
}
