package twtu.foundation.gui.appleskin;

import net.minecraft.resources.Identifier;
import twtu.TWTU;

public class TooltipOverlayHandler {

    private static final Identifier modIcons;

    public static void init() {
        // In Fabric, tooltip rendering is handled differently
        // This is a simplified version
    }

    static {
        modIcons = Identifier.fromNamespaceAndPath(TWTU.MOD_ID, "textures/gui/appleskin_icons.png");
    }
}
