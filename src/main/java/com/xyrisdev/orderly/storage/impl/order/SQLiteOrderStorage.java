package com.xyrisdev.orderly.storage.impl.order;

import com.xyrisdev.library.logger.XLogger;
import com.xyrisdev.library.serializer.base.Base64Serializer;
import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.storage.OrderStorage;
import com.xyrisdev.orderly.storage.impl.provider.SQLiteDatabaseProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class SQLiteOrderStorage implements OrderStorage {

	private final SQLiteDatabaseProvider provider;

	public SQLiteOrderStorage(SQLiteDatabaseProvider provider) {
		this.provider = provider;
	}

	@Override
	public void save(Order order) {
		try (Connection conn = provider.connection();
		     final PreparedStatement stmt = conn.prepareStatement(
				     "REPLACE INTO orders(id, owner, delivered, required, price, item, collectable_items, created_at) VALUES(?,?,?,?,?,?,?,?)"
		     )) {
			final Base64Serializer serializer = new Base64Serializer();

			stmt.setString(1, order.id().toString());
			stmt.setString(2, order.owner().toString());
			stmt.setInt(3, order.delivered());
			stmt.setInt(4, order.required());
			stmt.setDouble(5, order.price());
			stmt.setBytes(6, order.item().serializeAsBytes());
			stmt.setString(7, serializer.serialize(order.collectableItems()));
			stmt.setLong(8, order.createdAt().toEpochMilli());
			stmt.executeUpdate();
		} catch (Exception e) {
			XLogger.stackTrace(e);
		}
	}

	@Override
	public @NotNull List<Order> loadAll() {
		final List<Order> orders = new ArrayList<>();

		try (Connection conn = provider.connection();
		     final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM orders");
		     final ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				List<ItemStack> collectableItems = new ArrayList<>();
				String itemsData = rs.getString("collectable_items");
				if (itemsData != null && !itemsData.isEmpty()) {
					try {
						collectableItems = new Base64Serializer().deserialize(itemsData);
					} catch (Exception e) {
						XLogger.stackTrace(e);
					}
				}

				orders.add(Order.builder()
						.id(UUID.fromString(rs.getString("id")))
						.owner(UUID.fromString(rs.getString("owner")))
						.delivered(rs.getInt("delivered"))
						.required(rs.getInt("required"))
						.price(rs.getDouble("price"))
						.item(ItemStack.deserializeBytes(rs.getBytes("item")))
						.collectableItems(collectableItems)
						.createdAt(Instant.ofEpochMilli(rs.getLong("created_at")))
						.build());
			}
		} catch (Exception e) {
			XLogger.stackTrace(e);
		}
		return orders;
	}

	@Override
	public Optional<Order> load(UUID id) {
		try (Connection conn = provider.connection();
		     final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM orders WHERE id=?")) {

			stmt.setString(1, id.toString());

			final ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				return Optional.empty();
			}

			List<ItemStack> collectableItems = new ArrayList<>();
			final String itemsData = rs.getString("collectable_items");

			if (itemsData != null && !itemsData.isEmpty()) {
				try {
					collectableItems = new Base64Serializer().deserialize(itemsData);
				} catch (Exception e) {
					XLogger.stackTrace(e);
				}
			}

			return Optional.of(Order.builder()
					.id(id)
					.owner(UUID.fromString(rs.getString("owner")))
					.delivered(rs.getInt("delivered"))
					.required(rs.getInt("required"))
					.price(rs.getDouble("price"))
					.item(org.bukkit.inventory.ItemStack.deserializeBytes(rs.getBytes("item")))
					.collectableItems(collectableItems)
					.createdAt(java.time.Instant.ofEpochMilli(rs.getLong("created_at")))
					.build());
		} catch (Exception e) {
			XLogger.stackTrace(e);
			return Optional.empty();
		}
	}

	@Override
	public void delete(UUID id) {
		try (Connection conn = provider.connection();
		     final PreparedStatement stmt = conn.prepareStatement("DELETE FROM orders WHERE id=?")) {
			stmt.setString(1, id.toString());
			stmt.executeUpdate();
		} catch (Exception e) {
			XLogger.stackTrace(e);
		}
	}
}
