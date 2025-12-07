package com.xyrisdev.orderly.menu.state.item;

import com.xyrisdev.library.state.api.StateScope;
import com.xyrisdev.library.state.key.EnumStateKey;
import com.xyrisdev.library.state.key.StringStateKey;
import com.xyrisdev.library.state.key.builder.StateKey;
import com.xyrisdev.orderly.menu.state.item.type.ItemFilter;
import com.xyrisdev.orderly.menu.state.item.type.ItemSort;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ItemStates {

	public static final EnumStateKey<ItemSort> SORT = StateKey.enumKey(ItemSort.class)
			.key("orderly:itemselector.sort")
			.defaultValue(ItemSort.A_TO_Z)
			.scope(StateScope.METADATA)
			.register();

	public static final EnumStateKey<ItemFilter> FILTER = StateKey.enumKey(ItemFilter.class)
			.key("orderly:itemselector.filter")
			.defaultValue(ItemFilter.ALL)
			.scope(StateScope.METADATA)
			.register();

	public static final StringStateKey SEARCH = StateKey.stringKey()
			.key("orderly:itemselector.search")
			.defaultValue("")
			.scope(StateScope.MEMORY)
			.register();
}
