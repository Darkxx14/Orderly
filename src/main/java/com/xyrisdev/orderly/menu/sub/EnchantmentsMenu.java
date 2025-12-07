package com.xyrisdev.orderly.menu.sub;

import com.xyrisdev.library.menu.configurable.ConfigurablePagedMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.library.state.api.StateManager;
import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.menu.state.order.OrderCreationStates;
import com.xyrisdev.orderly.menu.sub.session.EnchantSession;
import com.xyrisdev.orderly.util.TextUtil;
import com.xyrisdev.orderly.util.enchantment.EnchantmentUtil;
import com.xyrisdev.orderly.util.enchantment.model.GroupedEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class EnchantmentsMenu {

	private static final List<List<Integer>> GROUP_SLOTS = List.of(
			List.of(2, 3, 4, 5, 6, 7, 8),
			List.of(11, 12, 13, 14, 15, 16, 17),
			List.of(20, 21, 22, 23, 24, 25, 26),
			List.of(29, 30, 31, 32, 33, 34, 35),
			List.of(38, 39, 40, 41, 42, 43, 44)
	);

	public static @NotNull ConfigurablePagedMenu menu(@NotNull Player viewer) {
		final ConfigurablePagedMenu menu = new ConfigurablePagedMenu(OrderPlugin.instance(), "menu/enchantments_menu.yml")
				.contentSlots()
				.previousItem("previous_page")
				.nextItem("next_page")
				.noPreviousItem("no_prev")
				.noNextItem("no_next");

		render(menu, viewer);

//		menu.onClose(event -> {
//			StateAPI.manager().clear(viewer, ItemStates.SEARCH);
//		});

		return menu;
	}


	private static void render(@NotNull ConfigurablePagedMenu menu, @NotNull Player viewer) {
		menu.clear();

		final UUID id = viewer.getUniqueId();
		final ItemStack stack = EnchantSession.stack(id);
		final List<GroupedEnchantment> groups = EnchantmentUtil.applicable(stack);

		final int page = menu.current() - 1;
		final int perPage = GROUP_SLOTS.size();
		final int from = page * perPage;
		final int to = Math.min(from + perPage, groups.size());
		final List<GroupedEnchantment> visible = groups.subList(from, to);

		int rowIndex = 0;

		for (GroupedEnchantment ge : visible) {
			final List<Integer> rowSlots = GROUP_SLOTS.get(rowIndex++);
			final Enchantment ench = ge.enchantment();

			for (int level = 1; level <= ge.maxLevel() && level <= rowSlots.size(); level++) {
				final int slot = rowSlots.get(level - 1);

				final ItemStack book = EnchantmentUtil.generate(ench, level);
				final boolean selected = EnchantSession.level(id, ench) == level;

				int finalLevel = level;
				menu.item("ench_" + slot)
						.base(book, BaseMode.STACK)
						.placeholders(Map.of("<selected>", selected ? "§aSelected" : "§7Click"))
						.click(event -> {
							event.setCancelled(true);

							EnchantSession.apply(id, ench, finalLevel);
							menu.open(viewer);
						});
			}
		}

		menu.item("stack")
				.base(stack, BaseMode.STACK)
				.click(e -> e.setCancelled(true));
	}
}