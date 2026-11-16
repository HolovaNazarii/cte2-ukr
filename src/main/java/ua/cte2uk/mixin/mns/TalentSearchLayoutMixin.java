package ua.cte2uk.mixin.mns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ua.cte2uk.Ukrainian;

/**
 * У дереві талантів поле пошуку стоїть у верхній панелі (висота 22) між «Очки талантів»
 * і «Скидання». Українські підписи довші, тож усе налазить одне на одне — переносимо поле під панель.
 */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.gui.screens.skill_tree.SkillTreeScreen", remap = false)
public abstract class TalentSearchLayoutMixin {
    /** Панель малюється з y = 0 і має висоту 22; поле ставимо на 2 пікселі нижче. */
    @Unique
    private static final int CTE2UK$SEARCH_Y = 24;

    @Unique
    private int cte2uk$searchShift;

    // m_253211_ = AbstractWidget.setY(int)
    @ModifyArg(method = "renderPanels", require = 0, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/components/EditBox;m_253211_(I)V"))
    private int cte2uk$searchBelowPanel(int y) {
        cte2uk$searchShift = Ukrainian.active() ? CTE2UK$SEARCH_Y - y : 0;
        return y + cte2uk$searchShift;
    }

    @ModifyArg(method = "renderPanels", require = 0, at = @At(value = "INVOKE",
        target = "Lcom/robertx22/mine_and_slash/gui/screens/skill_tree/component/TipsWidget;m_253211_(I)V"))
    private int cte2uk$tipsFollowSearch(int y) {
        return y + cte2uk$searchShift;
    }
}
