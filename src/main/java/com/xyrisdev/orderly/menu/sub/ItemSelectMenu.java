package com.xyrisdev.orderly.menu.sub;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.scheduler.scheduling.tasks.MyScheduledTask;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.library.state.api.StateManager;
import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.menu.state.item.ItemStates;
import com.xyrisdev.orderly.menu.state.item.config.ItemStateConfig;
import com.xyrisdev.orderly.menu.state.item.option.wrapper.ItemStateOptions;
import com.xyrisdev.orderly.menu.state.item.type.ItemFilter;
import com.xyrisdev.orderly.menu.state.item.type.ItemSort;
import com.xyrisdev.orderly.menu.state.item.option.ItemStateOption;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public final class ItemSelectMenu {

	private static final Cache<String, List<Material>> CACHE = Caffeine.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.maximumSize(500)
			.build();

	public static void cache() {
		final List<Material> raw = Arrays.stream(Material.values())
				.filter(Material::isItem)
				.filter(mat -> !NonObtainableItemsUtil.nonObtainable(mat))
				.toList();

		final List<Material> collected = new ArrayList<>(raw.size());
		final int batchSize = 80;

		final MyScheduledTask[] holder = new MyScheduledTask[1];

		holder[0] = Orderly.INSTANCE.scheduler().runTaskTimerAsynchronously(new Runnable() {
			int index = 0;

			@Override
			public void run() {
				if (index >= raw.size()) {
					collected.sort(Comparator.comparing(Material::name));

					final String key = ItemSort.A_TO_Z + "|" + ItemFilter.ALL + "|";
					ItemSelectMenu.CACHE.put(key, List.copyOf(collected));

					OrderPlugin.instance().getLogger().info("[Orderly] Cached " + collected.size() + " items");
					holder[0].cancel();
					return;
				}

				int end = Math.min(index + batchSize, raw.size());
				collected.addAll(raw.subList(index, end));
				index = end;
			}
		}, 0L, 1L);
	}

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

		final String key = sort + "|" + filter + "|" + query;

		final List<Material> allMaterials = Objects.requireNonNullElseGet(
				CACHE.get(key, k ->
						Arrays.stream(Material.values())
								.filter(Material::isItem)
								.filter(mat -> !NonObtainableItemsUtil.nonObtainable(mat))
								.filter(mat -> filter == ItemFilter.ALL ||  mat.getCreativeCategory() == filter.category())
								.filter(mat -> query == null || query.isBlank() || mat.name().toLowerCase().contains(query))
								.sorted(sort == ItemSort.A_TO_Z ? Comparator.comparing(Material::name) : Comparator.comparing(Material::name).reversed())
								.toList()
				),
				List::of
		);

		final int page = menu.current() - 1;
		final int itemsPerPage = 45;
		final int from = page * itemsPerPage;
		final int to = Math.min(from + itemsPerPage, allMaterials.size());

		if (from < allMaterials.size()) {
			final List<Material> pageMaterials = allMaterials.subList(from, to);

			pageMaterials.forEach(material ->
					menu.content("item")
							.base(new ItemStack(material), BaseMode.STACK)
							.placeholders(Map.of("<material>", TextUtil.format(material.name())))
					.click(e -> {
						if (!EnchantmentUtil.enchantable(material)) {
							state.set(viewer, OrderCreationStates.STACK, new ItemStack(material));
							NewOrderMenu.menu(viewer).open(viewer);
							return;
						}

						// enchant menu
					})
			);
		}

		final String active = menu.config().getString("items.active", "<#39ff14>");
		final String normal = menu.config().getString("items.normal", "<gray>");
		final String bullet = menu.config().getString("items.bullet", "•");

		final ItemStateOptions<ItemSort> sortOptions = ItemStateConfig.sort(menu.config().getConfig());
		final ItemStateOptions<ItemFilter> filterOptions = ItemStateConfig.filter(menu.config().getConfig());

		final Map<String, String> sortMap = new ConcurrentHashMap<>();
		final Map<String, String> filterMap = new ConcurrentHashMap<>();

		sortOptions.options().stream().filter(ItemStateOption::enabled).forEach(option -> {
			final ItemSort type = option.value();
			final String placeholder = "<sort_" + type.name().toLowerCase() + ">";
			final String colorPlaceholder = "<color_sort_" + type.name().toLowerCase() + ">";

			sortMap.put(placeholder, option.displayName());
			sortMap.put(colorPlaceholder, type == sort ? active : normal);
		});

		filterOptions.options().stream().filter(ItemStateOption::enabled).forEach(option -> {
			final ItemFilter type = option.value();
			final String placeholder = "<filter_" + type.name().toLowerCase() + ">";
			final String colorPlaceholder = "<color_filter_" + type.name().toLowerCase() + ">";

			filterMap.put(placeholder, option.displayName());
			filterMap.put(colorPlaceholder, type == filter ? active : normal);
		});

		sortMap.put("<bullet>", bullet);
		filterMap.put("<bullet>", bullet);

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

							menu(viewer).open(p);
						})
						.build()
						.open());
	}

	private static void refresh(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		render(menu, viewer);
		menu.refresh();
	}
}
