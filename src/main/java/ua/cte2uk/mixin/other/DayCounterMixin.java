package ua.cte2uk.mixin.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.Lang;
import ua.cte2uk.text.DayCaption;

/** Exile Overlay — анімація «— DAY —» на початку нового дня. */
@Pseudo
@Mixin(targets = "com.example.exile_overlay.client.render.DayCounterRenderer", remap = false)
public abstract class DayCounterMixin {
    @Inject(method = "getAnimationText(I)Ljava/lang/String;", at = @At("RETURN"), cancellable = true, require = 0)
    private void cte2uk$ukrainianDay(int ticks, CallbackInfoReturnable<String> cir) {
        String translated = DayCaption.translate(cir.getReturnValue(), Lang::get);
        if (translated != null) {
            cir.setReturnValue(translated);
        }
    }
}
