package dev.doctor4t.wathe.compat;

import com.thedeafcreeper.shooterpunishments.Shooterpunishments;
import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/** Loaded only when Shooter Punishments 1.2 is installed. */
public final class ShooterPunishmentsCompat {
    private static final Map<MinecraftServer, Map<RegistryKey<World>, Set<UUID>>> RESTRICTED = new WeakHashMap<>();

    private ShooterPunishmentsCompat() {}

    public static void readSetting(NbtCompound tag) {
        // A world that predates the addon must not abort the entire CCA load.
        if (!tag.contains("shootInnocentPunishment", 8)) return;
        String value = tag.getString("shootInnocentPunishment");
        try {
            Shooterpunishments.shootInnocentPunishment = Shooterpunishments.ShootInnocentPunishment.valueOf(value);
        } catch (IllegalArgumentException e) {
            Wathe.LOGGER.warn("Ignoring invalid Shooter Punishments setting '{}'; retaining {}", value,
                    Shooterpunishments.shootInnocentPunishment);
        }
    }

    public static boolean shouldPunish(ServerPlayerEntity shooter, PlayerEntity target) {
        var game = GameWorldComponent.KEY.get(shooter.getWorld());
        return shooter != target && GameFunctions.isPlayerAliveAndSurvival(shooter)
                && GameFunctions.isPlayerAliveAndSurvival(target)
                && game.isInnocent(shooter) && game.isInnocent(target)
                && !LegacyRoleAddons.keepRevolver(shooter);
    }

    public static void punish(ServerPlayerEntity shooter) {
        if (!GameFunctions.isPlayerAliveAndSurvival(shooter)) return;
        switch (Shooterpunishments.shootInnocentPunishment) {
            case DEFAULT -> { }
            case PREVENT_GUN_PICKUP -> {
                Shooterpunishments.preventGunPickup.add(shooter);
                RESTRICTED.computeIfAbsent(shooter.getServer(), server -> new HashMap<>())
                        .computeIfAbsent(shooter.getWorld().getRegistryKey(), world -> new HashSet<>()).add(shooter.getUuid());
            }
            case KILL_SHOOTER -> GameFunctions.killPlayer(shooter, true, shooter, Wathe.id("shot_innocent"));
        }
    }

    public static boolean cannotPickUpGuns(PlayerEntity player) {
        if (player.isCreative() || !(player.getWorld() instanceof ServerWorld world)) return false;
        return Shooterpunishments.preventGunPickup.contains(player)
                || RESTRICTED.getOrDefault(world.getServer(), Collections.emptyMap())
                .getOrDefault(world.getRegistryKey(), Collections.emptySet()).contains(player.getUuid());
    }

    public static void onItemCollision(ItemEntity item, PlayerEntity player, CallbackInfo ci) {
        if (item.getStack().isIn(WatheItemTags.GUNS) && cannotPickUpGuns(player)) ci.cancel();
    }

    public static void clearRestrictions(ServerWorld world) {
        var worlds = RESTRICTED.get(world.getServer());
        if (worlds != null) worlds.remove(world.getRegistryKey());
    }
}
