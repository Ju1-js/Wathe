package dev.doctor4t.wathe.mixin.compat.shooterpunishments;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import org.spongepowered.asm.mixin.Mixin;

/** TmmMixinPlugin repairs the addon's merged NBT reader. */
@Mixin(value = GameWorldComponent.class, priority = 500)
public class SavedSettingMixin {}
