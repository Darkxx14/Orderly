package com.xyrisdev.orderly.menu.sub;

import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.library.state.api.StateManager;
import com.xyrisdev.orderly.menu.state.item.ItemStates;
import com.xyrisdev.orderly.menu.state.item.config.ItemStateConfig;
import com.xyrisdev.orderly.menu.state.item.option.ItemStateOption;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public final class ItemSelectMenu {

	public static @NotNull ConfigurablePagedMenu menu(@NotNull Player viewer) {
		final ConfigurablePagedMenu menu = new ConfigurablePagedMenu("menu/item_selector_menu.yml")
				.contentSlots(IntStream.range(0, 45).toArray())
				.previousItem("previous_page")
				.nextItem("next_page")
				.noPreviousItem("no_prev")
				.noNextItem("no_next");

		render(menu, viewer);

		menu.onClose(event -> {
			StateAPI.manager().clear(viewer, ItemStates.SEARCH);
		});

		return menu;
	}

	private static void render(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		menu.clear();

		final StateManager state = StateAPI.manager();

		final ItemSort sort = state.get(viewer, ItemStates.SORT);
		final ItemFilter filter = state.get(viewer, ItemStates.FILTER);
		final String query = state.get(viewer, ItemStates.SEARCH);

		final String active = menu.config().getString("items.active", "<#39ff14>");
		final String normal = menu.config().getString("items.normal", "<gray>");
		final String bullet = menu.config().getString("items.bullet", "•");

		final ItemStateOptions<ItemSort> sortOptions = ItemStateConfig.sort(menu.config().getConfig());
		final ItemStateOptions<ItemFilter> filterOptions = ItemStateConfig.filter(menu.config().getConfig());

		Arrays.stream(Material.values())
				.filter(Material::isItem)
				.filter(material -> !NonObtainableItemsUtil.nonObtainable(material))
				.filter(mat -> {
					if (filter != ItemFilter.ALL) {
						if (mat.getCreativeCategory() == null) {
							return false;
						}

						return mat.getCreativeCategory() == filter.category();
					}

					return true;
				})
				.filter(mat -> query.isBlank() || mat.name().toLowerCase().contains(query))
				.sorted(sort == ItemSort.A_TO_Z ? Comparator.comparing(Material::name) : Comparator.comparing(Material::name).reversed())
				.forEach(material -> menu.content("item")
						.base(new ItemStack(material), BaseMode.STACK)
						.placeholders(Map.of("<material>", TextUtil.format(material.name())))
						.click(e -> {
							if (!EnchantmentUtil.enchantable(material)) {
								state.set(viewer, OrderCreationStates.STACK, new ItemStack(material));
								NewOrderMenu.menu(viewer).open(viewer);
								return;
							}

							// e menu
						})
				);

		final Map<String, String> sortMap = new ConcurrentHashMap<>();
		final Map<String, String> filterMap = new ConcurrentHashMap<>();

		sortOptions.options().stream().filter(ItemStateOption::enabled).forEach(option -> {
			final ItemSort type = option.value();
			final String placeholder = "<sort_" + type.name().toLowerCase() + ">";
			final String color = type == sort ? active : normal;

			sortMap.put(placeholder, color + bullet + option.displayName());
		});

		filterOptions.options().stream().filter(ItemStateOption::enabled).forEach(option -> {
			final ItemFilter type = option.value();
			final String placeholder = "<filter_" + type.name().toLowerCase() + ">";
			final String color = type == filter ? active : normal;

			filterMap.put(placeholder, color + bullet + option.displayName());
		});

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
		final int page = menu.current();
		render(menu, viewer);
		menu.page(page);
	}
}
