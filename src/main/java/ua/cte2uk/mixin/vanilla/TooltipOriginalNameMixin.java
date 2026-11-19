package ua.cte2uk.mixin.vanilla;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ua.cte2uk.tooltip.TooltipOriginalNames;

/** Усі підказки малюються через renderTooltipInternal — тут додаємо англійський оригінал назви (Shift). */
@Mixin(GuiGraphics.class)
public abstract class TooltipOriginalNameMixin {
    @ModifyVariable(method = "renderTooltipInternal", at = @At("HEAD"), argsOnly = true, require = 0)
    private List<ClientTooltipComponent> cte2uk$originalName(List<ClientTooltipComponent> lines) {
        return TooltipOriginalNames.withOriginal(lines);
    }
}
