package com.xyrisdev.orderly.util.enchantment.model;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public record GroupedEnchantment(@NotNull Enchantment enchantment, int maxLevel) {}