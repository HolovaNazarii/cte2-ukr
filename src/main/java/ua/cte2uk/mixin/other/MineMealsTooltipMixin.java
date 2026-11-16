package ua.cte2uk.mixin.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.Lang;

/** Mine and Meals має власні таблиці перекладу лише англійською та японською. */
@Pseudo
@Mixin(targets = "com.baketsu.mine_and_meals.events.TooltipHandler", remap = false)
public abstract class MineMealsTooltipMixin {
    @Inject(method = "getTranslation(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
        at = @At("RETURN"), cancellable = true, require = 0)
    private static void cte2uk$ukrainianMealCaption(String tag, String language, CallbackInfoReturnable<String> cir) {
        String translated = Lang.get("cte2_uk_runtime.meal." + tag);
        if (translated != null) {
            cir.setReturnValue(translated);
        }
    }
}
