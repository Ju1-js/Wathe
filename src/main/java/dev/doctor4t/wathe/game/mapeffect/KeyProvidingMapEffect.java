package dev.doctor4t.wathe.game.mapeffect;

import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public abstract class KeyProvidingMapEffect extends MapEffect {
    public KeyProvidingMapEffect(Identifier identifier) {
        super(identifier);
    }
    protected void givePlayerKey(String keyName, ServerPlayerEntity player) {
        ItemStack itemStack = new ItemStack(WatheItems.KEY);
        itemStack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> new LoreComponent(Text.literal(keyName)
                .getWithStyle(Style.EMPTY.withItalic(false).withColor(0xFF8C00))));
        player.giveItemStack(itemStack);
    }

    protected void provideKeysOnly(ServerWorld serverWorld, List<ServerPlayerEntity> players, int rooms) {
        provideKeysOnly(serverWorld, players, rooms, "Room %d");
    }

    protected void provideKeysOnly(ServerWorld serverWorld, List<ServerPlayerEntity> players, int rooms, String roomKeyFormat) {
        Collections.shuffle(players);
        int roomNumber = 0;
        for (ServerPlayerEntity serverPlayerEntity : players) {
            roomNumber = roomNumber % rooms + 1;
            int finalRoomNumber = roomNumber;
            givePlayerKey(roomKeyFormat.formatted(finalRoomNumber), serverPlayerEntity);
        }
    }

    @Override
    public void finalizeMapEffects(ServerWorld serverWorld, List<ServerPlayerEntity> players) {
    }
}
