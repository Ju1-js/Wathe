package dev.doctor4t.wathe.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.util.GunControls;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public WorldRenderer worldRenderer;

    @ModifyReturnValue(method = "hasOutline", at = @At("RETURN"))
    public boolean wathe$hasInstinctOutline(boolean original, @Local(argsOnly = true) Entity entity) {
        if (this.worldRenderer == null || this.worldRenderer.getEntityOutlinesFramebuffer() == null) {
            return false;
        }
        if (WatheClient.getInstinctHighlight(entity) != -1) return true;
        return original;
    }

    // Run after vanilla's block-breaking/riding checks and use-key repeat delay,
    // but before block/entity interaction so aiming at a door still fires the gun.
    @Inject(method = "doItemUse", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 0),
            cancellable = true)
    private void wathe$rightClickGunUse(CallbackInfo ci) {
        if (this.player != null && !this.player.isSpectator()
                && GunControls.usesRightClick(this.player.getMainHandStack())) {
            GunControls.fire(this.player);
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "doItemUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V"
            ))
    private boolean wathe$cancelRevolverUpdateAnimation(HeldItemRenderer instance, Hand hand) {
        return !MinecraftClient.getInstance().player.getStackInHand(hand).isIn(WatheItemTags.GUNS);
    }

    @WrapOperation(method = "handleInputEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"))
    private void wathe$invalid(@NotNull PlayerInventory instance, int value, Operation<Void> original) {
        int oldSlot = instance.selectedSlot;
        PlayerPsychoComponent component = PlayerPsychoComponent.KEY.get(instance.player);
        if (component.getPsychoTicks() > 0 &&
                (instance.getStack(oldSlot).isOf(WatheItems.BAT)) &&
                (!instance.getStack(value).isOf(WatheItems.BAT))
        ) return;
        original.call(instance, value);
    }
}
