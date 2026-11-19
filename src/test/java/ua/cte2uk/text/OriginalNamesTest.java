package ua.cte2uk.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OriginalNamesTest {
    private static final Map<String, String> ORIGINALS = Map.of(
        "mmorpg.spell.lightning_spear", "Lightning Spear",
        "mmorpg.effect.lightning_spear", "Lightning Spear",
        "mmorpg.spell.bolt", "Bolt",
        "mmorpg.spell.cold_id", "Cold ID",
        "mmorpg.spell.a", "Alpha",
        "mmorpg.spell.b", "Beta");

    private static final Map<String, String> UK = Map.of(
        "mmorpg.spell.lightning_spear", "Спис блискавки",
        "mmorpg.effect.lightning_spear", "Спис блискавки",
        "mmorpg.spell.bolt", "Снаряд",
        "mmorpg.spell.cold_id", "Cold ID",
        "mmorpg.spell.a", "Однаково",
        "mmorpg.spell.b", "Однаково");

    @Test
    void reverseMapsTranslationsToOriginals() {
        Map<String, String> reverse = OriginalNames.reverse(ORIGINALS, UK::get);
        assertEquals("Lightning Spear", reverse.get("Спис блискавки"));
        assertEquals("Bolt", reverse.get("Снаряд"));
        assertFalse(reverse.containsKey("Cold ID"), "неперекладене не потрібне");
        assertFalse(reverse.containsKey("Однаково"), "неоднозначний переклад пропускаємо");
    }

    @Test
    void findsTitleAmongFirstLines() {
        Map<String, String> reverse = OriginalNames.reverse(ORIGINALS, UK::get);
        assertEquals(0, OriginalNames.findTitle(List.of("Спис блискавки", "Кидає спис"), reverse));
        assertEquals(1, OriginalNames.findTitle(Arrays.asList("+1 Рівень вміння", " Снаряд ", "x"), reverse));
        assertEquals(-1, OriginalNames.findTitle(List.of("a", "b", "c", "d", "Снаряд"), reverse));
        assertTrue(OriginalNames.findTitle(Arrays.asList(null, "Снаряд"), reverse) == 1);
    }
}
