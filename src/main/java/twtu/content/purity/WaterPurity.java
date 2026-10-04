package twtu.content.purity;

import twtu.api.ThirstHelper;
import twtu.content.registry.ItemInit;
import twtu.foundation.common.event.RegisterThirstValueEvent;
import twtu.foundation.config.CommonConfig;
import twtu.foundation.util.MathHelper;
import twtu.foundation.util.TickHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


@SuppressWarnings("SpellCheckingInspection")
public class WaterPurity
{
    private static final List<ContainerWithPurity> waterContainers = new ArrayList<>();
    private static final List<Block> fillablesWithPurity = new ArrayList<>();
    public static final int MIN_PURITY = 0;
    public static final int MAX_PURITY = 3;

    public static final IntegerProperty BLOCK_PURITY = IntegerProperty.create("purity", 0, 4);

    public static void init()
    {
        registerContainers();
        registerFillables();
        registerFabricEvents();
    }

    private static void registerContainers()
    {
        waterContainers.add(new ContainerWithPurity(new ItemStack(Items.GLASS_BOTTLE),
                PotionContents.createItemStack(Items.POTION, Potions.WATER)).setEqualsFilled(itemStack ->
                itemStack.is(Items.POTION) && isWaterBottle(itemStack)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(ItemInit.TERRACOTTA_BOWL),
                new ItemStack(ItemInit.TERRACOTTA_WATER_BOWL)));
        waterContainers.add(new ContainerWithPurity(new ItemStack(Items.BUCKET),
                new ItemStack(Items.WATER_BUCKET), false).canHarvestRunningWater(false));
    }

    private static void registerFillables()
    {
        fillablesWithPurity.add(Blocks.CAULDRON);
        fillablesWithPurity.add(Blocks.WATER_CAULDRON);
    }

    private static void registerFabricEvents()
    {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) ->
        {
            if (player instanceof ServerPlayer && hand == InteractionHand.MAIN_HAND && isWaterFilledContainer(player.getItemInHand(hand)))
            {
                BlockPos pos = hitResult.getBlockPos();
                BlockState blockState = level.getBlockState(pos);

                if (isFillableBlock(blockState))
                {
                    int purity = getPurity(player.getItemInHand(hand));

                    int blockPurity = !blockState.hasProperty(BLOCK_PURITY) ?
                            3 : (blockState.getValue(BLOCK_PURITY) - 1 < 0 ?
                                3 : blockState.getValue(BLOCK_PURITY) - 1);

                    TickHelper.nextTick(level, () -> {
                        BlockState blockState1 = level.getBlockState(pos);

                        if(!blockState1.hasProperty(BLOCK_PURITY))
                            return;

                        level.setBlock(
                                pos,
                                blockState1.setValue(BLOCK_PURITY, Math.min(purity, blockPurity) + 1),
                                0
                        );
                    });
                }
            }
            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) ->
        {
            ItemStack item = player.getItemInHand(hand);

            if (!canHarvestRunningWater(item))
                return InteractionResult.PASS;

            BlockPos blockPos = MathHelper.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos();

            if (!level.getFluidState(blockPos).is(FluidTags.WATER))
                return InteractionResult.PASS;

            SoundEvent sound;
            ItemStack filledItem;

            if(item.getItem() == Items.GLASS_BOTTLE && !level.getFluidState(blockPos).isSource())
            {
                sound = SoundEvents.BOTTLE_FILL;
                filledItem = PotionContents.createItemStack(Items.POTION, Potions.WATER);
            }
            else if(item.getItem() == ItemInit.TERRACOTTA_BOWL)
            {
                sound = SoundEvents.BUCKET_FILL;
                filledItem = new ItemStack(ItemInit.TERRACOTTA_WATER_BOWL);
            }
            else
                return InteractionResult.PASS;

            level.playSound(player, player.getX(), player.getY(), player.getZ(), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);

            addPurity(filledItem, getBlockPurity(level, blockPos));

            ItemStack result = ItemUtils.createFilledResult(item, player, filledItem);

            player.setItemInHand(hand, result);
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        });
    }

    /**
     * Registers new custom water container
     * the container will be taken into consider of purity
     * Don't use it directly. Trying to subscribe #{@link RegisterThirstValueEvent}
     */
    @Deprecated
    @SuppressWarnings("unused")
    public static void addContainer(ContainerWithPurity container)
    {
        waterContainers.add(container);
    }

    /**
     * Returns the filled equivalent of the water container given in input.
     * The second parameter specifies if the container inputted is the empty or
     * filled version
     */
    @SuppressWarnings("unused")
    public static ItemStack getFilledContainer(ItemStack container, boolean fromFilled)
    {
        for (ContainerWithPurity waterContainer : waterContainers)
            if ((!fromFilled && waterContainer.equalsEmpty(container)) || (fromFilled && waterContainer.equalsFilled(container)))
                return waterContainer.getFilledItem().copy();

        return ItemStack.EMPTY.copy();
    }

    /**
     * Renders the client-side tooltip for items that have a water
     * purity tag
     */
    public static void renderPurityTooltip(ItemStack itemStack, List<Component> tooltip)
    {
        if(isWaterFilledContainer(itemStack))
        {
            int purity = getPurity(itemStack);
            if(purity >= MIN_PURITY && purity <= MAX_PURITY)
            {
                String purityText = getPurityText(purity);

                int purityColor = getPurityColor(purity);

                assert purityText != null;
                tooltip.add(Component.literal(purityText)
                        .setStyle(Style.EMPTY.withColor(purityColor)));
            }
        }
    }

    public static boolean isWaterFilledContainer(ItemStack item)
    {
        for (ContainerWithPurity waterContainer : waterContainers)
            if (waterContainer.equalsFilled(item))
                return true;

        return false;
    }

    public static boolean isEmptyWaterContainer(ItemStack item)
    {
        for (ContainerWithPurity waterContainer : waterContainers)
            if (waterContainer.equalsEmpty(item))
                return true;

        return false;
    }

    public static boolean isWaterBottle(ItemStack stack)
    {
        return stack.is(Items.POTION) && getPotionContents(stack).is(Potions.WATER);
    }

    public static boolean isPlainPotion(ItemStack stack)
    {
        PotionContents contents = getPotionContents(stack);
        return stack.is(Items.POTION) && contents.potion().isEmpty();
    }

    private static PotionContents getPotionContents(ItemStack stack)
    {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    /**
     * input is a water container/water bottle, returns a new stack with the
     * purity upgraded by bonus; otherwise returns result unchanged (same
     * reference, so callers can detect "no change"). Never modifies result.
     */
    public static ItemStack applyCookingUpgrade(ItemStack input, ItemStack result, int bonus)
    {
        if (!(isWaterFilledContainer(input) || isWaterBottle(input)))
            return result;

        int inputPurity = getPurity(input);
        int newPurity = Math.min(inputPurity + bonus, MAX_PURITY);

        if (isWaterBottle(result) || isPlainPotion(result))
        {
            ItemStack upgraded = PotionContents.createItemStack(Items.POTION, Potions.WATER);
            addPurity(upgraded, newPurity);
            return upgraded;
        }
        else if (isWaterFilledContainer(result))
        {
            ItemStack copy = result.copy();
            addPurity(copy, newPurity);
            return copy;
        }

        return result;
    }

    static boolean isFillableBlock(Block block)
    {
        for (Block fillable : fillablesWithPurity)
        {
            if (fillable == block)
                return  true;
        }

        return false;
    }

    static boolean isFillableBlock(BlockState blockState)
    {
        return isFillableBlock(blockState.getBlock());
    }

    static boolean canHarvestRunningWater(ItemStack item)
    {
        for (ContainerWithPurity waterContainer : waterContainers)
            if (waterContainer.equalsEmpty(item) && waterContainer.canHarvestRunningWater())
                return true;

        return false;
    }

    /**
     * Reads the purity from an item
     */
    public static int getPurity(ItemStack item)
    {
        CustomData customData = item.get(DataComponents.CUSTOM_DATA);
        if (customData == null)
            return CommonConfig.DEFAULT_PURITY;

        return customData.copyTag().getIntOr("Purity", CommonConfig.DEFAULT_PURITY);
    }

    /**
     * Returns the purity string in the language selected by the player
     */
    public static String getPurityText(int purity)
    {
        if(purity==-1) return null;
        String purityText = purity == 0 ? "dirty" :
                purity == 1 ? "slightly_dirty" :
                        purity == 2 ? "acceptable" : "purified";

        return Component.translatable("twt-u.purity." + purityText, purityText).getString();
    }

    /**
     * Returns the purity color in decimal format
     */
    public static int getPurityColor(int purity)
    {
        return purity == 0 ? 11028517 :
                purity == 1 ? 7957617 :
                purity == 2 ? 6128285 : 2208255;
    }

    /**
     * Returns the already-adjusted water purity level of a
     * block with the BLOCK_PURITY tag
     */
    public static int getBlockPurity(BlockState blockState)
    {
        return blockState.hasProperty(BLOCK_PURITY) ? blockState.getValue(BLOCK_PURITY) - 1 : -1;
    }

    public static boolean hasPurity(ItemStack item)
    {
        CustomData customData = item.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().contains("Purity");
    }

    /**
     * Shorthand for adding purity to an item if in a context where the block
     * the player is pointing at is accessible
     */
    public static ItemStack addPurity(ItemStack item, BlockPos pos, Level level)
    {
        addPurity(item, getBlockPurity(level, pos));

        return  item;
    }


    /**
     * Adds the "Purity" tag to an item
     */
    public static ItemStack addPurity(ItemStack item, int purity)
    {
        if(purity==CommonConfig.DEFAULT_PURITY)
        {
            CustomData existing = item.get(DataComponents.CUSTOM_DATA);
            if (existing != null)
            {
                CompoundTag tag = existing.copyTag();
                tag.remove("Purity");
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
        else
        {
            CustomData.update(DataComponents.CUSTOM_DATA, item, tag -> tag.putInt("Purity", purity));
        }

        return item;
    }


    /**
     * Calculates the water purity of a specific block in the level
     */
    public static int getBlockPurity(Level level, BlockPos pos)
    {
        int purity = (pos.getY() > CommonConfig.MOUNTAINS_Y || pos.getY() < CommonConfig.CAVES_Y)
                && pos.getY() < CommonConfig.MOUNTAINS_Y - 32 ? 1 : 0;

        if(level.getFluidState(pos).is(FluidTags.WATER))
        {
            if(!level.getFluidState(pos).isSource())
                purity = Math.min(purity + (int)CommonConfig.RUNNING_WATER_PURIFICATION_AMOUNT, MAX_PURITY);

            return purity;
        }
        else if(level.getBlockState(pos).is(Blocks.WATER_CAULDRON))
        {
            return level.getBlockState(pos).getValue(BLOCK_PURITY) - 1;
        }
        else
            return CommonConfig.DEFAULT_PURITY;
    }

    /**
     * Gives the player effects based on the purity of the water just drunk
     * and returns whether thirst and quenched should be added or not
     */
    public static boolean givePurityEffects(Player player, ItemStack item)
    {
        if(!isWaterFilledContainer(item)) return true;
        if(!hasPurity(item)) return true;
        return givePurityEffects(player, ThirstHelper.getPurity(item));
    }

    /**
     * Calculates purity-derived effects
     */
    public static boolean givePurityEffects(Player player, int purity)
    {
        boolean shouldRegenerate = true;
        Random random = new Random();
        float chance = random.nextFloat();

        switch (purity) {
            case 0 -> {
                if (chance < CommonConfig.DIRTY_NAUSEA_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 5, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 30, 0));
                    }

                }

                if (chance <= CommonConfig.DIRTY_POISON_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 10, 0));
                    }
                    shouldRegenerate = false;
                }

            }
            case 1 -> {
                if (chance < CommonConfig.SLIGHTLY_DIRTY_NAUSEA_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 5, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 30, 0));
                    }

                }

                if (chance <= CommonConfig.SLIGHTLY_DIRTY_POISON_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 10, 0));
                    }
                    shouldRegenerate = false;
                }

            }
            case 2 -> {
                if (chance < CommonConfig.ACCEPTABLE_NAUSEA_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 5, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 30, 0));
                    }

                }

                if (chance <= CommonConfig.ACCEPTABLE_POISON_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 10, 0));
                    }
                    shouldRegenerate = false;
                }

            }
            case 3 -> {
                if (chance < CommonConfig.PURIFIED_NAUSEA_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 20 * 5, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * 30, 0));
                    }

                }

                if (chance <= CommonConfig.PURIFIED_POISON_PERCENTAGE / 100.0f) {
                    if(player instanceof ServerPlayer)
                    {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 10, 0));
                    }
                    shouldRegenerate = false;
                }

            }
        }

        return shouldRegenerate || CommonConfig.QUENCH_THIRST_WHEN_DEBUFFED;
    }
}
