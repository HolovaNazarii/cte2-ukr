package ua.cte2uk.mixin.mns;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ua.cte2uk.Lang;

/** «Forces Soul to Produce %s» — тип броні підставляється англійським тегом. */
@Pseudo
@Mixin(targets = "com.robertx22.addons.orbs_of_crafting.currency.reworked.item_mod.soul.ForceGearSlotSoulMod", remap = false)
public abstract class SoulGearTagMixin {
    @ModifyArg(method = "getDescWithParams", require = 0, at = @At(value = "INVOKE",
        target = "Lcom/robertx22/library_of_exile/localization/ExileTranslation;getTranslatedName([Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private Object[] cte2uk$translatedGearTag(Object[] args) {
        if (args == null || args.length == 0 || !(args[0] instanceof String tag)) {
            return args;
        }
        String key = "mmorpg.tag.gear_slot." + tag;
        if (!Lang.has(key)) {
            return args;
        }
        Object[] copy = java.util.Arrays.copyOf(args, args.length); // не args.clone(): Mixin 0.8.5 падає на методах масивів
        copy[0] = Component.translatable(key);
        return copy;
    }
}
