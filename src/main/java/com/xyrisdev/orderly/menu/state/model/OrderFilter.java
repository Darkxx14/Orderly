package com.xyrisdev.orderly.menu.state.model;

import org.bukkit.inventory.CreativeCategory;
import org.jetbrains.annotations.NotNull;

public enum OrderFilter {
	ALL(null),
	BUILDING(CreativeCategory.BUILDING_BLOCKS),
	DECORATIONS(CreativeCategory.DECORATIONS),
	REDSTONE(CreativeCategory.REDSTONE),
	TRANSPORT(CreativeCategory.TRANSPORTATION),
	MISC(CreativeCategory.MISC),
	FOOD(CreativeCategory.FOOD),
	TOOLS(CreativeCategory.TOOLS),
	COMBAT(CreativeCategory.COMBAT),
	BREWING(CreativeCategory.BREWING);

	private final CreativeCategory category;

	OrderFilter(CreativeCategory category) {
		this.category = category;
	}

	public CreativeCategory category() {
		return category;
	}

	public @NotNull String defaultName() {
		return this == ALL ? "All" : category.translationKey().replace("itemGroup.", "");
	}
}
