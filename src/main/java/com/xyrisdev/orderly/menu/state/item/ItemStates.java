package com.xyrisdev.orderly.menu.state.item;

import com.xyrisdev.library.state.api.StateScope;
import com.xyrisdev.library.state.key.EnumStateKey;
import com.xyrisdev.library.state.key.StringStateKey;
import com.xyrisdev.library.state.key.builder.StateKey;
import com.xyrisdev.orderly.menu.state.order.type.OrderFilter;
import com.xyrisdev.orderly.menu.state.order.type.OrderSort;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ItemStates {

	public static final EnumStateKey<OrderSort> SORT = StateKey.enumKey(OrderSort.class)
			.key("orderly:itemselector.sort")
			.defaultValue(OrderSort.MOST_PAID)
			.scope(StateScope.METADATA)
			.register();

	public static final EnumStateKey<OrderFilter> FILTER = StateKey.enumKey(OrderFilter.class)
			.key("orderly:itemselector.filter")
			.defaultValue(OrderFilter.ALL)
			.scope(StateScope.METADATA)
			.register();

	public static final StringStateKey SEARCH = StateKey.stringKey()
			.key("orderly:itemselector.search")
			.defaultValue("")
			.scope(StateScope.MEMORY)
			.register();
}
