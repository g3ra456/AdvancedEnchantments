package me.egg82.ae.tasks;

import java.util.Optional;
import me.egg82.ae.APIException;
import me.egg82.ae.EnchantAPI;
import me.egg82.ae.api.AdvancedEnchantment;
import me.egg82.ae.api.BukkitEnchantableItem;
import me.egg82.ae.api.GenericEnchantableItem;
import me.egg82.ae.services.entity.EntityItemHandler;
import me.egg82.ae.utils.PermissionUtil;
import me.egg82.ae.utils.SoulsUtil;
import ninja.egg82.service.ServiceLocator;
import ninja.egg82.service.ServiceNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskVoid implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private EnchantAPI api = EnchantAPI.getInstance();

    private EntityItemHandler entityItemHandler;

    public TaskVoid() {
        try {
            entityItemHandler = ServiceLocator.get(EntityItemHandler.class);
        } catch (InstantiationException | IllegalAccessException | ServiceNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (
                    !PermissionUtil.canUseEnchant(player, "ae.curse.void")
                    || !PermissionUtil.canUseEnchant(player, "ae.curse.vorpal")
            ) {
                continue;
            }

            Optional<EntityEquipment> equipment = Optional.ofNullable(player.getEquipment());
            if (!equipment.isPresent()) {
                continue;
            }

            Optional<ItemStack> mainHand = entityItemHandler.getItemInMainHand(player);
            Optional<ItemStack> offHand = entityItemHandler.getItemInOffHand(player);
            GenericEnchantableItem enchantableMainHand = mainHand.isPresent() ? BukkitEnchantableItem.fromItemStack(mainHand.get()) : null;
            GenericEnchantableItem enchantableOffHand = offHand.isPresent() ? BukkitEnchantableItem.fromItemStack(offHand.get()) : null;

            GenericEnchantableItem enchantableHelmet = BukkitEnchantableItem.fromItemStack(equipment.get().getHelmet());
            GenericEnchantableItem enchantableChestplate = BukkitEnchantableItem.fromItemStack(equipment.get().getChestplate());
            GenericEnchantableItem enchantableLeggings = BukkitEnchantableItem.fromItemStack(equipment.get().getLeggings());
            GenericEnchantableItem enchantableBoots = BukkitEnchantableItem.fromItemStack(equipment.get().getBoots());

            boolean hasEnchantment;
            int level;
            try {
                hasEnchantment = api.anyHasEnchantment(AdvancedEnchantment.VOID_CURSE,
                        enchantableMainHand,
                        enchantableOffHand,
                        enchantableHelmet,
                        enchantableChestplate,
                        enchantableLeggings,
                        enchantableBoots);
                level = api.getMaxLevel(AdvancedEnchantment.VOID_CURSE,
                        enchantableMainHand,
                        enchantableOffHand,
                        enchantableHelmet,
                        enchantableChestplate,
                        enchantableLeggings,
                        enchantableBoots);
            } catch (APIException ex) {
                logger.error(ex.getMessage(), ex);
                continue;
            }

            if (!hasEnchantment) {
                continue;
            }

            if (Math.random() > 0.08 * level) {
                continue;
            }

            SoulsUtil.tryRemoveSouls(player, 1);
        }
    }
}
