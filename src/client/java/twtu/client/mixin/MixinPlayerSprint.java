package twtu.client.mixin;

import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class MixinPlayerSprint{

    /**
     * @reason prevent sprinting when thirst
     * @return food level or thirst level
     */

    @Redirect(method ="hasEnoughFoodToDoExhaustiveManoeuvres", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;hasEnoughFood()Z"))
    public boolean hasEnoughThirstToStartSprinting(FoodData instance){
        boolean enoughFood = instance.hasEnoughFood();
        if(!CommonConfig.MOVE_SLOW_WHEN_THIRSTY) return enoughFood;

        if(!enoughFood){
            return false;
        }else {
            IThirst thirstData = PlayerThirstStorage.get(Minecraft.getInstance().player);
            if (thirstData != null) {
                return thirstData.getThirst() >= 6;
            }
        }
        return enoughFood;
    }
}
