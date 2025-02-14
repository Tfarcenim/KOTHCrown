package tfar.kothcrown.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.kothcrown.KingOfTheHillMenu;
import tfar.kothcrown.KothCrown;
import tfar.kothcrown.network.server.C2SSetKothPacket;
import tfar.kothcrown.platform.Services;

import java.util.function.Consumer;

public class KingOfTheHillScreen extends AbstractContainerScreen<KingOfTheHillMenu> {

    private EditBox name;
    public static final ResourceLocation BACKGROUND = KothCrown.id("textures/gui/sprites/background.png");

    private NumberEditBox delayBox;
    private NumberEditBox radiusBox;


    public KingOfTheHillScreen(KingOfTheHillMenu menu, Inventory $$1, Component title) {
        super(menu, $$1, title);
        menu.addSlotListener(new ContainerListener() {
            /**
             * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
             * contents of that slot.
             */
            public void slotChanged(AbstractContainerMenu p_97973_, int p_97974_, ItemStack p_97975_) {
            }

            public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
                switch (pDataSlotIndex) {
                    case 1 -> delayBox.setValue(pValue+"");
                    case 2 -> radiusBox.setValue(pValue+"");
                }
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {

            int delay = tryGet(delayBox.getValue());
            int radius = tryGet(radiusBox.getValue());

            updateRemote(name.getValue(),true,delay,radius);
            this.minecraft.player.closeContainer();
        }).pos(leftPos + 140,topPos+130).size(30,20).build());

        initName();
        delayBox = initNumberBox(6,49,this::onDelayChanged);
        radiusBox = initNumberBox(6,74,this::onRadiusChanged);


    }

    void initName() {
        this.name = new EditBox(this.font, leftPos + 6, topPos + 23, 160, 12, Component.translatable("container.repair"));

        this.name.setTextColor(0xffffffff);
        this.name.setTextColorUneditable(0xffffffff);
        //this.name.setBordered(false);
        this.name.setMaxLength(10000);
        this.name.setResponder(this::onNameChanged);
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
    }

    NumberEditBox initNumberBox(int xPos, int yPos, Consumer<String> onChanged) {
        NumberEditBox editBox = new NumberEditBox(this.font, leftPos + xPos, topPos + yPos, 160, 12, Component.translatable("container.repair"));

        editBox.setTextColor(0xffffffff);
        editBox.setTextColorUneditable(0xffffffff);
        //this.delay.setBordered(false);
        editBox.setMaxLength(10);
        //editBox.setResponder(onChanged);
        editBox.setValue(menu.getDelay()+"");
        this.addWidget(editBox);
        //this.setInitialFocus(this.delayBox);
        return editBox;
    }

    private void onNameChanged(String s) {
        updateRemote(s,false,-1, -1);
    }

    private void onDelayChanged(String s) {
        int delay = tryGet(s);
        updateRemote(s,false,delay,-1);
    }

    private void onRadiusChanged(String s) {
        int radius = tryGet(s);
        updateRemote(s,false,-1,radius);
    }

    void updateRemote(String s,boolean set,int delay,int radius) {
        Services.PLATFORM.sendToServer(new C2SSetKothPacket(s,set,delay, radius));
    }

    int tryGet(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }


        return this.name.keyPressed(pKeyCode, pScanCode, pModifiers) || this.name.canConsumeInput() ||

                this.radiusBox.keyPressed(pKeyCode, pScanCode, pModifiers) || this.radiusBox.canConsumeInput() ||
                this.delayBox.keyPressed(pKeyCode, pScanCode, pModifiers) || this.delayBox.canConsumeInput() ||super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.name.getValue();
        String s = this.delayBox.getValue();
        String s1 = this.radiusBox.getValue();
        this.init($$0, $$1, $$2);
        this.name.setValue($$3);
       delayBox.setValue(s);
        radiusBox.setValue(s1);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        name.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        delayBox.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        radiusBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        name.setTextColor(menu.getColor());
    }

    @Override
    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        $$0.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        $$0.drawString(this.font, Component.literal("Delay"), this.titleLabelX, this.titleLabelY+33, 0x404040, false);
        $$0.drawString(this.font, Component.literal("Radius"), this.titleLabelX, this.titleLabelY+58, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        //        guiGraphics.blitNineSlicedSized(SOCIALBACKGROUND,relX,relY+83-8,imageWidth,imageHeight - 73,4,4,236,34,0,0,236,34);
        Services.PLATFORM.nineSlice(guiGraphics,BACKGROUND,leftPos,topPos,imageWidth,imageHeight,4,12,12,0,0,12,12);
    }
}
