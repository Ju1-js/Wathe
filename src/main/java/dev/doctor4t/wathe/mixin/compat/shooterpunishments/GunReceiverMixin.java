package dev.doctor4t.wathe.mixin.compat.shooterpunishments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.compat.ShooterPunishmentsCompat;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.network.GunShootPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GunShootPayload.Receiver.class)
public class GunReceiverMixin {
    @WrapOperation(method = "receive(Ldev/doctor4t/wathe/network/GunShootPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V"))
    private void wathe$punishInnocentShot(PlayerEntity target, boolean body, PlayerEntity killer, Identifier reason,
            Operation<Void> original, @Local(argsOnly = true) ServerPlayNetworking.Context context) {
        var shooter = context.player();
        boolean punish = shooter.getMainHandStack().isOf(WatheItems.REVOLVER)
                && killer == shooter && ShooterPunishmentsCompat.shouldPunish(shooter, target);
        original.call(target, body, killer, reason);
        // Preserve backfire, fake guns and addons that prevent the victim's death.
        if (punish && target.isSpectator()) ShooterPunishmentsCompat.punish(shooter);
    }
}
