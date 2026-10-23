package dev.doctor4t.wathe.mixin.compat.shooterpunishments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.compat.ShooterPunishmentsCompat;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "dpm.harpysimpleroles.util.BanditRevolverShootPayload$Receiver", remap = false)
public class BanditReceiverMixin {
    @WrapOperation(method = "receive(Ldpm/harpysimpleroles/util/BanditRevolverShootPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", remap = true))
    private void wathe$punishBanditShot(PlayerEntity target, boolean body, PlayerEntity killer, Identifier reason,
            Operation<Void> original, @Local(argsOnly = true) ServerPlayNetworking.Context context) {
        var shooter = context.player();
        boolean punish = killer == shooter && ShooterPunishmentsCompat.shouldPunish(shooter, target);
        original.call(target, body, killer, reason);
        if (punish && target.isSpectator()) ShooterPunishmentsCompat.punish(shooter);
    }
}
