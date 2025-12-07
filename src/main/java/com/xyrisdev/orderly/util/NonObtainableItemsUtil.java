package com.xyrisdev.orderly.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public final class NonObtainableItemsUtil {

	public static final Set<Material> materials = new HashSet<>();

	static {
		materials.add(Material.COMMAND_BLOCK);
		materials.add(Material.CHAIN_COMMAND_BLOCK);
		materials.add(Material.REPEATING_COMMAND_BLOCK);
		materials.add(Material.STRUCTURE_BLOCK);
		materials.add(Material.STRUCTURE_VOID);
		materials.add(Material.JIGSAW);
		materials.add(Material.BARRIER);

		materials.add(Material.DEBUG_STICK);
		materials.add(Material.KNOWLEDGE_BOOK);
		materials.add(Material.BUNDLE);

		materials.add(Material.ALLAY_SPAWN_EGG);
		materials.add(Material.AXOLOTL_SPAWN_EGG);
		materials.add(Material.BAT_SPAWN_EGG);
		materials.add(Material.BEE_SPAWN_EGG);
		materials.add(Material.BLAZE_SPAWN_EGG);
		materials.add(Material.BOGGED_SPAWN_EGG);
		materials.add(Material.BREEZE_SPAWN_EGG);
		materials.add(Material.CAMEL_SPAWN_EGG);
		materials.add(Material.CAT_SPAWN_EGG);
		materials.add(Material.CAVE_SPIDER_SPAWN_EGG);
		materials.add(Material.CHICKEN_SPAWN_EGG);
		materials.add(Material.COD_SPAWN_EGG);
		materials.add(Material.COW_SPAWN_EGG);
		materials.add(Material.CREAKING_SPAWN_EGG);
		materials.add(Material.CREEPER_SPAWN_EGG);
		materials.add(Material.DOLPHIN_SPAWN_EGG);
		materials.add(Material.DONKEY_SPAWN_EGG);
		materials.add(Material.DROWNED_SPAWN_EGG);
		materials.add(Material.ELDER_GUARDIAN_SPAWN_EGG);
		materials.add(Material.ENDER_DRAGON_SPAWN_EGG);
		materials.add(Material.ENDERMAN_SPAWN_EGG);
		materials.add(Material.ENDERMITE_SPAWN_EGG);
		materials.add(Material.EVOKER_SPAWN_EGG);
		materials.add(Material.FOX_SPAWN_EGG);
		materials.add(Material.FROG_SPAWN_EGG);
		materials.add(Material.GHAST_SPAWN_EGG);
		materials.add(Material.GLOW_SQUID_SPAWN_EGG);
		materials.add(Material.GOAT_SPAWN_EGG);
		materials.add(Material.GUARDIAN_SPAWN_EGG);
		materials.add(Material.HAPPY_GHAST_SPAWN_EGG);
		materials.add(Material.HOGLIN_SPAWN_EGG);
		materials.add(Material.HORSE_SPAWN_EGG);
		materials.add(Material.HUSK_SPAWN_EGG);
		materials.add(Material.IRON_GOLEM_SPAWN_EGG);
		materials.add(Material.LLAMA_SPAWN_EGG);
		materials.add(Material.MAGMA_CUBE_SPAWN_EGG);
		materials.add(Material.MOOSHROOM_SPAWN_EGG);
		materials.add(Material.MULE_SPAWN_EGG);
		materials.add(Material.OCELOT_SPAWN_EGG);
		materials.add(Material.PANDA_SPAWN_EGG);
		materials.add(Material.PARROT_SPAWN_EGG);
		materials.add(Material.PHANTOM_SPAWN_EGG);
		materials.add(Material.PIG_SPAWN_EGG);
		materials.add(Material.PIGLIN_SPAWN_EGG);
		materials.add(Material.PIGLIN_BRUTE_SPAWN_EGG);
		materials.add(Material.PILLAGER_SPAWN_EGG);
		materials.add(Material.POLAR_BEAR_SPAWN_EGG);
		materials.add(Material.PUFFERFISH_SPAWN_EGG);
		materials.add(Material.RABBIT_SPAWN_EGG);
		materials.add(Material.RAVAGER_SPAWN_EGG);
		materials.add(Material.SALMON_SPAWN_EGG);
		materials.add(Material.SHEEP_SPAWN_EGG);
		materials.add(Material.SHULKER_SPAWN_EGG);
		materials.add(Material.SILVERFISH_SPAWN_EGG);
		materials.add(Material.SKELETON_SPAWN_EGG);
		materials.add(Material.SKELETON_HORSE_SPAWN_EGG);
		materials.add(Material.SLIME_SPAWN_EGG);
		materials.add(Material.SNIFFER_SPAWN_EGG);
		materials.add(Material.SNOW_GOLEM_SPAWN_EGG);
		materials.add(Material.SPIDER_SPAWN_EGG);
		materials.add(Material.SQUID_SPAWN_EGG);
		materials.add(Material.STRAY_SPAWN_EGG);
		materials.add(Material.STRIDER_SPAWN_EGG);
		materials.add(Material.TADPOLE_SPAWN_EGG);
		materials.add(Material.TRADER_LLAMA_SPAWN_EGG);
		materials.add(Material.TROPICAL_FISH_SPAWN_EGG);
		materials.add(Material.TURTLE_SPAWN_EGG);
		materials.add(Material.VEX_SPAWN_EGG);
		materials.add(Material.VILLAGER_SPAWN_EGG);
		materials.add(Material.VINDICATOR_SPAWN_EGG);
		materials.add(Material.WANDERING_TRADER_SPAWN_EGG);
		materials.add(Material.WARDEN_SPAWN_EGG);
		materials.add(Material.WITCH_SPAWN_EGG);
		materials.add(Material.WITHER_SPAWN_EGG);
		materials.add(Material.WITHER_SKELETON_SPAWN_EGG);
		materials.add(Material.WOLF_SPAWN_EGG);
		materials.add(Material.ZOGLIN_SPAWN_EGG);
		materials.add(Material.ZOMBIE_SPAWN_EGG);
		materials.add(Material.ZOMBIE_HORSE_SPAWN_EGG);
		materials.add(Material.ZOMBIE_VILLAGER_SPAWN_EGG);
		materials.add(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);

		materials.add(Material.END_PORTAL_FRAME);
		materials.add(Material.DRAGON_EGG);
		materials.add(Material.BEDROCK);
		materials.add(Material.SPAWNER);
		materials.add(Material.REINFORCED_DEEPSLATE);

		// Special technical blocks
		materials.add(Material.PISTON_HEAD);
		materials.add(Material.MOVING_PISTON);
		materials.add(Material.NETHER_PORTAL);
		materials.add(Material.END_PORTAL);
		materials.add(Material.END_GATEWAY);
		materials.add(Material.BUBBLE_COLUMN);
		materials.add(Material.FIRE);
		materials.add(Material.SOUL_FIRE);
		materials.add(Material.FROSTED_ICE);
		materials.add(Material.BUDDING_AMETHYST);

		materials.add(Material.AIR);
		materials.add(Material.CAVE_AIR);
		materials.add(Material.VOID_AIR);

		materials.add(Material.TEST_BLOCK);
		materials.add(Material.TEST_INSTANCE_BLOCK);

		materials.add(Material.PETRIFIED_OAK_SLAB);

		materials.add(Material.INFESTED_STONE);
		materials.add(Material.INFESTED_COBBLESTONE);
		materials.add(Material.INFESTED_STONE_BRICKS);
		materials.add(Material.INFESTED_MOSSY_STONE_BRICKS);
		materials.add(Material.INFESTED_CRACKED_STONE_BRICKS);
		materials.add(Material.INFESTED_CHISELED_STONE_BRICKS);
		materials.add(Material.INFESTED_DEEPSLATE);
	}

	public static boolean nonObtainable(Material material) {
		return materials.contains(material);
	}

	@Contract(value = " -> new", pure = true)
	public static @NotNull Set<Material> materials() {
		return new HashSet<>(materials);
	}
}