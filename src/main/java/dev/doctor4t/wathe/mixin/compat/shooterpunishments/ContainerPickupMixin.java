package dev.doctor4t.wathe.mixin.compat.shooterpunishments;

import dev.doctor4t.wathe.compat.ShooterPunishmentsCompat;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ContainerPickupMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void wathe$preventContainerGunPickup(int slotId, int button, SlotActionType action, PlayerEntity player, CallbackInfo ci) {
        if (!ShooterPunishmentsCompat.cannotPickUpGuns(player)) return;
        var handler = (ScreenHandler) (Object) this;
        if (action == SlotActionType.PICKUP_ALL && handler.getCursorStack().isIn(WatheItemTags.GUNS)) {
            ci.cancel();
        } else if (slotId >= 0 && slotId < handler.slots.size()) {
            var slot = handler.slots.get(slotId);
            if (!(slot.inventory instanceof PlayerInventory) && slot.getStack().isIn(WatheItemTags.GUNS)) ci.cancel();
        }
    }
}
