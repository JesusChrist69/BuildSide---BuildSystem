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

    /**
     * This function creates a new Builder object.
     *
     * @return A new instance of the Builder class.
     */
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

        /**
         * This function sets the item and onClick of the builder to the item and onClick of the GuiItem passed in.
         *
         * @param item The item to be displayed in the GUI.
         * @return The builder object.
         */
        public Builder of(@NotNull GuiItem item) {
            this.item = item.item;
            this.onClick = item.onClick;
            return this;
        }

        /**
         * This function sets the item variable to the item parameter and returns the builder.
         *
         * @param item The item that will be displayed in the GUI.
         * @return The Builder object itself.
         */
        public Builder withItem(@NotNull ItemStack item) {
            this.item = item;
            return this;
        }

        /**
         * If the item is null, create a new itemstack with the material, otherwise set the material of the itemstack.
         *
         * @param material The material of the item.
         * @return The builder itself.
         */
        public Builder withMaterial(@NotNull Material material) {
            if (this.item == null) {
                this.item = new ItemStack(material);
            } else {
                this.item.setType(material);
            }
            return this;
        }

        /**
         * This function sets the amount of the item in the builder.
         *
         * @param amount The amount of the item to be set.
         * @return The Builder object itself.
         */
        public Builder withAmount(int amount) {
            assert this.item != null : "Item must be set before setting amount";
            assert amount > 0 && amount <= item.getType().getMaxStackSize();
            this.item.setAmount(amount);
            return this;
        }

        /**
         * Sets the name of the item to the given name, and returns the builder.
         *
         * @param name The name of the item.
         * @return The builder itself.
         */
        public Builder withName(@NotNull String name) {
            assert this.item != null : "Item must be set before setting name";
            ItemMeta meta = this.item.getItemMeta();
            meta.setDisplayName(ColorUtils.colorize(name));
            this.item.setItemMeta(meta);
            return this;
        }

        /**
         * This function takes a list of strings, colors them, and sets them as the lore of the item
         *
         * @param lore The lore to set.
         * @return The builder object.
         */
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

        /**
         * This function takes a list of strings and returns a builder with the list of strings as the lore.
         *
         * @return A new Builder object with the lore added to it.
         */
        public Builder withLore(@NotNull String... lore) {
            return withLore(Arrays.asList(lore));
        }

        /**
         * If the item is a skull, set the owner to the given string
         *
         * @param owner The name of the player whose head you want to use.
         * @return The builder object
         */
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

        /**
         * Sets the base64 string of the skull item.
         *
         * @param base64 The base64 string of the skull.
         * @return A new ItemStack with the base64 skull.
         */
        public Builder withBase64Skull(@NotNull String base64) {
            assert this.item != null : "Item must be set before setting base64 skull.";
            assert this.item.getType() == Material.SKULL_ITEM && item.getData().getData() == 3 : "Item must be a player head to set base64 skull.";
            this.item = SkullCreator.itemWithBase64(this.item, base64);
            return this;
        }

        /**
         * "Adds an enchantment to the item."
         *
         * The first line is the function declaration. It's a public function that returns a Builder object. The function
         * is named withEnchantment. It takes two parameters: an Enchantment object and an integer. The @NotNull annotation
         * is a Java annotation that tells the compiler that the Enchantment object cannot be null
         *
         * @param enchantment The enchantment to add.
         * @param level The level of the enchantment.
         * @return The builder object.
         */
        public Builder withEnchantment(@NotNull Enchantment enchantment, int level) {
            assert this.item != null : "Item must be set before adding enchantment.";
            assert level > 0 : "Enchantment level must be greater than 0.";
            this.item.addUnsafeEnchantment(enchantment, level);
            return this;
        }

        /**
         * Returns a new builder with the given enchantment and level.
         *
         * @param enchantment The enchantment to add to the item.
         * @return The builder itself.
         */
        public Builder withEnchantment(@NotNull Enchantment enchantment) {
            return withEnchantment(enchantment, 1);
        }

        /**
         * This function adds the specified ItemFlags to the item's ItemMeta.
         *
         * @return The builder itself.
         */
        public Builder withItemFlags(@NotNull ItemFlag... flags) {
            assert this.item != null : "Item must be set before adding flags.";
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(flags);
            this.item.setItemMeta(meta);
            return this;
        }

        /**
         * This function sets the onClick event to the given Consumer<InventoryClickEvent>.
         *
         * @param onClick The consumer that will be called when the item is clicked.
         * @return The Builder object itself.
         */
        public Builder withClickEvent(@NotNull Consumer<InventoryClickEvent> onClick) {
            this.onClick = onClick;
            return this;
        }

        /**
         * This function removes a line from the lore of the item
         *
         * @param line The line to remove.
         * @return The builder object.
         */
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

        /**
         * This function sets the unbreakable state of the item.
         *
         * @param unbreakable Whether the item is unbreakable.
         * @return The builder object.
         */
        public Builder unbreakable(boolean unbreakable) {
            assert this.item != null : "Item must be set before setting unbreakable.";
            ItemMeta meta = this.item.getItemMeta();
            meta.setUnbreakable(unbreakable);
            this.item.setItemMeta(meta);
            return this;
        }

        /**
         * This method returns a new GuiItem object with the values of the GuiItemBuilder object
         *
         * @return A new GuiItem object.
         */
        public GuiItem build() {
            return new GuiItem(this);
        }
    }

    /**
     * If the onClick variable is not null, then call the onClick function
     *
     * @param e The InventoryClickEvent that was called.
     */
    public void callClick(InventoryClickEvent e) {
        if (onClick != null) {
            onClick.accept(e);
        }
    }

}
