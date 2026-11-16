package ua.cte2uk.core;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Клієнтський конфіг: {@code config/cte2_uk-client.toml}.
 */
public final class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue SELECT_UKRAINIAN;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SELECT_UKRAINIAN = builder
            .comment(
                "Перемкнути мову гри на українську під час запуску.",
                "Після перемикання мод сам ставить false, тож далі мову можна змінювати як завжди.",
                "Поставте true, щоб мод знову вибрав українську під час наступного запуску.")
            .define("selectUkrainianOnStartup", true);
        SPEC = builder.build();
    }

    private ClientConfig() {
    }
}
