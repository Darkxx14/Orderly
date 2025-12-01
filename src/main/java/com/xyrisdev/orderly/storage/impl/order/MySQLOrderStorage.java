package com.xyrisdev.orderly.storage.impl.order;

import com.xyrisdev.library.logger.XLogger;
import com.xyrisdev.orderly.manager.model.Order;
import com.xyrisdev.orderly.storage.OrderStorage;
import com.xyrisdev.orderly.storage.impl.provider.MySQLDatabaseProvider;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public final class MySQLOrderStorage implements OrderStorage {

	private final MySQLDatabaseProvider provider;

	public MySQLOrderStorage(MySQLDatabaseProvider provider) {
		this.provider = provider;
	}

	@Override
	public void save(Order order) {
		try (Connection conn = provider.connection();
		     final PreparedStatement stmt = conn.prepareStatement(
				     "REPLACE INTO orders(id, owner, delivered, required, price, item, created_at) VALUES(?,?,?,?,?,?,?)"
		     )) {

			stmt.setString(1, order.id().toString());
			stmt.setString(2, order.owner().toString());
			stmt.setInt(3, order.delivered());
			stmt.setInt(4, order.required());
			stmt.setDouble(5, order.price());
			stmt.setBytes(6, order.item().serializeAsBytes());
			stmt.setLong(7, order.createdAt().toEpochMilli());
			stmt.executeUpdate();
		} catch (Exception e) {
			XLogger.stackTrace(e);
		}
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

			return Optional.of(Order.builder()
					.id(id)
					.owner(UUID.fromString(rs.getString("owner")))
					.delivered(rs.getInt("delivered"))
					.required(rs.getInt("required"))
					.price(rs.getDouble("price"))
					.item(ItemStack.deserializeBytes(rs.getBytes("item")))
					.createdAt(Instant.ofEpochMilli(rs.getLong("created_at")))
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
