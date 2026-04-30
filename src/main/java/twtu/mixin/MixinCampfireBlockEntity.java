package twtu.mixin;

import twtu.content.purity.WaterPurity;
import twtu.foundation.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CampfireBlockEntity.class})
public abstract class MixinCampfireBlockEntity
{
    @Shadow private NonNullList<ItemStack> items;

    @Inject(method = "cookTick", at = @At("RETURN"))
    private static void upgradePurityOnCook(Level level, BlockPos pos, BlockState blockState, CampfireBlockEntity campfire, CallbackInfo ci) {
        MixinCampfireBlockEntity self = (MixinCampfireBlockEntity) (Object) campfire;
        NonNullList<ItemStack> items = self.items;

        for (int i = 0; i < items.size(); i++) {
            ItemStack result = items.get(i);
            if (result.isEmpty()) continue;

            if (WaterPurity.isWaterFilledContainer(result) || isWaterBottle(result)) {
                if (!hasPurityTag(result)) {
                    CompoundTag tag = result.getOrCreateTag();
                    tag.putInt("Purity", CommonConfig.DEFAULT_PURITY);
                } else {
                    int currentPurity = result.getTag().getInt("Purity");
                    int newPurity = Math.min(currentPurity + 1, WaterPurity.MAX_PURITY);
                    result.getTag().putInt("Purity", newPurity);
                }
            }
        }
    }

    @Inject(
            method = {"particleTick"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void waterVapour(Level level, BlockPos pos, BlockState blockState, CampfireBlockEntity campfire, CallbackInfo ci) {
        RandomSource random = level.getRandom();
        int l = blockState.getValue(CampfireBlock.FACING).get2DDataValue();
        boolean cancel = false;

        for(int i = 0; i < campfire.getItems().size(); ++i) {
            ItemStack itemstack = campfire.getItems().get(i);
            if (WaterPurity.isWaterFilledContainer(itemstack)) {
                cancel = true;
                if (random.nextFloat() < 0.2F) {
                    Direction direction = Direction.from2DDataValue(Math.floorMod(i + l, 4));
                    final float f = 0.3125F;
                    double d0 = (double)pos.getX() + 0.5 - (double)((float)direction.getStepX() * f) + (double)((float)direction.getClockWise().getStepX() * f);
                    double d1 = (double)pos.getY() + 0.6;
                    double d2 = (double)pos.getZ() + 0.5 - (double)((float)direction.getStepZ() * f) + (double)((float)direction.getClockWise().getStepZ() * f);
                    level.addParticle(ParticleTypes.EFFECT, d0, d1, d2, 0.0, 0.001, 0.0);
                }
            }
        }

        if (cancel) {
            if (random.nextFloat() < 0.11F) {
                for(int i = 0; i < random.nextInt(2) + 2; ++i) {
                    CampfireBlock.makeParticles(level, pos, blockState.getValue(CampfireBlock.SIGNAL_FIRE), false);
                }
            }

            ci.cancel();
        }
    }

    @Unique
    private static boolean isWaterBottle(ItemStack stack) {
        return stack.is(Items.POTION) && PotionUtils.getPotion(stack) == Potions.WATER;
    }

    @Unique
    private static boolean hasPurityTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("Purity");
    }
}




