package ua.cte2uk.mixin.mns;

import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import ua.cte2uk.Lang;

/** Зілля Mine and Slash — «відновлює X% Health/Mana», ресурс передається англійським словом. */
@Pseudo
@Mixin(targets = "com.robertx22.mine_and_slash.vanilla_mc.items.SlashPotionItem", remap = false)
public abstract class PotionResourceMixin {
    // m_7373_ = Item.appendHoverText(...)
    @ModifyArg(method = "m_7373_", require = 0, at = @At(value = "INVOKE",
        target = "Lcom/robertx22/mine_and_slash/uncommon/localization/Itemtips;locName([Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private Object[] cte2uk$translatedResource(Object[] args) {
        if (args == null || args.length != 2 || !(args[1] instanceof String resource)) {
            return args;
        }
        String translated = Lang.get("cte2_uk.potion.resource." + resource.toLowerCase(Locale.ROOT));
        if (translated == null) {
            return args;
        }
        Object[] copy = java.util.Arrays.copyOf(args, args.length); // не args.clone(): Mixin 0.8.5 падає на методах масивів
        copy[1] = translated;
        return copy;
    }
}
