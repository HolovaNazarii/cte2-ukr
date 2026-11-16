package ua.cte2uk;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModList;

/**
 * Спільні перевірки «чи вмикати виправлення».
 */
public final class Ukrainian {
    public static final String LANG = "uk_ua";

    private Ukrainian() {
    }

    /** Чи вибрана зараз українська мова. */
    public static boolean active() {
        Minecraft mc = Minecraft.getInstance();
        return mc != null && LANG.equals(mc.getLanguageManager().getSelected());
    }

    /** Версія завантаженого мода або порожній рядок, якщо його немає. */
    public static String modVersion(String modId) {
        return ModList.get().getModContainerById(modId)
            .map(c -> c.getModInfo().getVersion().toString())
            .orElse("");
    }
}
