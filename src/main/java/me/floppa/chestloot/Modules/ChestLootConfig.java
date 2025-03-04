package me.floppa.chestloot.Modules;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChestLootConfig {
    // The common configuration spec
    public static final ForgeConfigSpec COMMON_CONFIG;

    static Random rand = new Random();

    // Example configuration option
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LootTable;

    // Amount of items for give

    public static final ForgeConfigSpec.ConfigValue<Integer> amountOfWeaponItems;

    public static final ForgeConfigSpec.ConfigValue<Integer> amountOfAmmoItems;

    public static final ForgeConfigSpec.ConfigValue<Integer> amountOfArmorItems;

    public static final ForgeConfigSpec.ConfigValue<Integer> amountOfOtherItems;

    //

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> chestsPositions;

    public static final ForgeConfigSpec.ConfigValue<Integer> delayOnRespawn;

    // Procents
    public static final ForgeConfigSpec.ConfigValue<Double> procentOnWeapon;

    public static final ForgeConfigSpec.ConfigValue<Double> procentOnAmmo;

    public static final ForgeConfigSpec.ConfigValue<Double> procentOnArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> procentOnOthers;

    //

    public static final ForgeConfigSpec.ConfigValue<Integer> amountToGiveMin;

    public static final ForgeConfigSpec.ConfigValue<Integer> amountToGiveMax;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("General Settings");
        // Define a boolean config option; default value is true
        LootTable = builder
                .comment("Define or adjust items for drop from chests")
                .define("LootTable", Arrays.asList("minecraft:air",
                        "securitycraft:keypad_chest",
                        "securitycraft:camera_monitor",
                        "securitycraft:remote_access_mine",
                        "securitycraft:stone_mine",
                        "securitycraft:mine",
                        "peterswarfare:flashbang",
                        "peterswarfare:fraggrenade",
                        "peterswarfare:smokegrenade",
                        "minecraft:cooked_beef 9",
                        "minecraft:golden_apple",
                        "tacz:attachment{\"AttachmentId\":\"tacz:muzzle_silencer_knight_qd\"}",
                        "tacz:attachment{\"AttachmentId\":\"gucci_attachments:scope_karrina\"}",
                        "tacz:attachment{\"AttachmentId\":\"tacz:sight_uh1\"}",
                        "tacz:attachment{\"AttachmentId\":\"gucci_attachments:grip_vert2\"}",
                        "tacz:ammo{\"AmmoId\":\"tacz:12g\"} 36",
                        "tacz:ammo{\"AmmoId\":\"tacz:338\"} 30",
                        "tacz:ammo{\"AmmoId\":\"tacz:40mm\"} 6",
                        "tacz:ammo{\"AmmoId\":\"tacz:556x45\"} 60",
                        "tacz:ammo{\"AmmoId\":\"tacz:9mm\"} 60",
                        "tacz:ammo{\"AmmoId\":\"tacz:762x39\"} 60",
                        "tacz:ammo{\"AmmoId\":\"tacz:308\"} 48",
                        "tacz:ammo{\"AmmoId\":\"tacz:12g\"} 36",
                        "tacz:ammo{\"AmmoId\":\"tacz:50ae\"} 36",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:m16a4\",\"GunCurrentAmmoCount\": 31,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"SEMI\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:m320\",\"GunCurrentAmmoCount\": 1,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"SEMI\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:ak47\",\"GunCurrentAmmoCount\": 31,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"AUTO\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:m4a1\",\"GunCurrentAmmoCount\": 30,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"AUTO\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:aug\",\"GunCurrentAmmoCount\": 30,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"AUTO\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:ai_awp\",\"HasBulletInBarrel\": 1b,\"\"GunFireMode\": \"SEMI\",\"GunCurrentAmmoCount\": 5}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:deagle\",\"GunCurrentAmmoCount\": 8,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"SEMI\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:m870\",\"GunCurrentAmmoCount\": 6,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"SEMI\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:scar_l\",\"GunCurrentAmmoCount\": 31,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"SEMI\"}",
                        "tacz:modern_kinetic_gun{\"GunId\":\"tacz:hk_g3\",\"GunCurrentAmmoCount\": 21,\"HasBulletInBarrel\": 1b,\"GunFireMode\": \"SEMI\"}",
                        "minecraft:netherite_helmet",
                        "minecraft:netherite_chestplate",
                        "minecraft:netherite_leggings",
                        "minecraft:netherite_boots",
                        "minecraft:diamond_helmet",
                        "minecraft:diamond_chestplate",
                        "minecraft:diamond_leggings",
                        "minecraft:diamond_boots"),value -> value instanceof List);
        // Amount of items for give

        amountOfWeaponItems = builder
                .comment("Amount of Rare Items from end of list, must look like this Others,ammo,weapons,armors")
                .define("amountOfWeaponItems",10,value -> value instanceof Integer);

        amountOfAmmoItems = builder
                .define("amountOfAmmoItems",9,value -> value instanceof Integer);

        amountOfArmorItems = builder
                .define("amountOfArmorItems",8,value -> value instanceof Integer);

        amountOfOtherItems = builder
                .define("amountOfOtherItems",11,value -> value instanceof Integer);

        //

        chestsPositions = builder
                .comment("Positions of chests to spawn on map (format: x,y,z)")
                .define("chestsPositions", List.of(), value -> value instanceof List);

        delayOnRespawn = builder
                .comment("Delay for respawning chests")
                .define("delayOnRespawn",14000,value -> value instanceof Integer);

        // Procents
        procentOnAmmo = builder
                .comment("Procent in non-full value, like 10% - 0.1, 20% - 0.2 and e.g")
                .define("procentOnAmmo",0.2,value -> value instanceof Double);
        procentOnWeapon = builder
                .define("procentOnWeapon",0.3,value -> value instanceof Double);
        procentOnArmor = builder
                .define("procentOnArmor",0.1,value -> value instanceof Double);
        procentOnOthers = builder
                .define("procentOnOthers",0.4,value -> value instanceof Double);

        //

        amountToGiveMin = builder
                .comment("Amount to give random items from chest")
                .define("amountToGiveMin",1,value -> value instanceof Integer);
        amountToGiveMax = builder
                .define("amountToGiveMax",3,value -> value instanceof Integer);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static String getRandomItem() {
        double generatedInt = rand.nextDouble();
        if(generatedInt < procentOnAmmo.get()) {
            return LootTable.get().get(rand.nextInt(LootTable.get().size()-amountOfAmmoItems.get()-amountOfWeaponItems.get()-amountOfArmorItems.get(),LootTable.get().size()-amountOfWeaponItems.get()-amountOfArmorItems.get()-1));
        } else if(generatedInt < procentOnWeapon.get()) {
            return LootTable.get().get(rand.nextInt(LootTable.get().size()-amountOfWeaponItems.get()-amountOfArmorItems.get(),LootTable.get().size()-amountOfArmorItems.get()-1));
        } else if(generatedInt < procentOnArmor.get()) {
            return LootTable.get().get(rand.nextInt(LootTable.get().size()-amountOfArmorItems.get()-1,LootTable.get().size()));
        } else if(generatedInt < procentOnOthers.get()) {
            return LootTable.get().get(rand.nextInt(0,LootTable.get().size()-amountOfAmmoItems.get()-amountOfWeaponItems.get()-amountOfArmorItems.get()-1));
        }
        return "minecraft:air";
    }
}
