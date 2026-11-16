package ua.cte2uk.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ModifierLabelTest {
    @Test
    void movesNameFirstAndExplainsKind() {
        assertEquals("§bШкода§r: §a+5% §7(збільшення)", ModifierLabel.reorder("§a+5% §bЗбільшено §bШкода"));
        assertEquals("Мана§r: -10% §7(зменшення)", ModifierLabel.reorder("-10% Зменшено Мана"));
        assertEquals("Шкода від вогню§r: +25.5% §7(більше, множник)", ModifierLabel.reorder("+25.5% Більше Шкода від вогню"));
        assertEquals("Шкода§r: +1.2k% §7(менше, множник)", ModifierLabel.reorder("+1.2k% Менше Шкода"));
    }

    @Test
    void handlesLabelWithoutNumber() {
        assertEquals("§6Шкода§r §7(збільшення)", ModifierLabel.reorder("§6Збільшено Шкода"));
    }

    @Test
    void leavesOtherLabelsAlone() {
        assertNull(ModifierLabel.reorder("+5 Сила"));
        assertNull(ModifierLabel.reorder("+5% Шкода"));
        assertNull(ModifierLabel.reorder("Збільшено"));
        assertNull(ModifierLabel.reorder(null));
    }
}
