package tfar.kothcrown;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;

public class CrownItem extends Item implements Equipable {
    public CrownItem(Properties $$0) {
        super($$0);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
