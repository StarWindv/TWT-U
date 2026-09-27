package twtu.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twtu.content.purity.WaterPurity;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceEntity {
    @Inject(method = "canBurn", at = @At("HEAD"), cancellable = true)
    private static void canBurnPredict(RegistryAccess registryAccess, Recipe<?> recipe, NonNullList<ItemStack> items, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        if (items.get(0).isEmpty() || recipe == null) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack result = recipe.getResultItem(registryAccess);
        if (result.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack output = items.get(2);
        if (output.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }

        ItemStack expected = WaterPurity.applyCookingUpgrade(items.get(0), result, 2);
        if (!ItemStack.isSameItemSameTags(output, expected)) {
            cir.setReturnValue(false);
            return;
        }

        int count = output.getCount();
        boolean ok = count < maxStackSize && count < output.getMaxStackSize();
        cir.setReturnValue(ok || count < expected.getMaxStackSize());
    }

    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER))
    private static void upgradePurity(RegistryAccess registryAccess, Recipe<?> recipe, NonNullList<ItemStack> items, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        ItemStack upgraded = WaterPurity.applyCookingUpgrade(items.get(0), items.get(2), 2);
        if (upgraded != items.get(2))
            items.set(2, upgraded);
    }
}
