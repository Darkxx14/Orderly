package com.xyrisdev.orderly.menu.state.order;

import com.xyrisdev.library.state.api.StateScope;
import com.xyrisdev.library.state.key.EnumStateKey;
import com.xyrisdev.library.state.key.StringStateKey;
import com.xyrisdev.library.state.key.builder.StateKey;
import com.xyrisdev.orderly.menu.state.order.config.OrderStateConfig;
import com.xyrisdev.orderly.menu.state.order.option.wrapper.OrderStateOptions;
import com.xyrisdev.orderly.menu.state.order.type.OrderFilter;
import com.xyrisdev.orderly.menu.state.order.type.OrderSort;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class OrderStates {

	private static boolean loaded = false;

	public static EnumStateKey<OrderSort> SORT;
	public static EnumStateKey<OrderFilter> FILTER;
	public static StringStateKey SEARCH;

	public static void config(@NotNull FileConfiguration cfg) {
		if (loaded) {
			return;
		}

		loaded = true;

		final OrderStateOptions<OrderSort> sort = OrderStateConfig.sort(cfg);
		final OrderStateOptions<OrderFilter> filter = OrderStateConfig.filter(cfg);

		SORT = StateKey.enumKey(OrderSort.class)
				.key("orderly:orders.sort")
				.defaultValue(sort.defaultValue())
				.scope(StateScope.METADATA)
				.register();

		FILTER = StateKey.enumKey(OrderFilter.class)
				.key("orderly:orders.filter")
				.defaultValue(filter.defaultValue())
				.scope(StateScope.METADATA)
				.register();

		SEARCH = StateKey.stringKey()
				.key("orderly:orders.search")
				.defaultValue("")
				.scope(StateScope.MEMORY)
				.register();
	}
}
