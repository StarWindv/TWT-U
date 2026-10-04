package twtu.mixin;

import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.CommonConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class MixinFoodData
{
    @Shadow
    public abstract void addExhaustion(float p_38704_);

    @Shadow private float exhaustionLevel;
    @Unique
    private int dehydratedHealTimer = 0;


    @Redirect(
            method = {"tick"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 0)
    )
    private void healWithSaturation(ServerPlayer player, float amount)
    {
        IThirst thirstData = PlayerThirstStorage.get(player);
        if(thirstData == null)
            return;
        FoodData foodData = player.getFoodData();

        float f = Math.min(foodData.getSaturationLevel(), 6.0F);

        boolean shouldHeal = !CommonConfig.DEHYDRATION_HALTS_HEALTH_REGEN || thirstData.getThirst() >= 20;

        if(shouldHeal)
        {
            player.heal(f / 6.0F);
            thirstData.setJustHealed();
            return;
        }

        dehydratedHealTimer++;
        if(dehydratedHealTimer >= 8 && thirstData.getThirst() > 18)
        {
            player.heal(f / 6.0F);
            thirstData.setJustHealed();
            dehydratedHealTimer = 0;
            return;
        }

        this.addExhaustion(-f);
    }

    @Redirect(
            method = {"tick"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 1)
    )
    private void healWithHunger(ServerPlayer player, float amount)
    {
        IThirst thirstData = PlayerThirstStorage.get(player);
        if(thirstData == null)
            return;
        boolean shouldHeal = !CommonConfig.DEHYDRATION_HALTS_HEALTH_REGEN || thirstData.getThirst() > 18;

        if(shouldHeal)
        {
            player.heal(1.0F);
            thirstData.setJustHealed();
        }
        else
            this.addExhaustion(-6.0F);
    }

    @Inject(method = "tick",at = @At(value = "HEAD"))
    private void DealWithExhaustionBySaturation(ServerPlayer player, CallbackInfo ci){
        if(exhaustionLevel>4.0F){
            IThirst thirstData = PlayerThirstStorage.get(player);
            if (thirstData != null) {
                thirstData.ExhaustionRecalculate();
            }
        }
    }
}
