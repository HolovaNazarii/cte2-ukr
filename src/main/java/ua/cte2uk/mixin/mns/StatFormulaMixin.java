package ua.cte2uk.mixin.mns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ua.cte2uk.Lang;

/** Рядок «Formula: Stat Percent = …» у підказці стату записаний літералом. */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.database.data.stats.tooltips.NormalStatTooltip", remap = false)
public abstract class StatFormulaMixin {
    @ModifyArg(method = "getTooltipList", require = 0, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/chat/Component;m_237113_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private String cte2uk$formula(String text) {
        if (text == null || !text.startsWith("Formula: Stat Percent")) {
            return text;
        }
        String translated = Lang.get("cte2_uk_runtime.stat_percent_formula");
        return translated != null ? translated : text;
    }
}
