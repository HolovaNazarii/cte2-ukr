package ua.cte2uk.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DayCaptionTest {
    private static final Map<String, String> LANG = Map.of(
        "cte2_uk.day_counter.d", "Д",
        "cte2_uk.day_counter.da", "ДЕ",
        "cte2_uk.day_counter.day", "ДЕНЬ");

    @Test
    void translatesAnimationFrames() {
        assertEquals("— Д —", DayCaption.translate("— D —", LANG::get));
        assertEquals("— ДЕ —", DayCaption.translate("— DA —", LANG::get));
        assertEquals("— ДЕНЬ —", DayCaption.translate("— DAY —", LANG::get));
        assertEquals("— ДЕНЬ 12 —", DayCaption.translate("— DAY 12 —", LANG::get));
    }

    @Test
    void ignoresOtherText() {
        assertNull(DayCaption.translate("— NIGHT —", LANG::get));
        assertNull(DayCaption.translate("DAYS", LANG::get));
        assertNull(DayCaption.translate(null, LANG::get));
        assertNull(DayCaption.translate("— DAY —", k -> null));
    }
}
