package com.xyrisdev.orderly.menu.sub;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.library.state.api.StateManager;
import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.menu.state.item.ItemStates;
import com.xyrisdev.orderly.menu.state.item.config.ItemStateConfig;
import com.xyrisdev.orderly.menu.state.item.option.wrapper.ItemStateOptions;
import com.xyrisdev.orderly.menu.state.item.type.ItemFilter;
import com.xyrisdev.orderly.menu.state.item.type.ItemSort;
import com.xyrisdev.orderly.menu.state.order.OrderCreationStates;
import com.xyrisdev.orderly.util.NonObtainableItemsUtil;
import com.xyrisdev.orderly.util.TextUtil;
import com.xyrisdev.orderly.util.enchantment.EnchantmentUtil;
import com.xyrisdev.orderly.util.input.InputRequest;
import com.xyrisdev.orderly.util.input.compatibility.InputCompatibility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Comparator;
import java.util.stream.IntStream;

public final class ItemSelectMenu {

	// cache base set of valid materials
	private static final List<Material> BASE = List.of(Material.values()).stream()
			.filter(Material::isItem)
			.filter(material -> !NonObtainableItemsUtil.nonObtainable(material))
			.toList();

	// caffeine cache for filtered/sorted/search states
	private static final Cache<String, List<Material>> CACHE = Caffeine.newBuilder()
			.expireAfterAccess(Duration.ofSeconds(30))
			.maximumSize(5000)
			.build();

	public static @NotNull ConfigurablePagedMenu menu(@NotNull Player viewer) {
		final ConfigurablePagedMenu menu = new ConfigurablePagedMenu(OrderPlugin.instance(), "menu/item_selector_menu.yml")
				.contentSlots(IntStream.range(0, 45).toArray())
				.previousItem("previous_page")
				.nextItem("next_page")
				.noPreviousItem("no_prev")
				.noNextItem("no_next");

		render(menu, viewer);

		menu.onClose(event -> StateAPI.manager().clear(viewer, ItemStates.SEARCH));

		return menu;
	}

	private static void render(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		menu.clear();

		final StateManager state = StateAPI.manager();

		final ItemSort sort = state.get(viewer, ItemStates.SORT);
		final ItemFilter filter = state.get(viewer, ItemStates.FILTER);
		final String query = state.get(viewer, ItemStates.SEARCH);

		final String active = menu.config().getConfig().getString("items.active", "<#39ff14>");
		final String normal = menu.config().getConfig().getString("items.normal", "<gray>");
		final String bullet = menu.config().getConfig().getString("items.bullet", "•");

		// cache key
		String key = sort.name() + "|" + filter.name() + "|" + query;

		// retrieve or build filtered/sorted list
		List<Material> filtered = CACHE.get(key, k ->
				BASE.stream()
						.filter(mat -> filter == ItemFilter.ALL ||
								(mat.getCreativeCategory() != null && mat.getCreativeCategory() == filter.category()))
						.filter(mat -> query.isBlank() || mat.name().toLowerCase().contains(query))
						.sorted(sort == ItemSort.A_TO_Z
								? Comparator.comparing(Material::name)
								: Comparator.comparing(Material::name).reversed())
						.toList()
		);

		// paging only visible 45 items
		int page = menu.current();
		int perPage = 45;

		int start = (page - 1) * perPage;
		int end = Math.min(start + perPage, filtered.size());

		filtered.subList(start, end).forEach(material -> {
			menu.content("item")
					.base(new ItemStack(material), BaseMode.STACK)
					.placeholders(Map.of("<material>", TextUtil.format(material.name())))
					.click(e -> {
						if (!EnchantmentUtil.enchantable(material)) {
							state.set(viewer, OrderCreationStates.STACK, new ItemStack(material));
							NewOrderMenu.menu(viewer).open(viewer);
						}
					});
		});

		final ItemStateOptions<ItemSort> sortOptions = ItemStateConfig.sort(menu.config().getConfig());
		final ItemStateOptions<ItemFilter> filterOptions = ItemStateConfig.filter(menu.config().getConfig());

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

		sortMap.put("<color>", active);
		filterMap.put("<color>", active);

		menu.item("sort")
				.placeholders(sortMap)
				.click(e -> {
					state.rotate(viewer, ItemStates.SORT);
					refresh(menu, viewer);
				});

		menu.item("filter")
				.placeholders(filterMap)
				.click(e -> {
					state.rotate(viewer, ItemStates.FILTER);
					refresh(menu, viewer);
				});

		menu.item("search")
				.click(e -> InputRequest.builder()
						.player(viewer)
						.type(InputCompatibility.resolve(viewer))
						.title("Search")
						.question("Search")
						.onComplete((p, input) -> {
							if (input.isBlank()) {
								state.clear(p, ItemStates.SEARCH);
							} else {
								state.set(p, ItemStates.SEARCH, input.toLowerCase());
							}
							refresh(menu, viewer);
						})
						.build()
						.open());
	}

	private static void refresh(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		render(menu, viewer);
		menu.refresh();
	}
}
