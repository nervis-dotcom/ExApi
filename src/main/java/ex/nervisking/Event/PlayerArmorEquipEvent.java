package ex.nervisking.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerArmorEquipEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ItemStack armorPiece;
    private final ArmorType armorType;
    private boolean cancelled;

    public PlayerArmorEquipEvent(Player player, ItemStack armorPiece, ArmorType armorType) {
        this.player = player;
        this.armorPiece = armorPiece;
        this.armorType = armorType;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getArmorPiece() {
        return armorPiece;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}
