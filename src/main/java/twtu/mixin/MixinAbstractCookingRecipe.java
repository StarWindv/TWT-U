package twtu.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import twtu.content.purity.WaterPurity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractCookingRecipe.class)
public class MixinAbstractCookingRecipe {

    @Shadow
    private ItemStack result;

    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean filterWaterContainers(Ingredient ingredient, ItemStack stack, Operation<Boolean> original) {
        if (!isWaterRecipeOutput()) {
            return original.call(ingredient, stack);
        }
        if (isWaterBottle(stack) || WaterPurity.isWaterFilledContainer(stack)) {
            return original.call(ingredient, stack);
        }
        return false;
    }

    @Unique
    private boolean isWaterRecipeOutput() {
        return result.is(Items.POTION) || WaterPurity.isWaterFilledContainer(result);
    }

    @Unique
    private boolean isWaterBottle(ItemStack stack) {
        return stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER;
    }
}




