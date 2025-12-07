package com.xyrisdev.orderly.menu.state.item.type;

import org.jetbrains.annotations.NotNull;

public enum ItemSort {
	A_TO_Z,
	Z_TO_A;

	public @NotNull String defaultName() {
		return this == A_TO_Z ? "A → Z" : "Z → A";
	}
}