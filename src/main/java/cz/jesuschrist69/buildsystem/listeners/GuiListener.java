package cz.jesuschrist69.buildsystem.listeners;

import cz.jesuschrist69.buildsystem.component.BuildSystemListener;
import cz.jesuschrist69.buildsystem.gui.Gui;
import cz.jesuschrist69.buildsystem.gui.GuiItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;

public class GuiListener implements BuildSystemListener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getHolder() instanceof Gui) {
            Gui gui = (Gui) e.getClickedInventory().getHolder();
            if (gui.isDisabledClicking()) {
                e.setCancelled(true);
            }

            GuiItem item = gui.getItems().get(e.getSlot());
            if (item != null) {
                item.callClick(e);
            }
        }
        if (e.getView().getTopInventory().getHolder() instanceof Gui) {
            Gui gui = (Gui) e.getView().getTopInventory().getHolder();
            if (e.getView().getBottomInventory().equals(e.getClickedInventory())) {
                if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Gui) {
            Gui gui = (Gui) e.getInventory().getHolder();
            if (gui.isDisabledClicking()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof Gui) {
            Gui gui = (Gui) e.getInventory().getHolder();
            gui.callClosed(e);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof Gui) {
            Gui gui = (Gui) e.getInventory().getHolder();
            gui.callOpen(e);
        }
    }

}
