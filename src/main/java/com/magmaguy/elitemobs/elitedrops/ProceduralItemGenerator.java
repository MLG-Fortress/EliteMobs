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

import com.magmaguy.elitemobs.ChatColorConverter;
import com.magmaguy.elitemobs.MetadataHandler;
import com.magmaguy.elitemobs.config.ConfigValues;
import com.magmaguy.elitemobs.config.DefaultConfig;
import com.magmaguy.elitemobs.config.EconomySettingsConfig;
import com.magmaguy.elitemobs.config.ItemsProceduralSettingsConfig;
import com.magmaguy.elitemobs.mobcustomizer.DamageAdjuster;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by MagmaGuy on 04/06/2017.
 */
public class ProceduralItemGenerator {

    private static ArrayList<String> nouns = (ArrayList<String>) ConfigValues.itemsProceduralSettingsConfig.getList("Valid nouns");
    private static ArrayList<String> adjectives = (ArrayList<String>) ConfigValues.itemsProceduralSettingsConfig.getList("Valid adjectives");
    private static ArrayList<String> verbs = (ArrayList<String>) ConfigValues.itemsProceduralSettingsConfig.getList("Valid verbs");
    private static ArrayList<String> verbers = (ArrayList<String>) ConfigValues.itemsProceduralSettingsConfig.getList("Valid verb-er (noun)");
    HashMap<Enchantment, Integer> validEnchantments = new HashMap();
    private Random random = new Random();

    /*
    In order to create an item, check the level of the mob and add just slightly more value to an item than would be necessary
    to defeat it assuming that every enchantment is combat-oriented.
     */

    public ItemStack proceduralItemGenerator(int mobLevel, Entity entity) {

        //Create itemstack, generate material
        ItemStack proceduralItem = new ItemStack(randomMaterialConstructor(mobLevel), 1);
        ItemMeta itemMeta = proceduralItem.getItemMeta();

        //Apply item name
        itemMeta.setDisplayName(ChatColorConverter.chatColorConverter(randomItemNameConstructor(proceduralItem.getType())));

        //Apply enchantments
        itemMeta = randomItemEnchantmentConstructor(proceduralItem.getType(), itemMeta, mobLevel);

        proceduralItem.setItemMeta(itemMeta);

        //Apply lore
        itemMeta.setLore(proceduralItemLoreConstructor(proceduralItem, entity));

        proceduralItem.setItemMeta(itemMeta);

        ItemQuality.dropQualityColorizer(proceduralItem);

        if (ConfigValues.itemsProceduralSettingsConfig.getBoolean(ItemsProceduralSettingsConfig.MONITOR_ITEMS_ON_CONSOLE)) {

            Bukkit.getLogger().info("[EliteMobs] Procedurally generated item with the following attributes:");
            Bukkit.getLogger().info("[EliteMobs] Item type: " + proceduralItem.getType());
            Bukkit.getLogger().info("[EliteMobs] Item name: " + proceduralItem.getItemMeta().getDisplayName());
            Bukkit.getLogger().info("[EliteMobs] Item lore: " + proceduralItem.getItemMeta().getLore().get(0));
            Bukkit.getLogger().info("[EliteMobs] Item enchantments:");

            for (Map.Entry<Enchantment, Integer> entry : proceduralItem.getItemMeta().getEnchants().entrySet()) {

                Bukkit.getLogger().info(entry.getKey() + " level " + entry.getValue());

            }

        }

        //Add hidden lore for shops to validate
        ObfuscatedSignatureLoreData.obfuscateSignatureData(proceduralItem);

        if (ConfigValues.defaultConfig.getBoolean(DefaultConfig.HIDE_ENCHANTMENTS_ATTRIBUTE))
            EnchantmentHider.hideEnchantments(proceduralItem);

        return proceduralItem;

    }

    public ItemStack randomItemGeneratorCommand(int mobLevel) {

        Entity entity = null;

        return proceduralItemGenerator(mobLevel, entity);

    }

    private Material randomMaterialConstructor(int mobLevel) {

        List<Material> validMaterials = new ArrayList<>();

        for (Object object : ConfigValues.itemsProceduralSettingsConfig.getList(ItemsProceduralSettingsConfig.PROCEDURAL_ITEM_VALID_MATERIALS)) {

            try {

                Material parsedMaterial = Material.getMaterial(object.toString());
                validMaterials.add(parsedMaterial);

            } catch (Exception e) {

                Bukkit.getLogger().info("Invalid material type detected: " + object.toString());

            }


        }

        if (mobLevel < DamageAdjuster.DIAMOND_TIER_LEVEL) {

            validMaterials.remove(Material.DIAMOND);
            validMaterials.remove(Material.DIAMOND_AXE);
            validMaterials.remove(Material.DIAMOND_BARDING);
            validMaterials.remove(Material.DIAMOND_BLOCK);
            validMaterials.remove(Material.DIAMOND_CHESTPLATE);
            validMaterials.remove(Material.DIAMOND_HELMET);
            validMaterials.remove(Material.DIAMOND_HOE);
            validMaterials.remove(Material.DIAMOND_LEGGINGS);
            validMaterials.remove(Material.DIAMOND_ORE);
            validMaterials.remove(Material.DIAMOND_PICKAXE);
            validMaterials.remove(Material.DIAMOND_SPADE);
            validMaterials.remove(Material.DIAMOND_SWORD);

        }

        if (mobLevel < DamageAdjuster.IRON_TIER_LEVEL) {

            validMaterials.remove(Material.IRON_AXE);
            validMaterials.remove(Material.IRON_BARDING);
            validMaterials.remove(Material.IRON_BLOCK);
            validMaterials.remove(Material.IRON_BOOTS);
            validMaterials.remove(Material.IRON_CHESTPLATE);
            validMaterials.remove(Material.IRON_HELMET);
            validMaterials.remove(Material.IRON_HOE);
            validMaterials.remove(Material.IRON_INGOT);
            validMaterials.remove(Material.IRON_LEGGINGS);
            validMaterials.remove(Material.IRON_NUGGET);
            validMaterials.remove(Material.IRON_ORE);
            validMaterials.remove(Material.IRON_PICKAXE);
            validMaterials.remove(Material.IRON_SPADE);
            validMaterials.remove(Material.IRON_SWORD);
            validMaterials.remove(Material.IRON_BOOTS);

        }

        if (mobLevel < DamageAdjuster.STONE_CHAIN_TIER_LEVEL) {

            validMaterials.remove(Material.CHAINMAIL_BOOTS);
            validMaterials.remove(Material.CHAINMAIL_CHESTPLATE);
            validMaterials.remove(Material.CHAINMAIL_HELMET);
            validMaterials.remove(Material.CHAINMAIL_LEGGINGS);
            validMaterials.remove(Material.STONE_SWORD);
            validMaterials.remove(Material.STONE_HOE);
            validMaterials.remove(Material.STONE_SPADE);
            validMaterials.remove(Material.STONE_PICKAXE);
            validMaterials.remove(Material.STONE_AXE);

        }

        int index = random.nextInt(validMaterials.size());

        Material material = validMaterials.get(index);

        return material;

    }

    private String randomItemNameConstructor(Material material) {

        String finalName = "";

        int nounConstructorSelector = random.nextInt(7) + 1;

        if (nounConstructorSelector == 1) {

            finalName = verbTypeAdjectiveNoun(material);

        } else if (nounConstructorSelector == 2) {

            finalName = typeAdjectiveNoun(material);

        } else if (nounConstructorSelector == 3) {

            finalName = nounVerbType(material);

        } else if (nounConstructorSelector == 4) {

            finalName = verbType(material);

        } else if (nounConstructorSelector == 5) {

            finalName = adjectiveVerbType(material);

        } else if (nounConstructorSelector == 6) {

            finalName = articleVerber();

        } else if (nounConstructorSelector == 7) {

            finalName = articleAdjectiveVerber();

        }

        return finalName;

    }

    private String verbTypeAdjectiveNoun(Material material) {

        String randomVerb = verbs.get(random.nextInt(verbs.size()));
        String itemType = itemTypeStringParser(material);
        String randomAdjective = adjectives.get(random.nextInt(adjectives.size()));
        String randomNoun = nouns.get(random.nextInt(nouns.size()));

        String finalName = randomVerb + " " + itemType + " " + "of the" + " " + randomAdjective + " " + randomNoun;

        return finalName;

    }

    private String typeAdjectiveNoun(Material material) {

        String itemType = itemTypeStringParser(material);
        String randomAdjective = adjectives.get(random.nextInt(adjectives.size()));
        String randomNoun = nouns.get(random.nextInt(nouns.size()));

        String finalName = itemType + " " + "of the" + " " + randomAdjective + " " + randomNoun;

        return finalName;

    }

    private String nounVerbType(Material material) {

        String randomNoun = nouns.get(random.nextInt(nouns.size()));
        String randomAdjective = adjectives.get(random.nextInt(adjectives.size()));
        String randomVerb = verbs.get(random.nextInt(verbs.size()));
        String itemType = itemTypeStringParser(material);

        String finalName = randomNoun + "'s" + " " + randomAdjective + " " + randomVerb + " " + itemType;

        return finalName;

    }

    private String verbType(Material material) {

        String randomVerb = verbs.get(random.nextInt(verbs.size()));
        String itemType = itemTypeStringParser(material);

        String finalName = randomVerb + " " + itemType;

        return finalName;

    }

    private String adjectiveVerbType(Material material) {

        String randomAdjective = adjectives.get(random.nextInt(adjectives.size()));
        String randomVerb = verbs.get(random.nextInt(verbs.size()));
        String itemType = itemTypeStringParser(material);

        String finalName = randomAdjective + " " + randomVerb + " " + itemType;

        return finalName;

    }

    private String articleVerber() {

        String article = "The";
        String randomVerber = verbers.get(random.nextInt(verbers.size()));

        String finalName = article + " " + randomVerber;

        return finalName;

    }

    private String articleAdjectiveVerber() {

        String article = "The";
        String randomAdjective = adjectives.get(random.nextInt(adjectives.size()));
        String randomVerber = verbers.get(random.nextInt(verbers.size()));

        String finalName = article + " " + randomAdjective + " " + randomVerber;

        return finalName;

    }

    private String itemTypeStringParser(Material material) {

        if (material.equals(Material.DIAMOND_SWORD) || material.equals(Material.GOLD_SWORD) ||
                material.equals(Material.IRON_SWORD) || material.equals(Material.STONE_SWORD) ||
                material.equals(Material.WOOD_SWORD)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Sword");

        } else if (material.equals(Material.BOW)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Bow");

        } else if (material.equals(Material.DIAMOND_PICKAXE) || material.equals(Material.GOLD_PICKAXE) ||
                material.equals(Material.IRON_PICKAXE) || material.equals(Material.STONE_PICKAXE) ||
                material.equals(Material.WOOD_PICKAXE)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Pickaxe");

        } else if (material.equals(Material.DIAMOND_SPADE) || material.equals(Material.GOLD_SPADE) ||
                material.equals(Material.IRON_SPADE) || material.equals(Material.STONE_SPADE) ||
                material.equals(Material.WOOD_SPADE)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Spade");

        } else if (material.equals(Material.DIAMOND_HOE) || material.equals(Material.GOLD_HOE) ||
                material.equals(Material.IRON_HOE) || material.equals(Material.STONE_HOE) ||
                material.equals(Material.WOOD_HOE)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Hoe");

        } else if (material.equals(Material.DIAMOND_AXE) || material.equals(Material.GOLD_AXE) ||
                material.equals(Material.IRON_AXE) || material.equals(Material.STONE_AXE) ||
                material.equals(Material.WOOD_AXE)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Axe");

        } else if (material.equals(Material.CHAINMAIL_HELMET) || material.equals(Material.DIAMOND_HELMET) ||
                material.equals(Material.GOLD_HELMET) || material.equals(Material.IRON_HELMET) ||
                material.equals(Material.LEATHER_HELMET)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Helmet");

        } else if (material.equals(Material.CHAINMAIL_CHESTPLATE) || material.equals(Material.DIAMOND_CHESTPLATE) ||
                material.equals(Material.GOLD_CHESTPLATE) || material.equals(Material.IRON_CHESTPLATE) ||
                material.equals(Material.LEATHER_CHESTPLATE)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Chestplate");

        } else if (material.equals(Material.CHAINMAIL_LEGGINGS) || material.equals(Material.DIAMOND_LEGGINGS) ||
                material.equals(Material.GOLD_LEGGINGS) || material.equals(Material.IRON_LEGGINGS) ||
                material.equals(Material.LEATHER_LEGGINGS)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Leggings");

        } else if (material.equals(Material.CHAINMAIL_BOOTS) || material.equals(Material.DIAMOND_BOOTS) ||
                material.equals(Material.GOLD_BOOTS) || material.equals(Material.IRON_BOOTS) ||
                material.equals(Material.LEATHER_BOOTS)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Boots");

        } else if (material.equals(Material.SHEARS)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Shears");

        } else if (material.equals(Material.FISHING_ROD)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Fishing Rod");

        } else if (material.equals(Material.SHIELD)) {

            return ConfigValues.itemsProceduralSettingsConfig.getString("Material name.Shield");

        }

        Bukkit.getLogger().info("EliteMobs - found unexpected material type in procedurally generated loot. Can't generate item type name.");
        return "";

    }

    private List<String> proceduralItemLoreConstructor(ItemStack itemStack, Entity entity) {

        String line1 = "";

        if (entity != null) {

            line1 = ConfigValues.itemsProceduralSettingsConfig.getString(ItemsProceduralSettingsConfig.LORE_MOB_LEVEL_SOURCE).replace("$level",
                    entity.getMetadata(MetadataHandler.ELITE_MOB_MD).get(0).asInt() + "");

            String newName = "";

            if (entity.getType().name().contains("_")) {

                List<String> tempSubList = Arrays.asList(entity.getType().name().split("_"));

                for (String string : tempSubList) {

                    string = string.toLowerCase().substring(0, 1).toUpperCase() + " ";
                    newName += string;

                }

            } else {

                newName = entity.getType().name().substring(0, 1) + entity.getType().name().substring(1).toLowerCase();

            }

            line1 = line1.replace("$mob", newName);


        } else {

            line1 = ConfigValues.itemsProceduralSettingsConfig.getString(ItemsProceduralSettingsConfig.LORE_SHOP_SOURCE);

        }

        String line2 = "";

        if (ConfigValues.economyConfig.getBoolean(EconomySettingsConfig.ENABLE_ECONOMY)) {

            String itemWorth = ItemWorthCalculator.determineItemWorth(itemStack) + "";
            line2 = ConfigValues.itemsProceduralSettingsConfig.getString(ItemsProceduralSettingsConfig.LORE_WORTH).replace("$currencyName",
                    ConfigValues.economyConfig.getString(EconomySettingsConfig.CURRENCY_NAME));
            line2 = line2.replace("$worth", itemWorth);

        }

        String line3 = ConfigValues.itemsProceduralSettingsConfig.getString(ItemsProceduralSettingsConfig.LORE_SIGNATURE);

        String loreStructure = ConfigValues.itemsProceduralSettingsConfig.getString(ItemsProceduralSettingsConfig.LORE_STRUCTURE);

        if (line1.length() > 0) {

            loreStructure = loreStructure.replace("$line1", line1);

        } else {


            loreStructure = loreStructure.replace("$line1", "");

        }

        if (line2.length() > 0) {


            loreStructure = loreStructure.replace("$line2", line2);

        } else {


            loreStructure = loreStructure.replace("$line2", "");

        }

        if (line3.length() > 0) {


            loreStructure = loreStructure.replace("$line3", line3);

        } else {

            loreStructure = loreStructure.replace("$line3", "");

        }

        List<String> lore = Arrays.asList(loreStructure.split("\n"));

        return lore;

    }

    private ItemMeta randomItemEnchantmentConstructor(Material material, ItemMeta oldMeta, int mobLevel) {

        if (material.equals(Material.DIAMOND_SWORD) || material.equals(Material.GOLD_SWORD) ||
                material.equals(Material.IRON_SWORD) || material.equals(Material.STONE_SWORD) ||
                material.equals(Material.WOOD_SWORD)) {

            validateEnchantment("DAMAGE_ALL");
            validateEnchantment("DAMAGE_ARTHROPODS");
            validateEnchantment("DAMAGE_UNDEAD");
            validateEnchantment("DURABILITY");
            validateEnchantment("FIRE_ASPECT");
            validateEnchantment("KNOCKBACK");
            validateEnchantment("LOOT_BONUS_MOBS");
            validateEnchantment("MENDING");
            validateEnchantment("SWEEPING_EDGE");
            validateEnchantment("VANISHING_CURSE");

        } else if (material.equals(Material.BOW)) {

            validateEnchantment("ARROW_DAMAGE");
            validateEnchantment("ARROW_FIRE");
            validateEnchantment("ARROW_INFINITE");
            validateEnchantment("ARROW_KNOCKBACK");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("VANISHING_CURSE");

        } else if (material.equals(Material.DIAMOND_PICKAXE) || material.equals(Material.GOLD_PICKAXE) ||
                material.equals(Material.IRON_PICKAXE) || material.equals(Material.STONE_PICKAXE) ||
                material.equals(Material.WOOD_PICKAXE)) {

            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("DIG_SPEED");
            //TODO: this doesn't take config into account
            if (random.nextDouble() < 0.5) {
                validateEnchantment("LOOT_BONUS_BLOCKS");
            } else {
                validateEnchantment("SILK_TOUCH");
            }

        } else if (material.equals(Material.DIAMOND_SPADE) || material.equals(Material.GOLD_SPADE) ||
                material.equals(Material.IRON_SPADE) || material.equals(Material.STONE_SPADE) ||
                material.equals(Material.WOOD_SPADE)) {

            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("DIG_SPEED");
            if (random.nextDouble() < 0.5) {
                validateEnchantment("LOOT_BONUS_BLOCKS");
            } else {
                validateEnchantment("SILK_TOUCH");
            }

        } else if (material.equals(Material.DIAMOND_HOE) || material.equals(Material.GOLD_HOE) ||
                material.equals(Material.IRON_HOE) || material.equals(Material.STONE_HOE) ||
                material.equals(Material.WOOD_HOE) || material.equals(Material.SHIELD)) {

            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("VANISHING_CURSE");

        } else if (material.equals(Material.DIAMOND_AXE) || material.equals(Material.GOLD_AXE) ||
                material.equals(Material.IRON_AXE) || material.equals(Material.STONE_AXE) ||
                material.equals(Material.WOOD_AXE)) {

            validateEnchantment("DAMAGE_ALL");
            validateEnchantment("DAMAGE_ARTHROPODS");
            validateEnchantment("DAMAGE_UNDEAD");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("DIG_SPEED");
            validateEnchantment("LOOT_BONUS_BLOCKS");

        } else if (material.equals(Material.CHAINMAIL_HELMET) || material.equals(Material.DIAMOND_HELMET) ||
                material.equals(Material.GOLD_HELMET) || material.equals(Material.IRON_HELMET) ||
                material.equals(Material.LEATHER_HELMET)) {

            validateEnchantment("BINDING_CURSE");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("OXYGEN");
            validateEnchantment("PROTECTION_ENVIRONMENTAL");
            validateEnchantment("PROTECTION_EXPLOSIONS");
            validateEnchantment("PROTECTION_FIRE");
            validateEnchantment("PROTECTION_PROJECTILE");
            validateEnchantment("THORNS");
            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("WATER_WORKER");

        } else if (material.equals(Material.CHAINMAIL_CHESTPLATE) || material.equals(Material.DIAMOND_CHESTPLATE) ||
                material.equals(Material.GOLD_CHESTPLATE) || material.equals(Material.IRON_CHESTPLATE) ||
                material.equals(Material.LEATHER_CHESTPLATE)) {

            validateEnchantment("BINDING_CURSE");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("PROTECTION_ENVIRONMENTAL");
            validateEnchantment("PROTECTION_EXPLOSIONS");
            validateEnchantment("PROTECTION_FIRE");
            validateEnchantment("PROTECTION_PROJECTILE");
            validateEnchantment("THORNS");
            validateEnchantment("VANISHING_CURSE");

        } else if (material.equals(Material.CHAINMAIL_LEGGINGS) || material.equals(Material.DIAMOND_LEGGINGS) ||
                material.equals(Material.GOLD_LEGGINGS) || material.equals(Material.IRON_LEGGINGS) ||
                material.equals(Material.LEATHER_LEGGINGS)) {

            validateEnchantment("BINDING_CURSE");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("PROTECTION_ENVIRONMENTAL");
            validateEnchantment("PROTECTION_EXPLOSIONS");
            validateEnchantment("PROTECTION_FIRE");
            validateEnchantment("PROTECTION_PROJECTILE");
            validateEnchantment("THORNS");
            validateEnchantment("VANISHING_CURSE");

        } else if (material.equals(Material.CHAINMAIL_BOOTS) || material.equals(Material.DIAMOND_BOOTS) ||
                material.equals(Material.GOLD_BOOTS) || material.equals(Material.IRON_BOOTS) ||
                material.equals(Material.LEATHER_BOOTS)) {

            validateEnchantment("BINDING_CURSE");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("PROTECTION_ENVIRONMENTAL");
            validateEnchantment("PROTECTION_EXPLOSIONS");
            validateEnchantment("PROTECTION_FALL");
            validateEnchantment("PROTECTION_FIRE");
            validateEnchantment("PROTECTION_PROJECTILE");
            validateEnchantment("THORNS");
            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("DEPTH_STRIDER");
            validateEnchantment("FROST_WALKER");


        } else if (material.equals(Material.FISHING_ROD)) {

            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("DURABILITY");
            validateEnchantment("MENDING");
            validateEnchantment("LUCK");
            validateEnchantment("LURE");

        } else if (material.equals(Material.SHEARS)) {

            validateEnchantment("VANISHING_CURSE");
            validateEnchantment("DIG_SPEED");
            validateEnchantment("MENDING");
            validateEnchantment("DURABILITY");

        }

        if (validEnchantments.size() == 0) {

            return oldMeta;

        }

        ItemMeta newMeta = oldMeta;

        HashMap<Enchantment, Integer> validEnchantmentsClone = (HashMap<Enchantment, Integer>) validEnchantments.clone();

        /*
        Take item worth into account
         */

        double targetItemWorth = ItemWorthCalculator.targetItemWorth(mobLevel);

        double materialWorth = ItemWorthCalculator.itemTypeWorth(material);

        double itemWorthLeft = targetItemWorth - materialWorth;

        while (itemWorthLeft > 0) {

            if (validEnchantments.size() < 1) {

                break;

            }

            int randomIndex = random.nextInt(validEnchantments.size());

            List<Enchantment> enchantmentList = new ArrayList();

            for (Enchantment enchantment : validEnchantments.keySet()) {

                enchantmentList.add(enchantment);

            }

            String enchantmentString = enchantmentList.get(randomIndex).getName();

            Enchantment enchantment = enchantmentList.get(randomIndex);

            validEnchantments.put(enchantment, validEnchantments.get(enchantment) - 1);

            if (ConfigValues.itemsProceduralSettingsConfig.contains("Valid Enchantments." + enchantmentString + ".Max Level")) {

                int finalEnchantLevel = validEnchantmentsClone.get(enchantment) - validEnchantments.get(enchantment);

                newMeta.addEnchant(enchantment, finalEnchantLevel, true);
                itemWorthLeft -= ItemWorthCalculator.enchantmentWorthGetter(enchantment);

            } else {

                newMeta.addEnchant(enchantment, 1, true);
                itemWorthLeft -= ItemWorthCalculator.enchantmentWorthGetter(enchantment);

            }

            int newEnchantInt = validEnchantments.get(enchantment);

            if (newEnchantInt == 0) {

                validEnchantments.remove(enchantment);

            }

        }

        //randomizer for enchantments
//        for (int i = 0; i < mobLevel; i++) {
//
//            if (validEnchantments.size() < 1) {
//
//                break;
//
//            }
//
//            int randomIndex = random.nextInt(validEnchantments.size());
//
//            List<Enchantment> enchantmentList = new ArrayList();
//
//            for (Enchantment enchantment : validEnchantments.keySet()) {
//
//                enchantmentList.add(enchantment);
//
//            }
//
//            String enchantmentString = enchantmentList.get(randomIndex).getName();
//
//            Enchantment enchantment = enchantmentList.get(randomIndex);
//
//            validEnchantments.put(enchantment, validEnchantments.get(enchantment) - 1);
//
//            if (ConfigValues.itemsProceduralSettingsConfig.contains("Valid Enchantments." + enchantmentString + ".Max Level")) {
//
//                int finalEnchantLevel = validEnchantmentsClone.get(enchantment) - validEnchantments.get(enchantment);
//
//                newMeta.addEnchant(enchantment, finalEnchantLevel, true);
//
//            } else {
//
//                newMeta.addEnchant(enchantment, 1, true);
//
//            }
//
//            int newEnchantInt = validEnchantments.get(enchantment);
//
//            if (newEnchantInt == 0) {
//
//                validEnchantments.remove(enchantment);
//
//            }
//
//        }

        return newMeta;

    }

    private void validateEnchantment(String string) {

        String mainString = "Valid Enchantments." + string;

        if (ConfigValues.itemsProceduralSettingsConfig.getBoolean(mainString + ".Allow")) {

            int enchantmentLevel;

            if (ConfigValues.itemsProceduralSettingsConfig.contains(mainString + ".Max Level")) {

                enchantmentLevel = ConfigValues.itemsProceduralSettingsConfig.getInt(mainString + ".Max Level");

            } else {

                enchantmentLevel = 1;

            }

            //index for random getting
            validEnchantments.put(Enchantment.getByName(string), enchantmentLevel);

        }

    }

}
