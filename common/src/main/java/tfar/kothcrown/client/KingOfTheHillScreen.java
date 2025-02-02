package tfar.kothcrown.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import tfar.kothcrown.KingOfTheHillMenu;
import tfar.kothcrown.KothCrown;
import tfar.kothcrown.network.server.C2SSetTablePacket;
import tfar.kothcrown.platform.Services;

public class KingOfTheHillScreen extends AbstractContainerScreen<KingOfTheHillMenu> {

    private EditBox name;
    public static final ResourceLocation BACKGROUND = KothCrown.id("textures/gui/sprites/background.png");


    public KingOfTheHillScreen(KingOfTheHillMenu menu, Inventory $$1, Component title) {
        super(menu, $$1, title);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            Services.PLATFORM.sendToServer(new C2SSetTablePacket(name.getValue(),true));
            this.minecraft.player.closeContainer();
        }).pos(leftPos + 140,topPos+130).size(30,20).build());

        this.name = new EditBox(this.font, leftPos + 6, topPos + 24, 160, 12, Component.translatable("container.repair"));

        this.name.setCanLoseFocus(false);
        this.name.setTextColor(0xffffffff);
        this.name.setTextColorUneditable(0xffffffff);
        //this.name.setBordered(false);
        this.name.setMaxLength(10000);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
    }

    private void onNameChanged(String s) {
        Services.PLATFORM.sendToServer(new C2SSetTablePacket(s,false));
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }

        return this.name.keyPressed($$0, $$1, $$2) || this.name.canConsumeInput() || super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.name.getValue();
        this.init($$0, $$1, $$2);
        this.name.setValue($$3);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        name.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        name.setTextColor(menu.getColor());
    }

    @Override
    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        $$0.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        //        guiGraphics.blitNineSlicedSized(SOCIALBACKGROUND,relX,relY+83-8,imageWidth,imageHeight - 73,4,4,236,34,0,0,236,34);
        Services.PLATFORM.nineSlice(guiGraphics,BACKGROUND,leftPos,topPos,imageWidth,imageHeight,4,12,12,0,0,12,12);
    }
}
