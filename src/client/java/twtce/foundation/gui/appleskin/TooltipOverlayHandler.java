package twtce.foundation.gui.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import twtce.TWTCE;
import twtce.api.ThirstHelper;
import twtce.foundation.gui.ThirstBarRenderer;

public class TooltipOverlayHandler {

    private static final ResourceLocation modIcons;

    public static void init() {
        // In Fabric, tooltip rendering is handled differently
        // This is a simplified version
    }

    static {
        modIcons = new ResourceLocation(TWTCE.MOD_ID, "textures/gui/appleskin_icons.png");
    }
}
