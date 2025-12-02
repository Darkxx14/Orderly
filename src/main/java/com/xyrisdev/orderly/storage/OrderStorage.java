package com.xyrisdev.orderly.storage;

import com.xyrisdev.orderly.manager.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderStorage {

	void save(Order order);
	Optional<Order> load(UUID id);
	List<Order> loadAll();
	void delete(UUID id);
}
