package com.xyrisdev.orderly.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.UUID;

@Getter
@Accessors(chain = true, fluent = true)
@Builder(toBuilder = true)
public final class Order {

	@Builder.Default
	private final UUID id = UUID.randomUUID();

	private final UUID owner;
	private final ItemStack item;
	private final int required;

	@Builder.Default
	private int delivered = 0;

	private final double price;

	@Builder.Default
	private final Instant createdAt = Instant.now();

	public void deliver(int amount) {
		delivered = Math.min(required, delivered + amount);
	}

	public boolean completed() {
		return delivered >= required;
	}

	public int remaining() {
		return Math.max(0, required - delivered);
	}
}
