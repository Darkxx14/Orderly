package com.xyrisdev.orderly.menu.sub;

import com.xyrisdev.library.menu.configurable.ConfigurableMenu;
import com.xyrisdev.library.menu.configurable.mode.BaseMode;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.library.state.api.StateManager;
import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.menu.state.order.OrderCreationStates;
import com.xyrisdev.orderly.util.FormatUtil;
import com.xyrisdev.orderly.util.NumberUtil;
import com.xyrisdev.orderly.util.TextUtil;
import com.xyrisdev.orderly.util.input.InputRequest;
import com.xyrisdev.orderly.util.input.compatibility.InputCompatibility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class NewOrderMenu {

	public static @NotNull ConfigurableMenu menu(@NotNull Player viewer) {
		final ConfigurableMenu menu = new ConfigurableMenu(OrderPlugin.instance(), "menu/new_order_menu.yml");

		final StateManager state = StateAPI.manager();

		final ItemStack stack = state.get(viewer, OrderCreationStates.STACK);
		final int amount = state.get(viewer, OrderCreationStates.AMOUNT);
		final double price = state.get(viewer, OrderCreationStates.PRICE);
		final double totalPrice = amount * price;

		menu.onClose(event -> {
			final Player player = (Player) event.getPlayer();
			state.clear(player, OrderCreationStates.STACK);
		});

		menu.item("cancel").click(event -> YourOrdersMenu.menu(viewer).open(viewer));

		menu.item("select_item")
				.base(state.get(viewer, OrderCreationStates.STACK), BaseMode.STACK)
				.placeholders(Map.of("<item_name>", TextUtil.format(stack.getType().name())))
				.click(event -> {
					ItemSelectMenu.menu(viewer).open(viewer);
				});

		menu.item("amount")
				.placeholders(Map.of("<amount>", String.valueOf(amount)))
				.click(event -> InputRequest.builder()
						.player(viewer)
						.type(InputCompatibility.resolve(viewer))
						.title("Amount")
						.question("Amount")
						.onComplete((p, input) -> {
							state.set(p, OrderCreationStates.AMOUNT, input.isBlank() ? 1 : NumberUtil.toIntOrDefault(input, 1));
							menu(viewer).open(viewer);
						})
						.onCancel(p -> menu(viewer).open(viewer))
						.build()
						.open());

		menu.item("price")
				.placeholders(Map.of("<price>", FormatUtil.format(price)))
				.click(event -> InputRequest.builder()
						.player(viewer)
						.type(InputCompatibility.resolve(viewer))
						.title("Price")
						.question("Price Per Item")
						.onComplete((p, input) -> {
							state.set(p, OrderCreationStates.PRICE, input.isBlank() ? 0.0 : NumberUtil.toDoubleOrDefault(input, 0.0));
							menu(viewer).open(viewer);
						})
						.onCancel(p -> menu(viewer).open(viewer))
						.build()
						.open());

		menu.item("confirm")
				.placeholders(Map.of(
						"<amount>", String.valueOf(amount),
						"<price>", FormatUtil.format(price),
						"<total_price>", FormatUtil.format(totalPrice)
				))
				.click(event -> {
					if (price == 0) {
						return;
					}

					final Order order = Order.builder()
							.owner(viewer.getUniqueId())
							.item(stack)
							.required(amount)
							.price(price)
							.build();

					Orderly.INSTANCE.manager().create(order);

					state.clear(viewer, OrderCreationStates.STACK);
					state.clear(viewer, OrderCreationStates.AMOUNT);
					state.clear(viewer, OrderCreationStates.PRICE);

					viewer.sendRichMessage("<green>Order created successfully!");
				});

		return menu;
	}
}
