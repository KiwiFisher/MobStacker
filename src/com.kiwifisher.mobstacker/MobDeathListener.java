package com.kiwifisher.mobstacker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class MobDeathListener implements Listener {

    boolean stackByAge = MobStacker.plugin.getConfig().getBoolean("stack-by-age");
    boolean stackLeashed = MobStacker.plugin.getConfig().getBoolean("stack-leashed-mobs");
    boolean protectTamed = MobStacker.plugin.getConfig().getBoolean("protect-tamed");
    boolean separateColour = MobStacker.plugin.getConfig().getBoolean("separate-stacks-by-color");



    @EventHandler (ignoreCancelled = true)
    public void mobDeathListener(EntityDeathEvent event) {

        if (MobStacker.isStacking()) {
            LivingEntity entity = event.getEntity();

            if (entity.hasMetadata("quantity")) {

                if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL && MobStacker.plugin.getConfig().getBoolean("kill-whole-stack-on-fall-death.enable") &&
                        entity.hasMetadata("quantity")) {
                    int quantity = entity.getMetadata("quantity").get(0).asInt();

                    LivingEntity dyingEntity = entity;

                    if (MobStacker.plugin.getConfig().getBoolean("kill-whole-stack-on-fall-death.multiply-loot")) {

                        for (int i = 0; i < quantity; i++) {
                            dyingEntity = StackUtils.peelOff(dyingEntity, false);
                            dyingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 2));
                            dyingEntity.setFallDistance(100);
                        }

                    }

                    return;
                }

                List<Entity> nearbyEntities = entity.getNearbyEntities(MobStacker.plugin.getConfig().getInt("stack-range.x"), MobStacker.plugin.getConfig().getInt("stack-range.y"), MobStacker.plugin.getConfig().getInt("stack-range.z"));

                int stackedEntityQuantity = entity.getMetadata("quantity").get(0).asInt();
                int newQuantity = stackedEntityQuantity - 1;

                Location entityLocation = entity.getLocation();
                EntityType entityType = entity.getType();

                if (newQuantity > 0) {

                    entity.removeMetadata("quantity", MobStacker.plugin);
                    LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entityLocation, entityType);

                    if (newEntity instanceof Ageable) {
                        ((Ageable) newEntity).setAge(((Ageable) event.getEntity()).getAge());
                    }

                    if (newEntity instanceof Colorable) {
                        ((Colorable) newEntity).setColor(((Colorable) event.getEntity()).getColor());
                    }

                    if (newEntity instanceof Sheep) {
                        ((Sheep) newEntity).setSheared(((Sheep) event.getEntity()).isSheared());
                    }

                    newEntity.setMetadata("quantity", new FixedMetadataValue(MobStacker.plugin, newQuantity));

                    if (newQuantity > 1) {

                        String configNaming = MobStacker.plugin.getConfig().getString("stack-naming");
                        configNaming = configNaming.replace("{QTY}", newQuantity + "");
                        configNaming = configNaming.replace("{TYPE}", entity.getType().toString().replace("_", " "));
                        configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
                        newEntity.setCustomName(configNaming);

                    }

                }

            }
        }

    }

}
