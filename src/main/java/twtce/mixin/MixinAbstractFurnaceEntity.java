package twtce.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twtce.content.purity.WaterPurity;
import twtce.foundation.config.CommonConfig;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceEntity {
    @Redirect(method = "canBurn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean canBurn(ItemStack remainItem, ItemStack recipeResult) {
        return ItemStack.isSameItemSameTags(remainItem, recipeResult);
    }

    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER))
    private static void upgradePurity(RegistryAccess registryAccess, Recipe<?> recipe, NonNullList<ItemStack> items, int maxStackSize, CallbackInfoReturnable<Boolean> cir) {
        ItemStack input = items.get(0);
        ItemStack result = items.get(2);

        if (WaterPurity.isWaterFilledContainer(input) || isWaterBottle(input)) {
            int inputPurity = CommonConfig.DEFAULT_PURITY;
            if (input.hasTag() && input.getTag().contains("Purity")) {
                inputPurity = input.getTag().getInt("Purity");
            }

            int newPurity = Math.min(inputPurity + 2, WaterPurity.MAX_PURITY);

            if (isWaterBottle(result) || isPlainPotion(result)) {
                ItemStack upgraded = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                CompoundTag tag = upgraded.getOrCreateTag();
                tag.putInt("Purity", newPurity);
                items.set(2, upgraded);
            } else if (WaterPurity.isWaterFilledContainer(result)) {
                CompoundTag tag = result.getOrCreateTag();
                tag.putInt("Purity", newPurity);
            }
        }
    }

    @Unique
    private static boolean isWaterBottle(ItemStack stack) {
        return stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER;
    }

    @Unique
    private static boolean isPlainPotion(ItemStack stack) {
        return stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.EMPTY;
    }
}
