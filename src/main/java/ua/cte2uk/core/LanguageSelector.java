package ua.cte2uk.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import ua.cte2uk.Cte2Uk;
import ua.cte2uk.Ukrainian;

/**
 * Одноразовий вибір української мови.
 */
public final class LanguageSelector {
    private LanguageSelector() {
    }

    /** Викликати в головному потоці клієнта. */
    public static void selectIfRequested() {
        if (!ClientConfig.SELECT_UKRAINIAN.get()) {
            return;
        }

        // Список мов на цьому етапі ще не завантажений, тому наявність uk_ua не перевіряємо:
        // це стандартна мова Minecraft, вона є завжди.
        Minecraft mc = Minecraft.getInstance();
        LanguageManager languages = mc.getLanguageManager();

        // Прапорець скидаємо в будь-якому разі: вибір робиться один раз.
        ClientConfig.SELECT_UKRAINIAN.set(false);
        ClientConfig.SELECT_UKRAINIAN.save();

        if (Ukrainian.LANG.equals(languages.getSelected())) {
            return;
        }

        Cte2Uk.LOG.info("Switching language from {} to {}", languages.getSelected(), Ukrainian.LANG);
        languages.setSelected(Ukrainian.LANG);
        mc.options.languageCode = Ukrainian.LANG;
        mc.options.save();
        mc.reloadResourcePacks();
    }
}
