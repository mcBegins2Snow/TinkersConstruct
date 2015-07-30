package tconstruct.tools.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;
import java.util.Map;

import tconstruct.common.inventory.BaseContainer;

public class ContainerMultiModule<T extends TileEntity> extends BaseContainer<T> {

  public List<Container> subContainers = Lists.newArrayList();

  // lookup used to redirect slot specific things to the appropriate container
  private Map<Integer, Container> slotContainerMap = Maps.newHashMap();

  public ContainerMultiModule(T tile) {
    super(tile);
  }


  public void addSubContainer(Container subcontainer) {
    subContainers.add(subcontainer);

    for(Object slot : subcontainer.inventorySlots) {
      SlotWrapper wrapper = new SlotWrapper((Slot) slot);
      addSlotToContainer(wrapper);
      slotContainerMap.put(wrapper.slotNumber, subcontainer);
    }
  }

  public <T extends Container> T getSubContainer(Class<T> clazz) {
    return getSubContainer(clazz, 0);
  }

  public <T extends Container> T getSubContainer(Class<T> clazz, int index) {
    for(Container sub : subContainers) {
      if(clazz.isAssignableFrom(sub.getClass())) {
        index--;
      }
      if(index < 0) {
        return (T) sub;
      }
    }

    return null;
  }

  public Container getSlotContainer(int slotNumber) {
    if(slotContainerMap.containsKey(slotNumber)) {
      return slotContainerMap.get(slotNumber);
    }

    return this;
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    // check if subcontainers are valid
    for(Container sub : subContainers) {
      if(!sub.canInteractWith(playerIn)) {
        return false;
      }
    }

    return super.canInteractWith(playerIn);
  }


  @Override
  public void onContainerClosed(EntityPlayer playerIn) {
    for(Container sub : subContainers) {
      sub.onContainerClosed(playerIn);
    }

    super.onContainerClosed(playerIn);
  }

  @Override
  public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
    if(slotId == -999 && mode == 5) {
      for(Container container : subContainers)
        container.slotClick(slotId, clickedButton, mode, playerIn);
    }

    if(slotContainerMap.containsKey(slotId)) {
      int actualId = slotId;
      if(this.inventorySlots.get(slotId) instanceof SlotWrapper)
        actualId = ((SlotWrapper) this.inventorySlots.get(slotId)).parent.slotNumber;
      return slotContainerMap.get(slotId).slotClick(actualId, clickedButton, mode, playerIn);
    }

    return super.slotClick(slotId, clickedButton, mode, playerIn);
  }
}
