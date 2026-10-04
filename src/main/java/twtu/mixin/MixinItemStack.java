package twtu.mixin;

import twtu.foundation.config.CommonConfig;
import twtu.content.purity.WaterPurity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.2 起 ItemStack 不再自己声明 getMaxStackSize()，
 * 该方法挪到了 ItemStack 实现的 ItemInstance 接口的 default 方法里，
 * 因此改为注入接口。其它实现类（如 ItemStackTemplate）由 instanceof 守卫排除。
 */
@Mixin(ItemInstance.class)
public interface MixinItemStack
{
    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    default void changeWaterBottleStackSize(CallbackInfoReturnable<Integer> cir)
    {
        if (((Object) this) instanceof ItemStack self
                && self.getItem() == Items.POTION
                && WaterPurity.isWaterBottle(self))
        {
            cir.setReturnValue(CommonConfig.WATER_BOTTLE_STACKSIZE);
        }
    }
}
