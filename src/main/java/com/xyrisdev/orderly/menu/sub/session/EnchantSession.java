package com.xyrisdev.orderly.menu.sub.session;

import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class EnchantSession {

	private static final Map<UUID, ItemStack> STACK = new ConcurrentHashMap<>();
	private static final Map<UUID, Map<Enchantment, Integer>> LEVELS = new ConcurrentHashMap<>();

	public static void start(UUID id, @NotNull ItemStack base) {
		STACK.put(id, base.clone());
		LEVELS.put(id, new HashMap<>());
	}

	public static ItemStack stack(UUID id) {
		return STACK.get(id);
	}

	public static int level(UUID id, Enchantment enchantment) {
		return LEVELS.getOrDefault(id, Map.of()).getOrDefault(enchantment, 0);
	}

	public static boolean selected(UUID id, Enchantment enchantment) {
		return level(id, enchantment) > 0;
	}

	public static void apply(UUID id, Enchantment enchantment, int level) {
		final ItemStack base = STACK.get(id);

		base.addUnsafeEnchantment(enchantment, level);
		STACK.put(id, base);

		LEVELS.computeIfAbsent(id, x -> new HashMap<>()).put(enchantment, level);
	}

	public static void clear(UUID id) {
		STACK.remove(id);
		LEVELS.remove(id);
	}
}
