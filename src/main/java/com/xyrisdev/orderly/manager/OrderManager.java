package com.xyrisdev.orderly.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.storage.OrderStorage;
import com.xyrisdev.orderly.util.cache.OfflinePlayerCache;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class OrderManager {

	private final OrderStorage storage;

	private final Cache<UUID, Order> cache = Caffeine.newBuilder()
			.expireAfterAccess(Duration.ofHours(1))
			.maximumSize(100_000)
			.build();

	private final Set<UUID> dirtyOrders = ConcurrentHashMap.newKeySet();
	private final OfflinePlayerCache playerCache = new OfflinePlayerCache();

	public OrderManager(@NotNull OrderStorage storage) {
		this.storage = storage;
	}

	public void loadAll() {
		storage.loadAll().forEach(order -> cache.put(order.id(), order));
		dirtyOrders.clear();
	}

	public void saveAll() {
		dirtyOrders.forEach(id -> {
			final Order order = cache.getIfPresent(id);
			if (order != null) {
				storage.save(order);
			}
		});
		dirtyOrders.clear();
	}

	public void create(@NotNull Order order) {
		cache.put(order.id(), order);
		dirtyOrders.add(order.id());
	}

	public void update(@NotNull Order order) {
		cache.put(order.id(), order);
		dirtyOrders.add(order.id());
	}

	public Optional<Order> find(@NotNull UUID id) {
		final Order order = cache.get(id, key -> storage.load(key).orElse(null));
		return Optional.ofNullable(order);
	}

	public void delete(@NotNull UUID id) {
		cache.invalidate(id);
		dirtyOrders.remove(id);
		storage.delete(id);
	}

	public Collection<Order> findByOwner(@NotNull UUID owner) {
		return cache.asMap().values().stream()
				.filter(order -> order.owner().equals(owner))
				.toList();
	}

	@Contract(pure = true)
	public @NotNull Collection<Order> all() {
		return cache.asMap().values();
	}

	public OfflinePlayerCache playerCache() {
		return playerCache;
	}
}