package twtu.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import twtu.TWTU;
import twtu.foundation.common.capability.IThirst;
import twtu.foundation.common.capability.PlayerThirstStorage;
import twtu.foundation.config.ClientConfig;

public class ThirstBarRenderer
{
    public static IThirst PLAYER_THIRST = null;
    public static ResourceLocation THIRST_ICONS = new ResourceLocation(TWTU.MOD_ID, "textures/gui/thirst_icons.png");

    public static final ResourceLocation MC_ICONS = new ResourceLocation("textures/gui/icons.png");
    public static Boolean cancelRender = false;
    static Minecraft minecraft = Minecraft.getInstance();
    protected final static RandomSource random = RandomSource.create();

    public static void init()
    {
        HudRenderCallback.EVENT.register((guiGraphics, renderTickCounter) -> {
            boolean isMounted = minecraft.player.getVehicle() instanceof LivingEntity;
            cancelRender = false;
            if (!isMounted && !minecraft.options.hideGui && minecraft.player.isAlive() && shouldDrawSurvivalElements())
            {
                if(minecraft.player.isAlive() && PlayerThirstStorage.get(minecraft.player) != null && !PlayerThirstStorage.get(minecraft.player).getShouldTickThirst()){
                    cancelRender = true;
                    return;
                }
                render(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight(), guiGraphics);
            }
        });
    }

    private static boolean shouldDrawSurvivalElements() {
        return minecraft.gameMode != null && !minecraft.gameMode.hasInfiniteItems();
    }

    public static void render(int width, int height, GuiGraphics guiGraphics)
    {
        minecraft.getProfiler().push("thirst");
        if (PLAYER_THIRST == null || minecraft.player.tickCount % 40 == 0)
        {
            PLAYER_THIRST = PlayerThirstStorage.get(minecraft.player);
        }

        if (PLAYER_THIRST == null) {
            minecraft.getProfiler().pop();
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, THIRST_ICONS);
        int left = width / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET;
        int top = height - 48 + ClientConfig.THIRST_BAR_Y_OFFSET;
        boolean unused = false;

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

            guiGraphics.blit(THIRST_ICONS, x, y, 0, 0, 9, 9, 25, 9);

            if (idx < level)
                guiGraphics.blit(THIRST_ICONS, x, y, 16, 0, 9, 9, 25, 9);
            else if (idx == level)
                guiGraphics.blit(THIRST_ICONS, x, y, 8, 0, 9, 9, 25, 9);
        }
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, MC_ICONS);

        minecraft.getProfiler().pop();
    }
}




