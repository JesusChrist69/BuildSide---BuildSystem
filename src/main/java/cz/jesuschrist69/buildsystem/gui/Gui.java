package cz.jesuschrist69.buildsystem.gui;

import cz.jesuschrist69.buildsystem.utils.ColorUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@EqualsAndHashCode
@ToString
public class Gui implements InventoryHolder {
    private Inventory inventory;
    private final Map<Integer, GuiItem> items;
    private String title;
    private int size;
    private final Consumer<InventoryCloseEvent> onClose;
    private final Consumer<InventoryOpenEvent> onOpen;

    private final boolean disabledClicking;

    public Gui(@NotNull Builder builder) {
        this.title = builder.title;
        this.size = builder.size;
        this.items = builder.items;
        this.onClose = builder.onClose;
        this.onOpen = builder.onOpen;
        this.disabledClicking = builder.disabledClicking;
        this.inventory = Bukkit.createInventory(this, size, ColorUtils.colorize(title));
        for (int slot : items.keySet()) {
            if (slot >= inventory.getSize()) continue;
            inventory.setItem(slot, items.get(slot).getItem());
        }
    }

    /**
     * This function creates a new Builder object and returns it.
     *
     * @return A new instance of the Builder class.
     */
    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Map<Integer, GuiItem> items;
        private String title;
        private int size;
        private Consumer<InventoryCloseEvent> onClose;
        private Consumer<InventoryOpenEvent> onOpen;

        private boolean disabledClicking;

        public Builder() {
            this.items = new HashMap<>();
            this.title = "Inventory";
            this.size = 9;
            this.onClose = null;
            this.onOpen = null;
            this.disabledClicking = false;
        }

        /**
         * It copies the values of the Gui object passed into the builder
         *
         * @param gui The gui you want to copy
         * @return A new instance of the Builder class.
         */
        public Builder of(@NotNull Gui gui) {
            this.items = gui.items;
            this.title = gui.title;
            this.size = gui.size;
            this.onClose = gui.onClose;
            this.onOpen = gui.onOpen;
            this.disabledClicking = gui.disabledClicking;
            return this;
        }

        /**
         * This function sets the disabledClicking variable to true and returns the builder object.
         *
         * @return The Builder object itself.
         */
        public Builder withDisabledClicking() {
            this.disabledClicking = true;
            return this;
        }

        /**
         * It sets the item in the specified slot to the specified item
         *
         * @param slot The slot of the item.
         * @param item The item to be set in the slot
         * @return The builder object itself.
         */
        public Builder setItem(int slot, @NotNull GuiItem item) {
            items.put(slot, item);
            return this;
        }

        /**
         * `This function sets the items of the GuiInventory object to the items passed in as a parameter.`
         *
         * @param items The items that will be displayed in the inventory.
         * @return The builder itself.
         */
        public Builder withItems(@NotNull Map<Integer, GuiItem> items) {
            this.items = items;
            return this;
        }

        /**
         * `withTitle` is a function that takes a `String` as an argument and returns a `Builder` object
         *
         * @param title The title of the notification.
         * @return The Builder object itself.
         */
        public Builder withTitle(@NotNull String title) {
            this.title = title;
            return this;
        }

        /**
         * This function returns a Builder object with the size field set to the value of the size parameter.
         *
         * @param size The size of the array to be created.
         * @return The Builder object itself.
         */
        public Builder withSize(int size) {
            this.size = size;
            return this;
        }

        /**
         * This function sets the onClose event to the given Consumer<InventoryCloseEvent>.
         *
         * @param onClose This is a Consumer<InventoryCloseEvent> that will be called when the inventory is closed.
         * @return The builder object itself.
         */
        public Builder withOnCloseEvent(Consumer<InventoryCloseEvent> onClose) {
            this.onClose = onClose;
            return this;
        }

        /**
         * This function sets the onOpen variable to the value of the onOpen parameter.
         *
         * @param onOpen This is a Consumer<InventoryOpenEvent> that will be called when the inventory is opened.
         * @return The builder object itself.
         */
        public Builder withOnOpenEvent(Consumer<InventoryOpenEvent> onOpen) {
            this.onOpen = onOpen;
            return this;
        }

        /**
         * This function returns a new Gui object, using the values of the fields in this Builder object.
         *
         * @return A new Gui object.
         */
        public Gui build() {
            return new Gui(this);
        }
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    /**
     * If the onClose variable is not null, then call the accept function of the onClose variable, passing in the
     * InventoryCloseEvent
     *
     * @param e The event that was called.
     */
    public void callClosed(InventoryCloseEvent e) {
        if (onClose != null) {
            onClose.accept(e);
        }
    }

    /**
     * If the onOpen variable is not null, then call the accept method of the onOpen variable, passing in the
     * InventoryOpenEvent parameter
     *
     * @param e The event that was called.
     */
    public void callOpen(InventoryOpenEvent e) {
        if (onOpen != null) {
            onOpen.accept(e);
        }
    }

    /**
     * It updates the item in the specified slot with the new item
     *
     * @param slot The slot you want to update.
     * @param newItem The new item to be placed in the slot.
     */
    public void updateItem(int slot, @NotNull GuiItem newItem) {
        items.put(slot, newItem);
        inventory.setItem(slot, newItem.getItem());
    }

    /**
     * It removes an item from the inventory
     *
     * @param slot The slot you want to remove the item from.
     */
    public void removeItem(int slot) {
        items.remove(slot);
        inventory.setItem(slot, null);
    }

    /**
     * It updates the title of the inventory and then creates a new inventory
     *
     * @param title The title of the inventory.
     */
    public void updateTitle(@NotNull String title) {
        this.title = title;
        createNewInv();
    }

    /**
     * This function updates the size of the inventory and creates a new inventory with the new size.
     *
     * @param newSize The new size of the inventory.
     */
    public void updateSize(int newSize) {
        this.size = newSize;
        createNewInv();
    }

    /**
     * It creates a new inventory, closes the old one, and opens the new one for all viewers
     */
    private void createNewInv() {
        List<HumanEntity> viwers = new ArrayList<>(inventory.getViewers());
        for (HumanEntity ent : inventory.getViewers()) {
            if (ent instanceof Player) {
                ((Player) ent).closeInventory();
            }
        }
        this.inventory = Bukkit.createInventory(this, size, ColorUtils.colorize(title));
        for (int slot : items.keySet()) {
            inventory.setItem(slot, items.get(slot).getItem());
        }
        for (HumanEntity ent : viwers) {
            if (ent instanceof Player) {
                ((Player) ent).openInventory(inventory);
            }
        }
    }

    /**
     * It clears the inventory, then adds all the items in the items HashMap to the inventory
     */
    public void updateItems() {
        inventory.clear();
        for (int item : items.keySet()) {
            inventory.setItem(item, items.get(item).getItem());
        }
    }

    /**
     * Returns the inventory.
     *
     * @return The inventory.
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
