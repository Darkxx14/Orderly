package com.xyrisdev.orderly.manager;

import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.storage.OrderStorage;

import java.util.Optional;
import java.util.UUID;

public final class OrderManager {

	private final OrderStorage storage;

	public OrderManager(OrderStorage storage) {
		this.storage = storage;
	}

	public void create(Order order) {
		storage.save(order);
	}

	public Optional<Order> find(UUID id) {
		return storage.load(id);
	}

	public void remove(UUID id) {
		storage.delete(id);
	}
}
