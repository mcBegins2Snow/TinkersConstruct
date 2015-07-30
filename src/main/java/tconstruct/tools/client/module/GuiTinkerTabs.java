package tconstruct.tools.client.module;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import java.util.List;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.tools.client.GuiMultiModule;
import tconstruct.tools.client.GuiTinkerStation;

public class GuiTinkerTabs extends GuiModule {
  protected static final GuiElement GUI_Tab = new GuiElement(0,2,28,28,256,256);
  protected static final GuiElement GUI_TabActiveL = new GuiElement(0,32,28,32,256,256);
  protected static final GuiElement GUI_TabActiveC = new GuiElement(28,32,28,32,256,256);
  protected static final GuiElement GUI_TabActiveR = new GuiElement(140,32,28,32,256,256);

  public GuiPartTabs tabs;
  public List<BlockPos> tabData;

  public final GuiTinkerStation parent;

  public GuiTinkerTabs(GuiTinkerStation parent, Container container) {
    super(parent, container, false, false);

    this.parent = parent;

    this.xSize = GUI_TabActiveC.w;
    this.ySize = GUI_TabActiveC.h;

    this.tabs = new GuiPartTabs(parent, GUI_Tab, GUI_Tab, GUI_Tab, GUI_TabActiveL, GUI_TabActiveC, GUI_TabActiveR);
    this.tabData = Lists.newArrayList();
  }

  public void addTab(ItemStack icon, BlockPos data) {
    this.tabData.add(data);
    this.tabs.addTab(icon);
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    // we actually want to be on top of the parent
    this.guiLeft = parentX;
    this.guiTop = parentY - this.ySize;

    tabs.setPosition(guiLeft + 4, guiTop);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    int sel = tabs.selected;
    tabs.update(mouseX, mouseY);
    tabs.draw();

    // new selection
    if(sel != tabs.selected) {
      parent.onTabSelection(tabs.selected);
    }
  }
}
