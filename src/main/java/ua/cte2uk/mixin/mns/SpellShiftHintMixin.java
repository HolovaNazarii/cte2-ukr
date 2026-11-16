package ua.cte2uk.mixin.mns;

import java.util.List;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.text.SpellHints;

/** Ховаємо «Утримуйте [Shift]…», коли Shift уже затиснуто. Логіка — у {@link SpellHints}. */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.database.data.spells.components.Spell", remap = false)
public abstract class SpellShiftHintMixin {
    @Inject(method = "GetTooltipString", at = @At("RETURN"), require = 0)
    private void cte2uk$hideShiftHint(CallbackInfoReturnable<List<Component>> cir) {
        SpellHints.removeShiftHints(cir.getReturnValue());
    }
}
