package ua.cte2uk.mixin.mns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ua.cte2uk.Lang;

/** Прапорець «Search Tooltips» у вікі Mine and Slash. */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.gui.wiki.reworked.NewWikiScreen", remap = false)
public abstract class WikiSearchLabelMixin {
    // m_7856_ = Screen.init(), m_237113_ = Component.literal(String)
    @ModifyArg(method = "m_7856_", require = 0, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/chat/Component;m_237113_(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private String cte2uk$searchTooltipsLabel(String text) {
        if (!"Search Tooltips".equals(text)) {
            return text;
        }
        String translated = Lang.get("cte2_uk_runtime.wiki_search_tooltips");
        return translated != null ? translated : text;
    }
}
