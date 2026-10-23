package dev.doctor4t.wathe.mixin.compat.kinswathe;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.client.gui.RoleAnnouncementTexts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Preserve Dreamer's role selection/events/rewards; migrate only its announcement. */
@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.roles.dreamer.DreamerKillerComponent", remap = false)
public class DreamerKillerComponentMixin {
    @ModifyArg(method = "triggerBecomeKiller", at = @At(value = "INVOKE",
            target = "Ljava/util/ArrayList;indexOf(Ljava/lang/Object;)I"), index = 0)
    private Object wathe$vanillaRoleAnnouncement(Object announcement) {
        return announcement == WatheRoles.KILLER ? RoleAnnouncementTexts.KILLER : announcement;
    }
}
