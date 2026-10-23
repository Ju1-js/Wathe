package dev.doctor4t.wathe.mixin.compat.noellesroles;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class JesterContainerMixin {
    @Shadow public abstract ItemStack getCursorStack();

    private static boolean wathe$realGun(ItemStack stack) {
        return stack.isIn(WatheItemTags.GUNS)
                && !Registries.ITEM.getId(stack.getItem()).equals(Identifier.of("noellesroles", "fake_revolver"));
    }

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void wathe$blockContainerGun(int slotId, int button, SlotActionType action, PlayerEntity player, CallbackInfo ci) {
        if (player.isCreative()) return;
        var role = GameWorldComponent.KEY.get(player.getWorld()).getRole(player);
        if (role == null || !role.identifier().equals(Identifier.of("noellesroles", "jester"))) return;
        if (action == SlotActionType.PICKUP_ALL && wathe$realGun(getCursorStack())) {
            ci.cancel();
            return;
        }
        var slots = ((ScreenHandler) (Object) this).slots;
        if (slotId < 0 || slotId >= slots.size()) return;
        var slot = slots.get(slotId);
        if (!(slot.inventory instanceof PlayerInventory) && wathe$realGun(slot.getStack())) ci.cancel();
    }
}
