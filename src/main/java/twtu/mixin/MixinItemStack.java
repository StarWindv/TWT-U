package twtu.mixin;

import twtu.foundation.config.CommonConfig;
import twtu.content.purity.WaterPurity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    @Inject(method="getMaxStackSize", at = @At("HEAD"), cancellable = true)
    public void changeWaterBottleStackSize(CallbackInfoReturnable<Integer> cir)
    {
        ItemStack self = (ItemStack)(Object)this;
        if(self.getItem() == Items.POTION && WaterPurity.isWaterBottle(self))
            cir.setReturnValue(CommonConfig.WATER_BOTTLE_STACKSIZE);
    }
}
