package ua.cte2uk.mixin.vanilla;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Доступ до тексту рядка підказки. */
@Mixin(ClientTextTooltip.class)
public interface ClientTextTooltipAccessor {
    @Accessor("text")
    FormattedCharSequence cte2uk$getText();
}
