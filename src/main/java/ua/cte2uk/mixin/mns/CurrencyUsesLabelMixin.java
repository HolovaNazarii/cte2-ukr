package ua.cte2uk.mixin.mns;

import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.Lang;

/**
 * Orbs of Crafting — назва лічильника використань («Seed Uses», «Level Up» …).
 * Мод будує її з id через заміну «_» на пробіли й великі літери; ми повертаємо id назад і беремо переклад.
 */
@Pseudo
@Mixin(targets = "com.robertx22.addons.orbs_of_crafting.currency.reworked.item_req.custom.MaximumUsesReq$Data", remap = false)
public abstract class CurrencyUsesLabelMixin {
    @Inject(method = "label()Ljava/lang/String;", at = @At("RETURN"), cancellable = true, require = 0)
    private void cte2uk$localizedLabel(CallbackInfoReturnable<String> cir) {
        String label = cir.getReturnValue();
        if (label == null) {
            return;
        }
        String id = label.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        String translated = Lang.get("cte2_uk_runtime.currency_uses." + id);
        if (translated != null) {
            cir.setReturnValue(translated);
        }
    }
}
