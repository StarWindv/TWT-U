package twtu.foundation.gui.appleskin;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import twtu.TWTU;
import twtu.api.ThirstHelper;
import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.ClientConfig;
import twtu.foundation.gui.ThirstBarRenderer;

import java.util.Random;
import java.util.Vector;

public class HUDOverlayHandler {
    private static float unclampedFlashAlpha = 0.0F;
    private static float flashAlpha = 0.0F;
    private static byte alphaDir = 1;
    protected static int foodIconsOffset;
    public static final Vector<twtu.foundation.gui.appleskin.IntPoint> foodBarOffsets = new Vector<>();
    private static final Random random = new Random();
    private static final ResourceLocation modIcons;
    static ResourceLocation THIRST_LEVEL_ELEMENT;

    public static void init() {
        HudRenderCallback.EVENT.register((guiGraphics, renderTickCounter) -> {
            Minecraft mc = Minecraft.getInstance();
            boolean isMounted = mc.player.getVehicle() instanceof LivingEntity;
            boolean isAlive = mc.player.isAlive();

            if (isAlive && !isMounted && !mc.options.hideGui && mc.player.isAlive() && !ThirstBarRenderer.cancelRender) {
                renderThirstOverlay(guiGraphics);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            unclampedFlashAlpha += alphaDir * 0.125f;
            if (unclampedFlashAlpha >= 1.5f) {
                alphaDir = -1;
            } else if (unclampedFlashAlpha <= -0.5f) {
                alphaDir = 1;
            }
            flashAlpha = Math.max(0F, Math.min(1F, unclampedFlashAlpha)) * 0.65f;
        });
    }

    public static void renderThirstOverlay(GuiGraphics guiGraphics)
    {
        if (!shouldRenderAnyOverlays())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;
        IThirst thirstData = PlayerThirstStorage.get(player);
        if (thirstData == null) return;

        int top = mc.getWindow().getGuiScaledHeight() - foodIconsOffset + ClientConfig.THIRST_BAR_Y_OFFSET;
        int right = mc.getWindow().getGuiScaledWidth() / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET;

        generateHungerBarOffsets(top, right, mc.gui.getGuiTicks(), player);

        drawSaturationOverlay(0, thirstData.getQuenched(), guiGraphics , right, top, 1f);

        ItemStack heldItem = player.getMainHandItem();
        if (!ThirstHelper.itemRestoresThirst(heldItem))
            heldItem = player.getOffhandItem();

        boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && ThirstHelper.itemRestoresThirst(heldItem);
        if (!shouldRenderHeldItemValues)
        {
            resetFlash();
            return;
        }

        ThirstValues thirstValues = new ThirstValues(ThirstHelper.getThirst(heldItem), ThirstHelper.getQuenched(heldItem));

        int drinkThirst = thirstValues.thirst;

        if(thirstData.getThirst() < 20)
            drawHungerOverlay(drinkThirst, thirstData.getThirst(), guiGraphics, right, top, flashAlpha);
        if(!ThirstHelper.isFood(heldItem) || player.getFoodData().getFoodLevel() < 20)
            drawSaturationOverlay(thirstValues.quenchedModifier, thirstData.getQuenched(),guiGraphics, right, top, flashAlpha);
    }

    public static void drawSaturationOverlay(float saturationGained, float saturationLevel, GuiGraphics guiGraphics, int right, int top, float alpha)
    {
        if (saturationLevel + saturationGained < 0)
            return;

        enableAlpha(alpha);
        RenderSystem.setShaderTexture(0, modIcons);

        float modifiedSaturation = Math.max(0, Math.min(saturationLevel + saturationGained, 20));

        int startSaturationBar = 0;
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0F);

        if (saturationGained != 0)
            startSaturationBar = (int) Math.max(saturationLevel / 2.0F, 0);

        int iconSize = 9;

        for (int i = startSaturationBar; i < endSaturationBar; ++i)
        {
            twtu.foundation.gui.appleskin.IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            int v = 0;
            int u = 0;

            float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;

            if (effectiveSaturationOfBar >= 1)
                u = 3 * iconSize;
            else if (effectiveSaturationOfBar > .5)
                u = 2 * iconSize;
            else if (effectiveSaturationOfBar > .25)
                u = iconSize;

            guiGraphics.blit(modIcons, x, y, u, v, iconSize, iconSize);
        }

        RenderSystem.setShaderTexture(0, ThirstBarRenderer.MC_ICONS);
        disableAlpha();
    }

    public static void drawHungerOverlay(int hungerRestored, int foodLevel, GuiGraphics guiGraphics, int right, int top, float alpha)
    {
        if (hungerRestored <= 0)
            return;

        enableAlpha(alpha);
        RenderSystem.setShaderTexture(0, ThirstBarRenderer.THIRST_ICONS);

        int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));

        int startFoodBars = Math.max(0, foodLevel / 2);
        int endFoodBars = (int) Math.ceil(modifiedFood / 2.0F);

        int iconStartOffset = 8 -3;
        int iconSize = 9;

        for (int i = startFoodBars; i < endFoodBars; ++i)
        {
            twtu.foundation.gui.appleskin.IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            int v = 3 * iconSize;
            int u = iconStartOffset + 4 * iconSize;

            if (i * 2 + 1 == modifiedFood)
                u -= iconSize -1;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            guiGraphics.blit(ThirstBarRenderer.THIRST_ICONS, x, y, u, v, iconSize, iconSize, 25, 9);
        }

        disableAlpha();
    }

    public static void enableAlpha(float alpha)
    {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.defaultBlendFunc();
    }

    public static void disableAlpha()
    {
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void resetFlash()
    {
        unclampedFlashAlpha = flashAlpha = 0f;
        alphaDir = 1;
    }

    private static boolean shouldRenderAnyOverlays()
    {
        return true;
    }

    private static void generateHungerBarOffsets(int top, int right, int ticks, Player player)
    {
        final int preferFoodBars = 10;

        boolean shouldAnimatedFood;

        IThirst thirstData = PlayerThirstStorage.get(player);
        if (thirstData == null) return;

        float quenched = thirstData.getQuenched();
        int thirst = thirstData.getThirst();
        shouldAnimatedFood = quenched <= 0.0F && ticks % (thirst * 3 + 1) == 0;

        if (foodBarOffsets.size() != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars);

        for (int i = 0; i < preferFoodBars; ++i)
        {
            int x = right - i * 8 - 9;
            int y = top;

            if (shouldAnimatedFood)
                y += random.nextInt(3) - 1;

            twtu.foundation.gui.appleskin.IntPoint point = foodBarOffsets.get(i);
            if (point == null)
            {
                point = new twtu.foundation.gui.appleskin.IntPoint();
                foodBarOffsets.set(i, point);
            }

            point.x = x - right;
            point.y = y - top;
        }
    }

    static {
        modIcons = new ResourceLocation(TWTU.MOD_ID, "textures/gui/appleskin_icons.png");
        THIRST_LEVEL_ELEMENT = new ResourceLocation(TWTU.MOD_ID, "thirst_level");
    }
}




