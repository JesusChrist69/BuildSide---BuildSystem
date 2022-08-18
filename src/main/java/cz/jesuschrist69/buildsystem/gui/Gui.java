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

        public Builder of(@NotNull Gui gui) {
            this.items = gui.items;
            this.title = gui.title;
            this.size = gui.size;
            this.onClose = gui.onClose;
            this.onOpen = gui.onOpen;
            this.disabledClicking = gui.disabledClicking;
            return this;
        }

        public Builder withDisabledClicking() {
            this.disabledClicking = true;
            return this;
        }

        public Builder setItem(int slot, @NotNull GuiItem item) {
            items.put(slot, item);
            return this;
        }

        public Builder withItems(@NotNull Map<Integer, GuiItem> items) {
            this.items = items;
            return this;
        }

        public Builder withTitle(@NotNull String title) {
            this.title = title;
            return this;
        }

        public Builder withSize(int size) {
            this.size = size;
            return this;
        }

        public Builder withOnCloseEvent(Consumer<InventoryCloseEvent> onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder withOnOpenEvent(Consumer<InventoryOpenEvent> onOpen) {
            this.onOpen = onOpen;
            return this;
        }

        public Gui build() {
            return new Gui(this);
        }
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    public void callClosed(InventoryCloseEvent e) {
        if (onClose != null) {
            onClose.accept(e);
        }
    }

    public void callOpen(InventoryOpenEvent e) {
        if (onOpen != null) {
            onOpen.accept(e);
        }
    }

    public void updateItem(int slot, @NotNull GuiItem newItem) {
        items.put(slot, newItem);
        inventory.setItem(slot, newItem.getItem());
    }

    public void removeItem(int slot) {
        items.remove(slot);
        inventory.setItem(slot, null);
    }

    public void updateTitle(@NotNull String title) {
        this.title = title;
        createNewInv();
    }

    public void updateSize(int newSize) {
        this.size = newSize;
        createNewInv();
    }

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

    public void updateItems() {
        inventory.clear();
        for (int item : items.keySet()) {
            inventory.setItem(item, items.get(item).getItem());
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
