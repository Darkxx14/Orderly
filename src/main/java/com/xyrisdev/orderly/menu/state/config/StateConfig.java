package com.xyrisdev.orderly.menu.state.config;

import com.xyrisdev.orderly.menu.state.model.OrderFilter;
import com.xyrisdev.orderly.menu.state.model.OrderSort;
import com.xyrisdev.orderly.menu.state.model.StateOption;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StateConfig {

	public static @NotNull List<StateOption<OrderSort>> sort(@NotNull FileConfiguration config) {
		final ConfigurationSection section = config.getConfigurationSection("items.sort.options");
		final List<StateOption<OrderSort>> list = new ArrayList<>();

		Arrays.stream(OrderSort.values()).forEach(type -> {
			final String key = type.name().toLowerCase();

			final boolean enabled = section == null || section.getBoolean(key + ".enabled", true);
			final String name = section == null ? type.defaultName() : section.getString(key + ".name", type.defaultName());

			list.add(new StateOption<>(type, name, enabled));
		});

		return list;
	}

	public static @NotNull List<StateOption<OrderFilter>> filter(@NotNull FileConfiguration config) {
		final ConfigurationSection section = config.getConfigurationSection("items.filter.options");
		final List<StateOption<OrderFilter>> list = new ArrayList<>();

		Arrays.stream(OrderFilter.values()).forEach(type -> {
			final String key = type.name().toLowerCase();

			final boolean enabled = section == null || section.getBoolean(key + ".enabled", true);
			final String name = section == null ? type.defaultName() : section.getString(key + ".name", type.defaultName());

			list.add(new StateOption<>(type, name, enabled));
		});

		return list;
	}
}
