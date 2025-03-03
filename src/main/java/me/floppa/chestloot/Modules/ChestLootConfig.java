package me.floppa.chestloot.Modules;

import net.minecraft.core.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ChestLootConfig {
    // The common configuration spec
    public static final ForgeConfigSpec COMMON_CONFIG;

    // Example configuration option
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LootTable;

    public static final ForgeConfigSpec.ConfigValue<Integer> amountOfRareItems;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> chestsPositions;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> Admins;

    public static final ForgeConfigSpec.ConfigValue<Integer> delayOnRespawn;

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
                        "minecraft:cooked_beef 9",
                        "minecraft:golden_apple",
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
        amountOfRareItems = builder
                .comment("Amount of Rare Items from end of list, others are default, decrease the value to 1 (It's required to be after the last item)")
                .define("amountOfRareItems",17,value -> value instanceof Integer);
        chestsPositions = builder
                .comment("Positions of chests to spawn on map (format: x,y,z)")
                .define("chestsPositions", List.of(), value -> value instanceof List);
        Admins = builder
                .comment("Add some admins")
                .define("admins",List.of(),value -> value instanceof List);
        delayOnRespawn = builder
                .comment("Delay for respawning chests")
                .define("delayOnRespawn",14000,value -> value instanceof Integer);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
