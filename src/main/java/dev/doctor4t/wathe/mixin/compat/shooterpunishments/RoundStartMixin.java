package dev.doctor4t.wathe.mixin.compat.shooterpunishments;

import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.compat.ShooterPunishmentsCompat;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class RoundStartMixin {
    @Inject(method = "startGame", at = @At("HEAD"))
    private static void wathe$clearPickupRestrictions(ServerWorld world, GameMode mode, MapEffect effect, int time, CallbackInfo ci) {
        ShooterPunishmentsCompat.clearRestrictions(world);
    }
}
