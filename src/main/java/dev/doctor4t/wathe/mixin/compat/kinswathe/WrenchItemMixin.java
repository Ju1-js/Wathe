package dev.doctor4t.wathe.mixin.compat.kinswathe;

import dev.doctor4t.wathe.block.VentHatchBlock;
import dev.doctor4t.wathe.block_entity.VentHatchBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.BsXinQin.kinswathe.KinsWatheItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.BsXinQin.kinswathe.items.WrenchItem", remap = false)
public abstract class WrenchItemMixin {
    // Both names support the named development environment and the shipped addon.
    @Inject(method = {"useOnBlock", "method_7884"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void wathe$repairHatch(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var player = context.getPlayer();
        var item = (Item) (Object) this;
        if (player == null || player.getItemCooldownManager().isCoolingDown(item)) return;
        var world = context.getWorld();
        var pos = context.getBlockPos();
        if (!(world.getBlockEntity(pos) instanceof VentHatchBlockEntity hatch)
                || (!hatch.isJammed() && !hatch.isBlasted())) return;
        if (!world.isClient) {
            KinsWatheItems.setItemAfterUsing(player, item, null);
            hatch.setJammed(0);
            hatch.setBlasted(false);
            // Repair never bypasses a configured lock. Preserve unlocked hatch behavior.
            if (hatch.getKeyName().isEmpty()) {
                world.setBlockState(pos, world.getBlockState(pos).with(VentHatchBlock.OPEN, true));
            } else {
                world.setBlockState(pos, world.getBlockState(pos).with(VentHatchBlock.OPEN, false));
            }
            hatch.sync();
            player.sendMessage(Text.literal(""), true);
            world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.PLAYERS, 0.8F, 1.0F);
        }
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
