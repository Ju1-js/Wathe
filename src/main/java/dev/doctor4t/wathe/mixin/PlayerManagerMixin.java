package dev.doctor4t.wathe.mixin;

import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.doctor4t.wathe.world.WatheMapWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    /**
     * Intercepts player connection before any dimension or chunk packets are sent.
     * If the player will spawn in the hub world and there is a current map active,
     * silently redirect them into the map world so clients never receive hub data.
     */
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void wathe$redirectToMapWorld(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        String mapName = WatheMapWorlds.getCurrentMapName();
        if (mapName == null) return;
        if (WatheMapWorlds.isMapWorldKey(player.getServerWorld().getRegistryKey())) return;

        ServerWorld target = WatheMapWorlds.getLoaded(player.getServer(), mapName).orElse(null);
        if (target == null) return;

        MapVariablesWorldComponent.PosWithOrientation spawn = MapVariablesWorldComponent.KEY.get(target).getSpawnPos();
        player.setWorld(target);
        player.refreshPositionAndAngles(spawn.pos, spawn.yaw, spawn.pitch);
    }

}
