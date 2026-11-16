package ua.cte2uk.mixin.mns;

import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.Lang;
import ua.cte2uk.Ukrainian;

/** Службова інформація про стат («Id:» …) — через ключі {@code cte2_uk.literal.mmorpg.*}. */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.gui.screens.stat_gui.StatInfoButton$StatInfoType$7", remap = false)
public abstract class StatDebugLabelsMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"), require = 0)
    private void cte2uk$translateLabels(CallbackInfoReturnable<List<MutableComponent>> cir) {
        List<MutableComponent> lines = cir.getReturnValue();
        if (lines != null && Ukrainian.active()) {
            try {
                Lang.LITERALS.translateAll(lines, "mmorpg", c -> c);
            } catch (UnsupportedOperationException immutable) {
                // список незмінний — залишаємо як є
            }
        }
    }
}
