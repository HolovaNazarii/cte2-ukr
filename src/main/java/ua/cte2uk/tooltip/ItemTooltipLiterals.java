package ua.cte2uk.tooltip;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import ua.cte2uk.Lang;
import ua.cte2uk.Ukrainian;

/**
 * Підключає {@link LiteralTranslator} до підказок предметів.
 */
public final class ItemTooltipLiterals {
    private ItemTooltipLiterals() {
    }

    public static void register() {
        // LOWEST — щоб інші моди вже додали свої рядки.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ItemTooltipLiterals::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event) {
        if (!Ukrainian.active() || event.getItemStack().isEmpty()) {
            return;
        }
        String namespace = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()).getNamespace();
        Lang.LITERALS.translateAll(event.getToolTip(), namespace, c -> c);
    }
}
