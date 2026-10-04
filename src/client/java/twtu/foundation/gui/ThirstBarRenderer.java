package twtu.foundation.gui;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import twtu.TWTU;
import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.ClientConfig;

public class ThirstBarRenderer
{
    public static IThirst PLAYER_THIRST = null;
    public static Identifier THIRST_ICONS = Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "textures/gui/thirst_icons.png");

    public static final Identifier MC_ICONS = Identifier.withDefaultNamespace("textures/gui/icons.png");
    public static Boolean cancelRender = false;
    static Minecraft minecraft = Minecraft.getInstance();
    protected final static RandomSource random = RandomSource.create();

    public static void init()
    {
        HudElementRegistry.attachElementAfter(VanillaHudElements.FOOD_BAR, Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "thirst_bar"), ThirstBarRenderer::renderElement);
    }

    private static void renderElement(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker)
    {
        boolean isMounted = minecraft.player.getVehicle() instanceof LivingEntity;
        cancelRender = false;
        if (!isMounted && !minecraft.gui.hud.isHidden() && minecraft.player.isAlive() && shouldDrawSurvivalElements())
        {
            if(minecraft.player.isAlive() && PlayerThirstStorage.get(minecraft.player) != null && !PlayerThirstStorage.get(minecraft.player).getShouldTickThirst()){
                cancelRender = true;
                return;
            }
            render(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight(), guiGraphics);
        }
    }

    private static boolean shouldDrawSurvivalElements() {
        return minecraft.player == null || !minecraft.player.hasInfiniteMaterials();
    }

    public static void render(int width, int height, GuiGraphicsExtractor guiGraphics)
    {
        if (PLAYER_THIRST == null || minecraft.player.tickCount % 40 == 0)
        {
            PLAYER_THIRST = PlayerThirstStorage.get(minecraft.player);
        }

        if (PLAYER_THIRST == null) {
            return;
        }

        int left = width / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET;
        boolean airBarVisible = minecraft.player.isEyeInFluid(FluidTags.WATER)
                || minecraft.player.getAirSupply() < minecraft.player.getMaxAirSupply();
        int top = height - (airBarVisible ? 59 : 48) + ClientConfig.THIRST_BAR_Y_OFFSET;

        int level = PLAYER_THIRST.getThirst();

        for (int i = 0; i < 10; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            if (PLAYER_THIRST.getQuenched() <= 0.0F && minecraft.player.tickCount % (level * 3 + 1) == 0)
            {
                y = top + (random.nextInt(3) - 1);
            }

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, THIRST_ICONS, x, y, 0, 0, 9, 9, 25, 9);

            if (idx < level)
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, THIRST_ICONS, x, y, 16, 0, 9, 9, 25, 9);
            else if (idx == level)
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, THIRST_ICONS, x, y, 8, 0, 9, 9, 25, 9);
        }
    }
}
