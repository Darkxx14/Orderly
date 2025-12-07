package com.xyrisdev.orderly.menu;

import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.library.state.api.StateManager;
import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.menu.state.order.OrderStates;
import com.xyrisdev.orderly.menu.state.order.config.OrderStateConfig;
import com.xyrisdev.orderly.menu.state.order.option.wrapper.OrderStateOptions;
import com.xyrisdev.orderly.menu.state.order.type.OrderFilter;
import com.xyrisdev.orderly.menu.state.order.type.OrderSort;
import com.xyrisdev.orderly.menu.sub.YourOrdersMenu;
import com.xyrisdev.orderly.util.TextUtil;
import com.xyrisdev.orderly.util.input.InputRequest;
import com.xyrisdev.orderly.util.input.compatibility.InputCompatibility;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public final class OrdersMenu {

	public static @NotNull ConfigurablePagedMenu menu(@NotNull Player viewer) {
		final ConfigurablePagedMenu menu = new ConfigurablePagedMenu(OrderPlugin.instance(), "menu/orders_menu.yml")
				.contentSlots(IntStream.range(0, 45).toArray())
				.previousItem("previous_page")
				.nextItem("next_page")
				.noPreviousItem("no_prev")
				.noNextItem("no_next");

		OrderStates.config(menu.config().getConfig());
		render(menu, viewer);

		menu.onClose(event -> StateAPI.manager().clear((Player) event.getPlayer(), OrderStates.SEARCH));
		return menu;
	}

	private static void render(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		menu.clear();

		final StateManager state = StateAPI.manager();

		final OrderSort sort = state.get(viewer, OrderStates.SORT);
		final OrderFilter filter = state.get(viewer, OrderStates.FILTER);
		final String query = state.get(viewer, OrderStates.SEARCH);

		final String active = menu.config().getString("items.active", "<#39ff14>");
		final String normal = menu.config().getString("items.normal", "<gray>");
		final String bullet = menu.config().getString("items.bullet", "•");

		Orderly.INSTANCE.manager().all().stream()
				.filter(order -> !order.completed())
				.filter(order -> {
					if (filter != OrderFilter.ALL) {
						if (order.item().getType().getCreativeCategory() == null) {
							return false;
						}

						if (order.item().getType().getCreativeCategory() != filter.category()) {
							return false;
						}
					}

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
									Map.entry("<item>", TextUtil.format(order.item().getType().name())),
									Map.entry("<created_at>", order.createdAt().toString())
							))
							.click(event -> {
								// manage menu / deliver menu
							});
				});

		final OrderStateOptions<OrderSort> sortOptions = OrderStateConfig.sort(menu.config().getConfig());
		final OrderStateOptions<OrderFilter> filterOptions = OrderStateConfig.filter(menu.config().getConfig());

		final Map<String, String> sortMap = new ConcurrentHashMap<>();
		final Map<String, String> filterMap = new ConcurrentHashMap<>();

		sortMap.put("<bullet>", bullet);
		filterMap.put("<bullet>", bullet);

		sortOptions.options().forEach(option ->
				sortMap.put("<sort_" + option.value().name().toLowerCase() + ">", option.displayName())
		);

		filterOptions.options().forEach(option ->
				filterMap.put("<filter_" + option.value().name().toLowerCase() + ">", option.displayName())
		);

		sortMap.put("<color>", sort == OrderSort.valueOf(sort.name()) ? active : normal);
		filterMap.put("<color>", filter == null ? normal : (filter == OrderFilter.valueOf(filter.name()) ? active : normal));


		menu.item("sort")
				.placeholders(sortMap)
				.click(e -> {
					state.rotate(viewer, OrderStates.SORT);
					refresh(menu, viewer);
				});

		menu.item("filter")
				.placeholders(filterMap)
				.click(e -> {
					state.rotate(viewer, OrderStates.FILTER);
					refresh(menu, viewer);
				});

		menu.item("refresh")
				.click(event -> {
					render(menu, viewer);
					menu.page(menu.current());
				});

		menu.item("search")
				.click(e -> InputRequest.builder()
						.player(viewer)
						.type(InputCompatibility.resolve(viewer))
						.title("Search")
						.question("Search")
						.onComplete((p, input) -> {
							if (input.isBlank()) {
								state.clear(p, OrderStates.SEARCH);
							} else {
								state.set(p, OrderStates.SEARCH, input.toLowerCase());
							}

							refresh(menu, viewer);
						})
						.build()
						.open());

		menu.item("your_orders").click(event -> YourOrdersMenu.menu(viewer).open(viewer));
	}

	// smol refresh method :DDDDDDDD
	private static void refresh(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		render(menu, viewer);
		menu.refresh();
	}
}
