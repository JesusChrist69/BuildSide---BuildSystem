package cz.jesuschrist69.buildsystem.gui;

import cz.jesuschrist69.buildsystem.utils.ColorUtils;
import dev.dbassett.skullcreator.SkullCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Getter
@EqualsAndHashCode
@ToString
public class GuiItem {

    private ItemStack item;
    private Consumer<InventoryClickEvent> onClick;

    public GuiItem(@NotNull Builder builder) {
        this.item = builder.item;
        this.onClick = builder.onClick;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private ItemStack item;
        private Consumer<InventoryClickEvent> onClick;

        public Builder() {
            this.item = null;
            this.onClick = null;
        }

        public Builder of(@NotNull GuiItem item) {
            this.item = item.item;
            this.onClick = item.onClick;
            return this;
        }

        public Builder withItem(@NotNull ItemStack item) {
            this.item = item;
            return this;
        }

        public Builder withMaterial(@NotNull Material material) {
            if (this.item == null) {
                this.item = new ItemStack(material);
            } else {
                this.item.setType(material);
            }
            return this;
        }

        public Builder withAmount(int amount) {
            assert this.item != null : "Item must be set before setting amount";
            assert amount > 0 && amount <= item.getType().getMaxStackSize();
            this.item.setAmount(amount);
            return this;
        }

        public Builder withName(@NotNull String name) {
            assert this.item != null : "Item must be set before setting name";
            ItemMeta meta = this.item.getItemMeta();
            meta.setDisplayName(ColorUtils.colorize(name));
            this.item.setItemMeta(meta);
            return this;
        }

        public Builder withLore(@NotNull List<String> lore) {
            assert this.item != null : "Item must be set before setting lore.";
            ItemMeta meta = this.item.getItemMeta();
            List<String> newLore = new ArrayList<>();
            for (String s : lore) {
                newLore.add(ColorUtils.colorize(s));
            }
            meta.setLore(newLore);
            this.item.setItemMeta(meta);
            return this;
        }

        public Builder withLore(@NotNull String... lore) {
            return withLore(Arrays.asList(lore));
        }

        public Builder withSkullOwner(@NotNull String owner) {
            if (item == null) return this;
            if (item.getType() != Material.SKULL_ITEM && item.getData().getData() != 3) return this;
            if (owner.length() > 16) {
                return withBase64Skull(owner);
            }
            SkullMeta meta = (SkullMeta) this.item.getItemMeta();
            meta.setOwner(owner);
            this.item.setItemMeta(meta);
            return this;
        }

        public Builder withBase64Skull(@NotNull String base64) {
            assert this.item != null : "Item must be set before setting base64 skull.";
            assert this.item.getType() == Material.SKULL_ITEM && item.getData().getData() == 3 : "Item must be a player head to set base64 skull.";
            this.item = SkullCreator.itemWithBase64(this.item, base64);
            return this;
        }

        public Builder withEnchantment(@NotNull Enchantment enchantment, int level) {
            assert this.item != null : "Item must be set before adding enchantment.";
            assert level > 0 : "Enchantment level must be greater than 0.";
            this.item.addUnsafeEnchantment(enchantment, level);
            return this;
        }

        public Builder withEnchantment(@NotNull Enchantment enchantment) {
            return withEnchantment(enchantment, 1);
        }

        public Builder withItemFlags(@NotNull ItemFlag... flags) {
            assert this.item != null : "Item must be set before adding flags.";
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(flags);
            this.item.setItemMeta(meta);
            return this;
        }

        public Builder withClickEvent(@NotNull Consumer<InventoryClickEvent> onClick) {
            this.onClick = onClick;
            return this;
        }

        public Builder removeLoreLine(int line) {
            assert this.item != null : "Item must be set before removing lore line.";
            assert line > 0 : "Lore line must be greater than 0.";
            ItemMeta meta = this.item.getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
            assert line < lore.size() : "Lore line must be less than the amount of lore lines.";
            lore.remove(line);
            meta.setLore(lore);
            this.item.setItemMeta(meta);
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            assert this.item != null : "Item must be set before setting unbreakable.";
            ItemMeta meta = this.item.getItemMeta();
            meta.setUnbreakable(unbreakable);
            this.item.setItemMeta(meta);
            return this;
        }

        public GuiItem build() {
            return new GuiItem(this);
        }
    }

    public void callClick(InventoryClickEvent e) {
        if (onClick != null) {
            onClick.accept(e);
        }
    }

}
