package eu.haelexuis.beautyindicator.controller;

import eu.haelexuis.beautyindicator.BeautyIndicator;
import eu.haelexuis.beautyindicator.model.Combat;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CombatController implements Listener {
    private BeautyIndicator beautyIndicator;
    private BukkitTask combatControlling;
    private ConcurrentHashMap<LivingEntity, Combat> entitiesInCombat = new ConcurrentHashMap<>();
    private String character;
    private int showTime;
    private String activeColor;
    private String neutralColor;
    private List<String> excludedMobs;
    private boolean hitByItself;

    public CombatController(BeautyIndicator beautyIndicator, FileConfiguration config) {
        this.beautyIndicator = beautyIndicator;

        onLoad(config);
    }

    public void onDisable() {
        entitiesInCombat.forEach((entity, entityCombat) -> entity.setCustomName(entityCombat.getNameToRestore()));
        entitiesInCombat.clear();
        combatControlling.cancel();
    }

    public void onReload(FileConfiguration config) {
        onDisable();
        onLoad(config);
    }

    public void onLoad(FileConfiguration config) {
        this.character = config.getString("heart-character");
        this.showTime = config.getInt("show-time");
        this.excludedMobs = config.getStringList("excluded-mobs");
        this.activeColor = config.getString("active-color");
        this.neutralColor = config.getString("neutral-color");
        this.excludedMobs = config.getStringList("excluded-mobs");
        this.excludedMobs = config.getStringList("excluded-mobs");
        this.hitByItself = config.getBoolean("hit-by-itself");

        startControllingCombat();
    }

    private void startControllingCombat() {
        combatControlling = new BukkitRunnable() {
            @Override
            public void run() {
                entitiesInCombat.forEach((entity, entityCombat) -> {
                    if(entityCombat.getSeconds() > 0)
                        entityCombat.doUpdate();
                    else {
                        if(entity != null) {
                            if(!entity.isDead()) {
                                String customName = entityCombat.getNameToRestore();
                                if(customName == null)
                                    entity.setCustomNameVisible(false);
                                entity.setCustomName(customName);
                            }
                            entitiesInCombat.remove(entity);
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(beautyIndicator, 0, 20);
    }

    private void addToCombat(LivingEntity entity, String entityName) {
        if(!entitiesInCombat.containsKey(entity))
            entitiesInCombat.put(entity, new Combat(entityName, showTime));
        else
            entitiesInCombat.get(entity).resetSeconds();
    }

    private void onHit(Entity entity) {
        if(entity instanceof ArmorStand || entity instanceof Player || entity instanceof ItemFrame || entity instanceof Minecart)
            return;
        if(entity instanceof LivingEntity) {
            if(excludedMobs.contains(entity.getType().toString()))
                return;
            new BukkitRunnable() {
                public void run() {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(livingEntity.getHealth() == livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                        return;

                    StringBuilder hearts = new StringBuilder();

                    int newHealth = (int) livingEntity.getHealth() / 2;
                    int maxHealth = (int) livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2 - newHealth;

                    for(int i = newHealth; i > 0; i--)
                        hearts.append(activeColor).append(character);
                    for(int i = maxHealth; i > 0; i--)
                        hearts.append(neutralColor).append(character);

                    addToCombat(livingEntity, livingEntity.getCustomName() == null ? livingEntity.getCustomName() : ChatColor.stripColor(livingEntity.getCustomName()).equals(ChatColor.stripColor(hearts.toString())) ? null : livingEntity.getCustomName());

                    entity.setCustomName(ChatColor.translateAlternateColorCodes('&', hearts.toString()));
                    entity.setCustomNameVisible(true);
                }
            }.runTaskAsynchronously(beautyIndicator);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHitByEntity(EntityDamageByEntityEvent e) {
        if(!hitByItself)
            onHit(e.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHit(EntityDamageEvent e) {
        if(hitByItself)
            onHit(e.getEntity());
    }
}
