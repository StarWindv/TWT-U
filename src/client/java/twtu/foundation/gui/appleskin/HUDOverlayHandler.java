package twtu.foundation.gui.appleskin;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
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
    private static final Identifier modIcons;
    static Identifier THIRST_LEVEL_ELEMENT;

    public static void init() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.FOOD_BAR,
                Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "thirst_overlay"), HUDOverlayHandler::renderElement);

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

    private static void renderElement(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker)
    {
        Minecraft mc = Minecraft.getInstance();
        boolean isMounted = mc.player.getVehicle() instanceof LivingEntity;
        boolean isAlive = mc.player.isAlive();

        if (isAlive && !isMounted && !mc.gui.hud.isHidden() && mc.player.isAlive() && !ThirstBarRenderer.cancelRender) {
            renderThirstOverlay(guiGraphics);
        }
    }

    public static void renderThirstOverlay(GuiGraphicsExtractor guiGraphics)
    {
        if (!shouldRenderAnyOverlays())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;
        IThirst thirstData = PlayerThirstStorage.get(player);
        if (thirstData == null) return;

        int height = mc.getWindow().getGuiScaledHeight();
        int width = mc.getWindow().getGuiScaledWidth();
        boolean airBarVisible = player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)
                || player.getAirSupply() < player.getMaxAirSupply();
        int top = height - (airBarVisible ? 59 : 48) + ClientConfig.THIRST_BAR_Y_OFFSET;
        int right = width / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET;

        generateHungerBarOffsets(top, right, player.tickCount, player);

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

    public static void drawSaturationOverlay(float saturationGained, float saturationLevel, GuiGraphicsExtractor guiGraphics, int right, int top, float alpha)
    {
        if (saturationLevel + saturationGained < 0)
            return;

        float modifiedSaturation = Math.max(0, Math.min(saturationLevel + saturationGained, 20));

        int startSaturationBar = 0;
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0F);

        if (saturationGained != 0)
            startSaturationBar = (int) Math.max(saturationLevel / 2.0F, 0);

        int iconSize = 9;
        int color = ((int) (alpha * 255.0F)) << 24 | 0xFFFFFF;

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

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, modIcons, x, y, u, v, iconSize, iconSize, 256, 256, color);
        }
    }

    public static void drawHungerOverlay(int hungerRestored, int foodLevel, GuiGraphicsExtractor guiGraphics, int right, int top, float alpha)
    {
        if (hungerRestored <= 0)
            return;

        int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));

        int startFoodBars = Math.max(0, foodLevel / 2);
        int endFoodBars = (int) Math.ceil(modifiedFood / 2.0F);

        int iconStartOffset = 8 -3;
        int iconSize = 9;

        int color = ((int) (alpha * 255.0F)) << 24 | 0xFFFFFF;

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

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ThirstBarRenderer.THIRST_ICONS, x, y, u, v, iconSize, iconSize, 25, 9, color);
        }
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
        modIcons = Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "textures/gui/appleskin_icons.png");
        THIRST_LEVEL_ELEMENT = Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "thirst_level");
    }
}
