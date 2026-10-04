package twtu.mixin;

import twtu.content.purity.WaterPurity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SingleItemRecipe.class)
public class MixinSingleItemRecipe {

    @Shadow
    @Final
    private ItemStackTemplate result;

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/world/level/Level;)Z",
            at = @At("HEAD"), cancellable = true)
    private void filterWaterContainers(SingleRecipeInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (!(((Object) this) instanceof AbstractCookingRecipe)) {
            return;
        }
        if (!isWaterRecipeOutput()) {
            return;
        }
        ItemStack stack = input.item();
        if (isWaterBottle(stack) || WaterPurity.isWaterFilledContainer(stack)) {
            if (WaterPurity.getPurity(stack) >= WaterPurity.MAX_PURITY) {
                cir.setReturnValue(false);
            }
            // otherwise let the vanilla ingredient test decide
            return;
        }
        cir.setReturnValue(false);
    }

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"), cancellable = true)
    private void upgradeAssembledResult(SingleRecipeInput input, CallbackInfoReturnable<ItemStack> cir) {
        if (!(((Object) this) instanceof AbstractCookingRecipe)) return;
        ItemStack inputStack = input.item();
        if (inputStack.isEmpty()) return;
        ItemStack resultStack = cir.getReturnValue();
        if (resultStack == null || resultStack.isEmpty()) return;
        int bonus = ((Object) this instanceof CampfireCookingRecipe) ? 1 : 2;
        cir.setReturnValue(WaterPurity.applyCookingUpgrade(inputStack, resultStack, bonus));
    }

    @Unique
    private boolean isWaterRecipeOutput() {
        ItemStack resultStack = result.create();
        return resultStack.is(Items.POTION) || WaterPurity.isWaterFilledContainer(resultStack);
    }

    @Unique
    private boolean isWaterBottle(ItemStack stack) {
        return stack.is(Items.POTION)
                && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
    }
}
