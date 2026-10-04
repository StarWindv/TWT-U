package twtu.mixin;

import twtu.content.thirst.PlayerThirst;
import twtu.foundation.config.CommonConfig;
import twtu.api.ThirstHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces the old Player.eat / PotionItem.finishUsingItem hooks:
 * all eating and drinking flows through Consumable#onConsume in modern versions.
 */
@Mixin(Consumable.class)
public class MixinConsumable
{
    @Inject(method = "onConsume", at = @At("HEAD"))
    public void onConsume(Level level, LivingEntity user, ItemStack item, CallbackInfoReturnable<ItemStack> cir)
    {
        if(!(user instanceof Player player))
            return;

        // drinks restore thirst, not hunger nutrition
        if(!CommonConfig.ENABLE_DRINKS_NUTRITION && ThirstHelper.isDrink(item))
        {
            FoodProperties food = item.get(DataComponents.FOOD);
            if(food != null && food.nutrition() > 0)
            {
                item.set(DataComponents.FOOD, new FoodProperties(0, food.saturation(), food.canAlwaysEat()));
            }
        }

        PlayerThirst.drink(item, player);
    }
}
