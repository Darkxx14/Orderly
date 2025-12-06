package com.xyrisdev.orderly.menu.state.order.config;

import com.xyrisdev.orderly.menu.state.order.option.OrderStateOption;
import com.xyrisdev.orderly.menu.state.order.option.wrapper.OrderStateOptions;
import com.xyrisdev.orderly.menu.state.order.type.OrderFilter;
import com.xyrisdev.orderly.menu.state.order.type.OrderSort;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OrderStateConfig {

	@Contract("_ -> new")
	public static @NotNull OrderStateOptions<OrderSort> sort(@NotNull FileConfiguration config) {
		final ConfigurationSection section = config.getConfigurationSection("items.sort.options");
		final List<OrderStateOption<OrderSort>> options = new ArrayList<>();

		Arrays.stream(OrderSort.values()).forEach(type -> {
			final String key = type.name().toLowerCase();
			final boolean enabled = section == null || section.getBoolean(key + ".enabled", true);
			final String name = section == null ? type.defaultName() : section.getString(key + ".name", type.defaultName());

			options.add(new OrderStateOption<>(type, name, enabled));
		});

		final OrderSort def = resolveDefault(
				config.getString("default_sorting_type"),
				OrderSort.class,
				OrderSort.MOST_PAID,
				options
		);

		return new OrderStateOptions<>(def, options);
	}

	@Contract("_ -> new")
	public static @NotNull OrderStateOptions<OrderFilter> filter(@NotNull FileConfiguration config) {
		final ConfigurationSection section = config.getConfigurationSection("items.filter.options");
		final List<OrderStateOption<OrderFilter>> options = new ArrayList<>();

		Arrays.stream(OrderFilter.values()).forEach(type -> {
			final String key = type.name().toLowerCase();
			final boolean enabled = section == null || section.getBoolean(key + ".enabled", true);
			final String name = section == null ? type.defaultName() : section.getString(key + ".name", type.defaultName());

			options.add(new OrderStateOption<>(type, name, enabled));
		});

		final OrderFilter def = resolveDefault(
				config.getString("default_filtering_type"),
				OrderFilter.class,
				OrderFilter.ALL,
				options
		);

		return new OrderStateOptions<>(def, options);
	}

	private static <T extends Enum<T>> T resolveDefault(String raw, @NotNull Class<T> type, @NotNull T fallback, @NotNull List<OrderStateOption<T>> options) {
		if (raw == null) {
			return fallback;
		}

		try {
			final T parsed = Enum.valueOf(type, raw.toUpperCase());
			return options.stream().anyMatch(o -> o.enabled() && o.value() == parsed) ? parsed : fallback;
		} catch (Exception ignored) {
			return fallback;
		}
	}
}