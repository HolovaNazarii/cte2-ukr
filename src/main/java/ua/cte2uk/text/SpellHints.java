package ua.cte2uk.text;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ua.cte2uk.Lang;

/** Підказка «Утримуйте [Shift]…» у заклинань зайва, коли Shift уже затиснуто. */
public final class SpellHints {
    private static final List<String> SHIFT_HINT_KEYS = List.of("mmorpg.item_tips.shift_tip", "mmorpg.word.shift_to_show_effect");

    private SpellHints() {
    }

    public static void removeShiftHints(List<Component> lines) {
        if (lines == null || !Screen.hasShiftDown()) {
            return;
        }
        for (String key : SHIFT_HINT_KEYS) {
            String hint = Lang.get(key);
            if (hint == null) {
                continue;
            }
            String text = hint.trim();
            try {
                lines.removeIf(line -> line != null && line.getString().trim().equals(text));
            } catch (UnsupportedOperationException immutable) {
                return;
            }
        }
    }
}
