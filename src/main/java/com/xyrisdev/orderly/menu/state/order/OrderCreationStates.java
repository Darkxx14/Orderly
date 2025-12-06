package com.xyrisdev.orderly.menu.state.order;

import com.xyrisdev.library.state.api.StateScope;
import com.xyrisdev.library.state.key.CustomStateKey;
import com.xyrisdev.library.state.key.DoubleStateKey;
import com.xyrisdev.library.state.key.IntStateKey;
import com.xyrisdev.library.state.key.builder.StateKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OrderCreationStates {

	public static final CustomStateKey<ItemStack> STACK = StateKey.customKey(ItemStack.class)
			.key("orderly:orders_create.stack")
			.defaultValue(new ItemStack(Material.STONE))
			.scope(StateScope.MEMORY)
			.register();


	public static final IntStateKey AMOUNT = StateKey.intKey()
			.key("orderly:orders_create.amount")
			.defaultValue(1)
			.scope(StateScope.MEMORY)
			.register();

	public static final DoubleStateKey PRICE = StateKey.doubleKey()
			.key("orderly:orders_create.price")
			.defaultValue(0)
			.scope(StateScope.MEMORY)
			.register();
}
