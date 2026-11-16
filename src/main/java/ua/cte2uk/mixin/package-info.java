/**
 * Міксини. Кожен новий клас додайте в {@code cte2_uk.mixins.json} → "client".
 *
 * Шаблон для класу іншого мода (його не треба мати при компіляції):
 * <pre>
 * &#64;Mixin(targets = "com.example.SomeScreen", remap = false)
 * public abstract class SomeScreenMixin {
 *     &#64;Inject(method = "someMethod", at = &#64;At("RETURN"), cancellable = true, require = 0)
 *     private void cte2uk$fix(CallbackInfoReturnable&lt;String&gt; cir) {
 *         if (!Ukrainian.active()) return;
 *         ...
 *     }
 * }
 * </pre>
 * Для класів самого Minecraft пишіть &#64;Mixin(SomeVanillaClass.class) без remap = false.
 */
package ua.cte2uk.mixin;
