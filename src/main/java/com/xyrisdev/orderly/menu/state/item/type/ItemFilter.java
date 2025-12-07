package com.xyrisdev.orderly.menu.state.item.type;

import org.bukkit.inventory.CreativeCategory;
import org.jetbrains.annotations.NotNull;

public enum ItemFilter {
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

	ItemFilter(CreativeCategory category) {
		this.category = category;
	}

	public CreativeCategory category() {
		return category;
	}

	public @NotNull String defaultName() {
		return this == ALL ? "All" :
				category.translationKey().replace("itemGroup.", "");
	}
}
