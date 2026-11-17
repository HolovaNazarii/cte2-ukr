package ua.cte2uk;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import ua.cte2uk.core.ClientConfig;
import ua.cte2uk.core.LanguageSelector;
import ua.cte2uk.core.install.GameFiles;

/**
 * Точка входу мода. Окремі виправлення додаються як модулі.
 */
@Mod(Cte2Uk.MOD_ID)
public final class Cte2Uk {
    public static final String MOD_ID = "cte2_uk";
    public static final Logger LOG = LogUtils.getLogger();

    public Cte2Uk(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return; // мод лише клієнтський
        }
        // Якомога раніше, щоб мовний пакет з'явився до першого сканування ресурспаків.
        GameFiles.install();

        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        context.getModEventBus().addListener(this::onClientSetup);

        ua.cte2uk.tooltip.ItemTooltipLiterals.register();
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Змінювати мову можна лише в головному потоці.
        event.enqueueWork(LanguageSelector::selectIfRequested);
    }
}
