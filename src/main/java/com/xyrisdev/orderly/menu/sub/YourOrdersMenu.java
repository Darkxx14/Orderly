package com.xyrisdev.orderly.menu.sub;

import com.xyrisdev.library.menu.configurable.ConfigurableMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.menu.configurable.mode.SlotType;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.util.TextUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class YourOrdersMenu {

	public static @NotNull ConfigurableMenu menu(@NotNull Player viewer) {
		final ConfigurableMenu menu = new ConfigurableMenu("menu/your_orders_menu.yml");

		Orderly.INSTANCE.manager().findByOwner(viewer.getUniqueId()).forEach(order -> {
			final String owner = Orderly.INSTANCE.manager().playerCache().name(order.owner());
			final double totalPrice = order.required() * order.price();
			final double paidPrice = order.delivered() * order.price();

			menu.item("player_order")
					.base(order.item(), BaseMode.STACK)
					.slot(SlotType.ADAPTIVE)
					.placeholders(Map.ofEntries(
							Map.entry("<id>", order.id().toString()),
							Map.entry("<player>", owner),
							Map.entry("<required>", String.valueOf(order.required())),
							Map.entry("<delivered>", String.valueOf(order.delivered())),
							Map.entry("<remaining>", String.valueOf(order.remaining())),
							Map.entry("<completed>", String.valueOf(order.completed())),
							Map.entry("<price>", String.valueOf(order.price())),
							Map.entry("<paid>", String.valueOf(paidPrice)),
							Map.entry("<total_price>", String.valueOf(totalPrice)),
							Map.entry("<item>", TextUtil.format(order.item().getType().name())),
							Map.entry("<created_at>", order.createdAt().toString())
					))
					.click(event -> {
						// manage menu
					});
		});

		menu.item("new_order")
				.click(event -> {
					NewOrderMenu.menu(viewer).open(viewer);
				});

		return menu;
	}
}
