package com.xyrisdev.orderly.menu;

import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.menu.state.OrderViewState;
import com.xyrisdev.orderly.menu.state.model.OrderFilter;
import com.xyrisdev.orderly.menu.state.model.OrderSort;
import com.xyrisdev.orderly.menu.state.model.StateOption;
import com.xyrisdev.orderly.menu.state.model.StateType;
import com.xyrisdev.orderly.util.input.InputRequest;
import com.xyrisdev.orderly.util.input.model.InputType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public final class OrdersMenu {

	private static final OrderViewState VIEW = new OrderViewState();

	public static @NotNull ConfigurablePagedMenu menu(@NotNull Player viewer) {
		final ConfigurablePagedMenu menu = new ConfigurablePagedMenu("menu/orders_menu.yml")
				.contentSlots(IntStream.range(0, 44).toArray())
				.previousItem("previous_page")
				.nextItem("next_page")
				.noPreviousItem("no_prev")
				.noNextItem("no_next");

		VIEW.load(menu.config().getConfig());

		render(menu, viewer);

		menu.onClose(event -> VIEW.clearSearch(event.getPlayer().getUniqueId()));
		return menu;
	}

	private static void render(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		menu.clear();

		final UUID id = viewer.getUniqueId();
		final OrderSort sort = VIEW.sort(id);
		final OrderFilter filter = VIEW.filter(id);
		final String query = VIEW.search(id);

		final String active = menu.config().getString("items.active", "<#39ff14>");
		final String normal = menu.config().getString("items.normal", "<gray>");
		final String bullet = menu.config().getString("items.bullet", "•");

		Orderly.INSTANCE.manager().all().stream()
				.filter(order -> !order.completed()) // we dont wanna show the orders which r completed
				.filter(order -> {
					if (filter != OrderFilter.ALL) {
						if (order.item().getType().getCreativeCategory() == null) {
							return false;
						}

						if (order.item().getType().getCreativeCategory() != filter.category()) {
							return false;
						}
					}

					// search
					if (!query.isEmpty()) {
						final String item = order.item().getType().name().toLowerCase();
						final String owner = Orderly.INSTANCE.manager().playerCache().name(order.owner()).toLowerCase();
						return item.contains(query) || owner.contains(query);
					}

					return true;
				})
				.sorted(switch (sort) {
					case MOST_PAID -> Comparator.comparingDouble((Order o) -> o.delivered() * o.price()).reversed();
					case MOST_DELIVERED -> Comparator.comparingInt(Order::delivered).reversed();
					case RECENTLY_LISTED -> Comparator.comparing(Order::createdAt).reversed();
					case VALUE_PER_ITEM -> Comparator.comparingDouble(Order::price).reversed();
				})
				.forEach(order -> {
					final String owner = Orderly.INSTANCE.manager().playerCache().name(order.owner());
					final double totalPrice = order.required() * order.price();
					final double paidPrice = order.delivered() * order.price();

					menu.content("order")
							.base(order.item(), BaseMode.STACK)
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
									Map.entry("<item>", order.item().getType().name()),
									Map.entry("<created_at>", order.createdAt().toString())
							))
							.click(event -> {
								// manage menu / deliver menu
							});
				});

		final Map<String, String> sortMap = new ConcurrentHashMap<>();
		final Map<String, String> filterMap = new ConcurrentHashMap<>();

		for (StateOption<OrderSort> option : VIEW.sortOptions()) {
			final OrderSort type = option.value();
			final String placeholder = "<sort_" + type.name().toLowerCase() + ">";
			final String color = type == sort ? active : normal;
			sortMap.put(placeholder, color + bullet + option.displayName());
		}

		for (StateOption<OrderFilter> option : VIEW.filterOptions()) {
			final OrderFilter type = option.value();
			final String placeholder = "<filter_" + type.name().toLowerCase() + ">";
			final String color = type == filter ? active : normal;
			filterMap.put(placeholder, color + bullet + option.displayName());
		}

		menu.item("sort")
				.placeholders(sortMap)
				.click(e -> {
					VIEW.rotate(e.getWhoClicked().getUniqueId(), true, StateType.SORT);
					render(menu, (Player) e.getWhoClicked());
					menu.page(menu.current());
				});

		menu.item("filter")
				.placeholders(filterMap)
				.click(e -> {
					VIEW.rotate(e.getWhoClicked().getUniqueId(), true, StateType.FILTER);
					render(menu, (Player) e.getWhoClicked());
					menu.page(menu.current());
				});

		menu.item("refresh")
				.click(event -> {
					render(menu, viewer);
					menu.page(menu.current());
				});

		menu.item("search")
				.click(e -> InputRequest.builder()
						.player(viewer)
						.type(InputType.SIGN)
						.title("Search")
						.question("Search")
						.onComplete((p, input) -> {
							if (input.isBlank()) {
								VIEW.clearSearch(p.getUniqueId());
							} else {
								VIEW.search(p.getUniqueId(), input);
							}

							render(menu, p);
							menu.page(menu.current());
						})
						.onCancel(p -> {
							render(menu, p);
							menu.page(menu.current());
						})
						.build()
						.open());

		menu.item("your_orders")
				.click(event -> {
					final Player p = (Player) event.getWhoClicked();
					PlayerOrdersMenu.menu(p).open(p);
				});
	}
}
