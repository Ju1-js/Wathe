package dev.doctor4t.wathe.client.util;

import dev.doctor4t.wathe.WatheConfig;
import dev.doctor4t.wathe.item.AttackUseItem;
import dev.doctor4t.wathe.item.PistolItem;
import dev.doctor4t.wathe.network.AttackUsePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class GunControls {
    private GunControls() {}

    /** Implemented by version-gated client mixins for addon guns. */
    public interface AddonGun extends AttackUseItem {}

    public static boolean usesRightClick(ItemStack stack) {
        return !WatheConfig.leftClickShooting && (stack.getItem() instanceof PistolItem || stack.getItem() instanceof AddonGun);
    }

    public static void fire(PlayerEntity player) {
        ((AttackUseItem) player.getMainHandStack().getItem()).triggerAttackUseClient(player);
        ClientPlayNetworking.send(new AttackUsePayload());
    }
}
