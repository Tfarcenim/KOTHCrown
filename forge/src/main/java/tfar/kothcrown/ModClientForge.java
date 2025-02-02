package tfar.kothcrown;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.kothcrown.client.KingOfTheHillScreen;
import tfar.kothcrown.client.ThroneScreen;

public class ModClientForge {
    static void init(IEventBus bus) {
        bus.addListener(ModClientForge::setup);
    }

    static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(Init.THRONE_MENU, ThroneScreen::new);
        MenuScreens.register(Init.KING_OF_THE_HILL_MENU, KingOfTheHillScreen::new);
    }

}
