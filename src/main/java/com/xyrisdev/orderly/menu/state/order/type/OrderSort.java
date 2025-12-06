package com.xyrisdev.orderly.menu.state.order.type;

import org.jetbrains.annotations.NotNull;

public enum OrderSort {
	MOST_PAID,
	MOST_DELIVERED,
	RECENTLY_LISTED,
	VALUE_PER_ITEM;

	public @NotNull String defaultName() {
		return switch (this) {
			case MOST_PAID -> "Most Paid";
			case MOST_DELIVERED -> "Most Delivered";
			case RECENTLY_LISTED -> "Recently Listed";
			case VALUE_PER_ITEM -> "Most Money Per Item";
		};
	}
}
