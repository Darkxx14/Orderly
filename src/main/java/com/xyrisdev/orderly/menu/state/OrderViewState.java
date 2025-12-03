package com.xyrisdev.orderly.menu.state;

import com.xyrisdev.library.config.CachableConfiguration;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.menu.state.config.StateConfig;
import com.xyrisdev.orderly.menu.state.model.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class OrderViewState {

	private final Map<UUID, OrderSort> sort = new ConcurrentHashMap<>();
	private final Map<UUID, OrderFilter> filter = new ConcurrentHashMap<>();

	private StateSelector<OrderSort> sortSelector;
	private StateSelector<OrderFilter> filterSelector;

	private OrderSort defaultSort;
	private OrderFilter defaultFilter;

	public void load(@NotNull FileConfiguration menuConfig) {
		sortSelector = new StateSelector<>(StateConfig.sort(menuConfig));
		filterSelector = new StateSelector<>(StateConfig.filter(menuConfig));

		final CachableConfiguration config = Orderly.INSTANCE.config();

		final String rawSort = config.getString("default_sorting_type", "most_paid").toUpperCase();
		final String rawFilter = config.getString("default_filtering_type", "all").toUpperCase();

		try {
			defaultSort = OrderSort.valueOf(rawSort);
		} catch (IllegalArgumentException e) {
			defaultSort = sortSelector.first();
		}

		try {
			defaultFilter = OrderFilter.valueOf(rawFilter);
		} catch (IllegalArgumentException e) {
			defaultFilter = filterSelector.first();
		}

		if (sortSelector.options().stream().noneMatch(o -> o.value() == defaultSort)) {
			defaultSort = sortSelector.first();
		}

		if (filterSelector.options().stream().noneMatch(o -> o.value() == defaultFilter)) {
			defaultFilter = filterSelector.first();
		}
	}

	public OrderSort sort(UUID id) {
		return sort.getOrDefault(id, defaultSort);
	}

	public OrderFilter filter(UUID id) {
		return filter.getOrDefault(id, defaultFilter);
	}

	public void rotate(UUID id, boolean forward, @NotNull StateType type) {
		switch (type) {
			case SORT -> sort.put(id, sortSelector.rotate(sort(id), forward));
			case FILTER -> filter.put(id, filterSelector.rotate(filter(id), forward));
		}
	}

	@Contract(pure = true)
	public @NotNull List<StateOption<OrderSort>> sortOptions() {
		return sortSelector.options();
	}

	@Contract(pure = true)
	public @NotNull List<StateOption<OrderFilter>> filterOptions() {
		return filterSelector.options();
	}
}
