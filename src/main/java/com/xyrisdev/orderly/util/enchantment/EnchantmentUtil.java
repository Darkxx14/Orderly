package com.xyrisdev.orderly.util.enchantment;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xyrisdev.orderly.util.enchantment.model.GroupedEnchantment;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

@UtilityClass
@SuppressWarnings("all")
public final class EnchantmentUtil {

	private static final Registry<Enchantment> REGISTRY = RegistryAccess
			.registryAccess()
			.getRegistry(RegistryKey.ENCHANTMENT);

	private static final Cache<Material, Boolean> ENCHANTABILITY_CACHE = Caffeine.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(15))
			.maximumSize(1024)
			.build();

	private static final Cache<Material, List<GroupedEnchantment>> APPLICABLE_CACHE = Caffeine.newBuilder()
			.expireAfterAccess(Duration.ofMinutes(15))
			.maximumSize(1024)
			.build();

	private static final String UNBREAKING_KEY = "minecraft:unbreaking";
	private static final String MENDING_KEY = "minecraft:mending";

	public static boolean enchantable(@NotNull ItemStack item) {
		if (item.getType().isAir()) {
			return false;
		}

		return Boolean.TRUE.equals(
				ENCHANTABILITY_CACHE.get(item.getType(), material ->
						REGISTRY.stream().anyMatch(e -> e.canEnchantItem(item))
				)
		);
	}

	public static boolean enchantable(@NotNull Material material) {
		if (material.isAir()) {
			return false;
		}

		return Boolean.TRUE.equals(
				ENCHANTABILITY_CACHE.get(material, mat ->
						REGISTRY.stream().anyMatch(e -> e.canEnchantItem(new ItemStack(mat)))
				)
		);
	}

	public static List<GroupedEnchantment> applicable(@NotNull ItemStack item) {
		if (item.getType().isAir()) {
			return List.of();
		}

		return APPLICABLE_CACHE.get(item.getType(), material ->
				REGISTRY.stream()
						.filter(e -> e.canEnchantItem(item))
						.filter(e -> {
							final String key = REGISTRY.getKey(e).asString();
							return !key.equals(UNBREAKING_KEY) && !key.equals(MENDING_KEY);
						})
						.map(e -> new GroupedEnchantment(e, e.getMaxLevel()))
						.toList()
		);
	}

	public static List<GroupedEnchantment> applicable(@NotNull Material material) {
		if (material.isAir()) {
			return List.of();
		}

		return APPLICABLE_CACHE.get(material, mat -> {
			final ItemStack probe = new ItemStack(mat);

			return REGISTRY.stream()
					.filter(e -> e.canEnchantItem(probe))
					.filter(e -> {
						final String key = REGISTRY.getKey(e).asString();
						return !key.equals(UNBREAKING_KEY) && !key.equals(MENDING_KEY);
					})
					.map(e -> new GroupedEnchantment(e, e.getMaxLevel()))
					.toList();
		});
	}

	public static @NotNull ItemStack enchant(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
		if (stack.getType().isAir()) {
			return stack;
		}

		if (!enchantment.canEnchantItem(stack)) {
			return stack;
		}

		final ItemStack result = stack.clone();
		final ItemMeta meta = result.getItemMeta();

		if (meta == null) {
			return stack;
		}

		meta.getEnchants().keySet().forEach(existing -> {
			if (existing.conflictsWith(enchantment)) {
				meta.removeEnchant(existing);
			}
		});

		final int currentLevel = meta.getEnchantLevel(enchantment);
		final int nextLevel = currentLevel == 0 ? 1 : Math.min(currentLevel + 1, enchantment.getMaxLevel());

		meta.addEnchant(enchantment, nextLevel, false);
		result.setItemMeta(meta);
		return result;
	}

	public static @NotNull ItemStack remove(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
		if (stack.getType().isAir()) {
			return stack;
		}

		final ItemStack result = stack.clone();
		final ItemMeta meta = result.getItemMeta();

		if (meta == null) {
			return stack;
		}

		if (meta.hasEnchant(enchantment)) {
			meta.removeEnchant(enchantment);
			result.setItemMeta(meta);
		}

		return result;
	}

	public static @NotNull ItemStack generate(@NotNull Enchantment enchantment, int level) {
		final ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		final ItemMeta meta = book.getItemMeta();

		if (!(meta instanceof EnchantmentStorageMeta storage)) {
			return book;
		}

		final int safeLevel = Math.clamp(level, 1, enchantment.getMaxLevel());
		storage.addStoredEnchant(enchantment, safeLevel, false);

		book.setItemMeta(storage);
		return book;
	}
}
