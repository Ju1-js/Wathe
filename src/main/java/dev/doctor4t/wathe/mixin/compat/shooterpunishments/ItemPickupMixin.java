package dev.doctor4t.wathe.mixin.compat.shooterpunishments;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;

/** TmmMixinPlugin narrows the addon's merged pickup callback to gun items. */
@Mixin(value = ItemEntity.class, priority = 500)
public class ItemPickupMixin {}
