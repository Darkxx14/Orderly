package com.xyrisdev.orderly.menu.sub;

import com.xyrisdev.library.menu.configurable.ConfigurableMenu;
import com.xyrisdev.orderly.util.input.InputRequest;
import com.xyrisdev.orderly.util.input.compatibility.InputCompatibility;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NewOrderMenu {

	public static @NotNull ConfigurableMenu menu(@NotNull Player viewer) {
		final ConfigurableMenu menu = new ConfigurableMenu("menu/new_order_menu.yml");

		menu.item("cancel")
				.click(event -> {
					YourOrdersMenu.menu(viewer).open(viewer);
				});

		menu.item("select_item")
				.click(event -> {
					// item selector menu
					// def = stone
				});

		menu.item("amount")
				.click(e -> InputRequest.builder()
						.player(viewer)
						.type(InputCompatibility.resolve(viewer))
						.title("Amount")
						.question("Amount")
						.onComplete((p, input) -> {
							if (input.isBlank()) {
								// blank == amount == 1
							} else {
								// implement
							}

							menu.open(viewer);
						})
						.onCancel(p -> {
							menu.open(viewer);
						})
						.build()
						.open());

		menu.item("price")
				.click(e -> InputRequest.builder()
						.player(viewer)
						.type(InputCompatibility.resolve(viewer))
						.title("Price")
						.question("Price Per Item")
						.onComplete((p, input) -> {
							if (input.isBlank()) {
								// blank == $1
							} else {
								// implement
							}

							menu.open(viewer);
						})
						.onCancel(p -> {
							menu.open(viewer);
						})
						.build()
						.open());

		menu.item("confirm")
				.click(event -> {
					// get data (item, amount, price) and create order.
				});

		return menu;
	}
}
