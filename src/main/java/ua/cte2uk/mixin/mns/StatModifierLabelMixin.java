package ua.cte2uk.mixin.mns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.Ukrainian;
import ua.cte2uk.text.ModifierLabel;

/** «+5% Збільшено Шкода» → «Шкода: +5% (збільшення)». */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.database.data.stats.name_regex.StatNameRegex", remap = false)
public abstract class StatModifierLabelMixin {
    @Inject(method = "translate", at = @At("RETURN"), cancellable = true, require = 0)
    private void cte2uk$reorderModifier(CallbackInfoReturnable<String> cir) {
        if (!Ukrainian.active()) {
            return;
        }
        String reordered = ModifierLabel.reorder(cir.getReturnValue());
        if (reordered != null) {
            cir.setReturnValue(reordered);
        }
    }
}
