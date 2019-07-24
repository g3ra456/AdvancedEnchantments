package me.egg82.ae.events.enchants;

import java.util.Random;
import me.egg82.ae.APIException;
import me.egg82.ae.api.AdvancedEnchantment;
import me.egg82.ae.api.BukkitEnchantableItem;
import me.egg82.ae.events.EventHolder;
import me.egg82.ae.utils.BlockUtil;
import me.egg82.ae.utils.LocationUtil;
import ninja.egg82.events.BukkitEventFilters;
import ninja.egg82.events.BukkitEvents;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.Plugin;

public class EnderEvents extends EventHolder {
    public EnderEvents(Plugin plugin) {
        events.add(
                BukkitEvents.subscribe(plugin, EntityDamageByEntityEvent.class, EventPriority.NORMAL)
                        .filter(BukkitEventFilters.ignoreCancelled())
                        .filter(this::townyIgnoreCancelled)
                        .filter(e -> e.getEntity() instanceof LivingEntity)
                        .filter(e -> ((LivingEntity) e.getEntity()).getEquipment() != null)
                        .filter(e -> canUseEnchant(e.getEntity(), "ae.curse.ender"))
                        .handler(this::damage)
        );
        try {
            Class.forName("org.bukkit.event.entity.ProjectileHitEvent");
            events.add(
                    BukkitEvents.subscribe(plugin, ProjectileHitEvent.class, EventPriority.NORMAL)
                            .filter(e -> e.getHitEntity() != null)
                            .filter(e -> e.getHitEntity() instanceof LivingEntity)
                            .filter(e -> ((LivingEntity) e.getHitEntity()).getEquipment() != null)
                            .filter(e -> canUseEnchant(e.getHitEntity(), "ae.curse.ender"))
                            .handler(this::hit)
            );
        } catch (ClassNotFoundException ignored) {}
    }

    private void damage(EntityDamageByEntityEvent event) {
        LivingEntity to = (LivingEntity) event.getEntity();
        EntityEquipment equipment = to.getEquipment();

        boolean hasEnchantment;
        int level;
        try {
            hasEnchantment = api.anyHasEnchantment(AdvancedEnchantment.ENDER_CURSE,
                    BukkitEnchantableItem.fromItemStack(equipment.getHelmet()),
                    BukkitEnchantableItem.fromItemStack(equipment.getChestplate()),
                    BukkitEnchantableItem.fromItemStack(equipment.getLeggings()),
                    BukkitEnchantableItem.fromItemStack(equipment.getBoots())
            );
            level = api.getMaxLevel(AdvancedEnchantment.ENDER_CURSE,
                    BukkitEnchantableItem.fromItemStack(equipment.getHelmet()),
                    BukkitEnchantableItem.fromItemStack(equipment.getChestplate()),
                    BukkitEnchantableItem.fromItemStack(equipment.getLeggings()),
                    BukkitEnchantableItem.fromItemStack(equipment.getBoots())
            );
        } catch (APIException ex) {
            logger.error(ex.getMessage(), ex);
            return;
        }

        if (!hasEnchantment) {
            return;
        }

        if (Math.random() > 0.08 * level) {
            return;
        }

        to.teleport(BlockUtil.getHighestSolidBlock(LocationUtil.getRandomPointAround(to.getLocation(), (new Random().nextInt(5) + 5) * level, false)).getLocation().add(0.0d, 1.0d, 0.0d));
    }

    private void hit(ProjectileHitEvent event) {
        LivingEntity to = (LivingEntity) event.getHitEntity();
        EntityEquipment equipment = to.getEquipment();

        boolean hasEnchantment;
        int level;
        try {
            hasEnchantment = api.anyHasEnchantment(AdvancedEnchantment.ENDER_CURSE,
                    BukkitEnchantableItem.fromItemStack(equipment.getHelmet()),
                    BukkitEnchantableItem.fromItemStack(equipment.getChestplate()),
                    BukkitEnchantableItem.fromItemStack(equipment.getLeggings()),
                    BukkitEnchantableItem.fromItemStack(equipment.getBoots())
            );
            level = api.getMaxLevel(AdvancedEnchantment.ENDER_CURSE,
                    BukkitEnchantableItem.fromItemStack(equipment.getHelmet()),
                    BukkitEnchantableItem.fromItemStack(equipment.getChestplate()),
                    BukkitEnchantableItem.fromItemStack(equipment.getLeggings()),
                    BukkitEnchantableItem.fromItemStack(equipment.getBoots())
            );
        } catch (APIException ex) {
            logger.error(ex.getMessage(), ex);
            return;
        }

        if (!hasEnchantment) {
            return;
        }

        if (Math.random() > 0.08 * level) {
            return;
        }

        to.teleport(BlockUtil.getHighestSolidBlock(LocationUtil.getRandomPointAround(to.getLocation(), (new Random().nextInt(5) + 5) * level, false)).getLocation().add(0.0d, 1.0d, 0.0d));
    }
}