package ua.cte2uk.mixin.mns;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ua.cte2uk.Lang;

/** Кнопка групи фільтрів у вікі показує порожній підпис, коли фільтр не вибрано. */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.gui.wiki.reworked.filters.FilterGroupButton", remap = false)
public abstract class WikiFilterLabelMixin {
    private static final String KEY = "cte2_uk_runtime.wiki_choose_filter";

    // m_6035_ = AbstractWidget.getMessage()
    @Inject(method = "m_6035_", at = @At("RETURN"), cancellable = true, require = 0)
    private void cte2uk$emptyFilterLabel(CallbackInfoReturnable<Component> cir) {
        Component label = cir.getReturnValue();
        if ((label == null || label.getString().isBlank()) && Lang.has(KEY)) {
            cir.setReturnValue(Component.translatable(KEY));
        }
    }
}
