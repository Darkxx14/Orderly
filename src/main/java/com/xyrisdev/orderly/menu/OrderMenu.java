package com.xyrisdev.orderly.menu;

import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.orderly.Orderly;
import java.util.Map;
import java.util.stream.IntStream;

public final class OrderMenu {

	public static ConfigurablePagedMenu order() {
		final ConfigurablePagedMenu menu = new ConfigurablePagedMenu("menu/orders_menu.yml")
				.contentSlots(IntStream.range(0, 44).toArray())
				.previousItem("previous_page")
				.nextItem("next_page")
				.noPreviousItem("no_prev")
				.noNextItem("no_next");

		Orderly.INSTANCE.manager().all().forEach(order -> {
			final String owner = Orderly.INSTANCE.manager().playerCache().name(order.owner());

			menu.content("order")
					.base(order.item(), BaseMode.STACK)
					.placeholders(Map.of(
							"<id>", order.id().toString(),
							"<player>", owner,
							"<required>", String.valueOf(order.required()),
							"<delivered>", String.valueOf(order.delivered()),
							"<remaining>", String.valueOf(order.remaining()),
							"<completed>", String.valueOf(order.completed()),
							"<price>", String.valueOf(order.price()),
							"<item>", order.item().getType().name(),
							"<created_at>", order.createdAt().toString()
					))
					.click(event -> {
						event.getWhoClicked().sendMessage("works");
					});
		});

		return menu;
	}
}
