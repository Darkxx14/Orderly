package com.xyrisdev.orderly.menu.state.item.config;

import com.xyrisdev.orderly.menu.state.item.option.ItemStateOption;
import com.xyrisdev.orderly.menu.state.item.option.wrapper.ItemStateOptions;
import com.xyrisdev.orderly.menu.state.item.type.ItemFilter;
import com.xyrisdev.orderly.menu.state.item.type.ItemSort;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ItemStateConfig {

	@Contract("_ -> new")
	public static @NotNull ItemStateOptions<ItemSort> sort(@NotNull FileConfiguration config) {
		final ConfigurationSection section = config.getConfigurationSection("items.sort.options");
		final List<ItemStateOption<ItemSort>> options = new ArrayList<>();

		Arrays.stream(ItemSort.values()).forEach(type -> {
			final String key = type.name().toLowerCase();
			final boolean enabled = section == null || section.getBoolean(key + ".enabled", true);
			final String name = section == null ? type.defaultName() : section.getString(key + ".name", type.defaultName());
			options.add(new ItemStateOption<>(type, name, enabled));
		});

		final ItemSort def = resolveDefault(
				config.getString("default_sorting_type"),
				ItemSort.class,
				ItemSort.A_TO_Z,
				options
		);

		return new ItemStateOptions<>(def, options);
	}

	@Contract("_ -> new")
	public static @NotNull ItemStateOptions<ItemFilter> filter(@NotNull FileConfiguration config) {
		final ConfigurationSection section = config.getConfigurationSection("items.filter.options");
		final List<ItemStateOption<ItemFilter>> options = new ArrayList<>();

		Arrays.stream(ItemFilter.values()).forEach(type -> {
			final String key = type.name().toLowerCase();
			final boolean enabled = section == null || section.getBoolean(key + ".enabled", true);
			final String name = section == null ? type.defaultName() : section.getString(key + ".name", type.defaultName());
			options.add(new ItemStateOption<>(type, name, enabled));
		});

		final ItemFilter def = resolveDefault(
				config.getString("default_filtering_type"),
				ItemFilter.class,
				ItemFilter.ALL,
				options
		);

		return new ItemStateOptions<>(def, options);
	}

	private static <T extends Enum<T>> T resolveDefault(String raw, @NotNull Class<T> type, @NotNull T fallback, @NotNull List<ItemStateOption<T>> options) {
		if (raw == null) return fallback;

		try {
			final T parsed = Enum.valueOf(type, raw.toUpperCase());
			return options.stream().anyMatch(o -> o.enabled() && o.value() == parsed) ? parsed : fallback;
		} catch (Exception ignored) {
			return fallback;
		}
	}
}
