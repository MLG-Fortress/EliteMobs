/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.magmaguy.elitemobs.elitedrops;

import com.magmaguy.elitemobs.MetadataHandler;
import com.magmaguy.elitemobs.config.ConfigValues;
import com.magmaguy.elitemobs.config.ItemsDropSettingsConfig;
import com.magmaguy.elitemobs.config.ItemsProceduralSettingsConfig;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by MagmaGuy on 04/06/2017.
 */
public class EliteDropsDropper implements Listener {

    private Random random = new Random();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(EntityDeathEvent event) {

        if (!ConfigValues.itemsDropSettingsConfig.getBoolean(ItemsDropSettingsConfig.ENABLE_PLUGIN_LOOT)) return;

        Entity entity = event.getEntity();

        if (!entity.hasMetadata(MetadataHandler.NATURAL_MOB_MD) ||
                !entity.hasMetadata(MetadataHandler.ELITE_MOB_MD)) return;

        if (entity.getMetadata(MetadataHandler.ELITE_MOB_MD).get(0).asInt() < 2) return;

        dropItem(entity);

    }

    public void dropItem(Entity entity) {

        //remember that this is used by other classes, like the extra loot power
        double chanceToDrop = ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.ELITE_ITEM_FLAT_DROP_RATE) / 100 +
                (ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.ELITE_ITEM_LEVEL_DROP_RATE) / 100 *
                        entity.getMetadata(MetadataHandler.ELITE_MOB_MD).get(0).asInt());

        if (random.nextDouble() > chanceToDrop) return;

        boolean proceduralItemsOn = ConfigValues.itemsProceduralSettingsConfig.getBoolean(ItemsProceduralSettingsConfig.DROP_ITEMS_ON_DEATH);

        boolean customItemsOn = ConfigValues.itemsDropSettingsConfig.getBoolean(ItemsDropSettingsConfig.DROP_CUSTOM_ITEMS);

        boolean staticCustomItemsExist = CustomItemConstructor.staticCustomItemHashMap.size() > 0;

        int mobLevel = entity.getMetadata(MetadataHandler.ELITE_MOB_MD).get(0).asInt();

        if (mobLevel < 1) mobLevel = 0;

        boolean customDynamicDropExists = CustomItemConstructor.dynamicRankedItemStacks.containsKey((int) (ItemWorthCalculator.targetItemWorth(mobLevel) / 10));

        if (proceduralItemsOn && !customItemsOn) {

            dropProcedurallyGeneratedItem(mobLevel, entity);
            return;

        }

        if (!proceduralItemsOn && customItemsOn) {

            if (!customDynamicDropExists && !staticCustomItemsExist) {

                return;

            }

            if (!customDynamicDropExists && staticCustomItemsExist) {

                dropCustomStaticLoot(entity);
                return;

            }

            if (customDynamicDropExists && !staticCustomItemsExist) {

                dropCustomDynamicLoot(mobLevel, entity);

            }

            if (customDynamicDropExists && staticCustomItemsExist) {

                HashMap<String, Double> weighedConfigValues = new HashMap<>();
                weighedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT));
                weighedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT));

                String selectedLootSystem = pickWeighedLootSystem(weighedConfigValues);

                if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT))
                    dropCustomDynamicLoot(mobLevel, entity);
                if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT))
                    dropCustomStaticLoot(entity);

                return;

            }

        }

        if (proceduralItemsOn && customItemsOn) {

            if (!customDynamicDropExists && !staticCustomItemsExist) {

                dropProcedurallyGeneratedItem(mobLevel, entity);
                return;

            }

            if (!customDynamicDropExists && staticCustomItemsExist) {

                HashMap<String, Double> weighedConfigValues = new HashMap<>();
                weighedConfigValues.put(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT));
                weighedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT));

                String selectedLootSystem = pickWeighedLootSystem(weighedConfigValues);

                if (selectedLootSystem.equals(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT))
                    dropProcedurallyGeneratedItem(mobLevel, entity);
                if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT))
                    dropCustomStaticLoot(entity);

                return;

            }

            if (customDynamicDropExists && !staticCustomItemsExist) {

                HashMap<String, Double> weighedConfigValues = new HashMap<>();
                weighedConfigValues.put(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT));
                weighedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT));

                String selectedLootSystem = pickWeighedLootSystem(weighedConfigValues);

                if (selectedLootSystem.equals(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT))
                    dropProcedurallyGeneratedItem(mobLevel, entity);
                if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT))
                    dropCustomDynamicLoot(mobLevel, entity);

                return;

            }

            if (customDynamicDropExists && staticCustomItemsExist) {

                HashMap<String, Double> weighedConfigValues = new HashMap<>();
                weighedConfigValues.put(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT));
                weighedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT));
                weighedConfigValues.put(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT, ConfigValues.itemsDropSettingsConfig.getDouble(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT));

                String selectedLootSystem = pickWeighedLootSystem(weighedConfigValues);

                if (selectedLootSystem.equals(ItemsDropSettingsConfig.PROCEDURAL_ITEM_WEIGHT))
                    dropProcedurallyGeneratedItem(mobLevel, entity);
                if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_DYNAMIC_ITEM_WEIGHT))
                    dropCustomDynamicLoot(mobLevel, entity);
                if (selectedLootSystem.equals(ItemsDropSettingsConfig.CUSTOM_STATIC_ITEM_WEIGHT))
                    dropCustomStaticLoot(entity);

                return;

            }

        }

    }

    private String pickWeighedLootSystem(HashMap<String, Double> weighedConfigValues) {

        double totalWeight = 0;

        for (String string : weighedConfigValues.keySet()) {

            totalWeight += weighedConfigValues.get(string);

        }

        String selectedLootSystem = null;
        double random = Math.random() * totalWeight;

        for (String string : weighedConfigValues.keySet()) {

            random -= weighedConfigValues.get(string);


            if (random <= 0) {

                selectedLootSystem = string;

                break;

            }

        }

        return selectedLootSystem;

    }

    private void dropCustomDynamicLoot(int mobLevel, Entity entity) {

        double targetItemWorth = ItemWorthCalculator.targetItemWorth(mobLevel);
        int itemRank = (int) (targetItemWorth / 10);

        int randomCustomDrop = random.nextInt(CustomItemConstructor.dynamicRankedItemStacks.get(itemRank).size());

        //get rank matching randomizer and item matching randomized index
        entity.getWorld().dropItem(entity.getLocation(), CustomItemConstructor.dynamicRankedItemStacks.get(itemRank).get(randomCustomDrop));

    }

    private void dropCustomStaticLoot(Entity entity) {

        double totalWeight = 0;

        for (ItemStack itemStack : CustomItemConstructor.staticCustomItemHashMap.keySet()) {

            totalWeight += CustomItemConstructor.staticCustomItemHashMap.get(itemStack);

        }

        ItemStack generatedItemStack = null;
        double random = Math.random() * totalWeight;

        for (ItemStack itemStack : CustomItemConstructor.staticCustomItemHashMap.keySet()) {

            random -= CustomItemConstructor.staticCustomItemHashMap.get(itemStack);

            if (random <= 0) {

                generatedItemStack = itemStack;
                break;

            }

        }

        entity.getWorld().dropItem(entity.getLocation(), generatedItemStack);

    }

    private void dropProcedurallyGeneratedItem(int mobLevel, Entity entity) {

        ProceduralItemGenerator proceduralItemGenerator = new ProceduralItemGenerator();
        ItemStack randomLoot = proceduralItemGenerator.proceduralItemGenerator(mobLevel, entity);

        entity.getWorld().dropItem(entity.getLocation(), randomLoot);

    }

}
