package dev.doctor4t.wathe.compat;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.BsXinQin.kinswathe.KinsWatheConfig;
import org.BsXinQin.kinswathe.KinsWatheRoles;

/** Replaces KW 1.6.4's two injections into the removed util gun receiver. */
final class KinsWatheCompat {
    private KinsWatheCompat() {}

    static boolean keepRevolver(ServerPlayerEntity player) {
        if (!GameFunctions.isPlayerAliveAndSurvival(player)) return false;
        GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
        if (game.isRole(player, KinsWatheRoles.LICENSED_VILLAIN)) return true;
        KinsWatheConfig config = KinsWatheConfig.HANDLER.instance();
        return config.EnableWatheModify && config.PreventKillerDropRevolver
                && game.canUseKillerFeatures(player);
    }
}
