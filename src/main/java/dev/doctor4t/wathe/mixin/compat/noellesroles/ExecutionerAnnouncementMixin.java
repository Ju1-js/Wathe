package dev.doctor4t.wathe.mixin.compat.noellesroles;

import dev.doctor4t.wathe.game.GameFunctions;
import org.spongepowered.asm.mixin.Mixin;

/** TmmMixinPlugin fixes NoellesRoles' merged callback after mixin application. */
@Mixin(value = GameFunctions.class, priority = 500)
public class ExecutionerAnnouncementMixin {}
