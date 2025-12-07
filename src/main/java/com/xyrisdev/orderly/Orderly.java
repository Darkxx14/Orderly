package com.xyrisdev.orderly;

import com.xyrisdev.library.config.CachableConfiguration;
import com.xyrisdev.library.lib.Library;
import com.xyrisdev.library.scheduler.XScheduler;
import com.xyrisdev.library.scheduler.scheduling.schedulers.TaskScheduler;
import com.xyrisdev.library.state.api.StateAPI;
import com.xyrisdev.orderly.command.OrderCommand;
import com.xyrisdev.orderly.manager.OrderManager;
import com.xyrisdev.orderly.storage.*;
import com.xyrisdev.orderly.storage.factory.StorageFactory;
import com.xyrisdev.orderly.storage.impl.order.MySQLOrderStorage;
import com.xyrisdev.orderly.storage.impl.order.SQLiteOrderStorage;
import com.xyrisdev.orderly.storage.impl.provider.MySQLDatabaseProvider;
import com.xyrisdev.orderly.storage.impl.provider.SQLiteDatabaseProvider;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.configuration.ConfigurationSection;

@Accessors(chain = true, fluent = true)
@Getter
public enum Orderly {
	INSTANCE;

	private CachableConfiguration config;
	private TaskScheduler scheduler;

	private DatabaseProvider provider;
	private OrderManager manager;

	public void start(OrderPlugin plugin) {
		Library.of(plugin, "orderly");
		StateAPI.of(plugin);

		this.config = CachableConfiguration.builder()
				.file("config.yml")
				.build();

		this.scheduler = XScheduler.of(plugin);

		final ConfigurationSection section = config.getSection("database");

		if (section == null) {
			throw new IllegalStateException("Missing database section in config.yml");
		}

		final StorageConfig storageConfig = new StorageConfig(
				DatabaseType.valueOf(section.getString("type", "sqlite").toUpperCase()),
				section.getString("host", "localhost"),
				section.getInt("port", 3306),
				section.getString("database", "orderly"),
				section.getString("username", "root"),
				section.getString("password", "123456"),
				section.getString("sqlite_file", "orderly.db"),
				section.getInt("pool_settings.max_pool_size", 10),
				section.getInt("pool_settings.min_idle", 2),
				section.getLong("pool_settings.connection_timeout", 30000),
				section.getLong("pool_settings.idle_timeout", 600000),
				section.getLong("pool_settings.max_lifetime", 1800000)
		);

		provider = StorageFactory.create(storageConfig);
		provider.connect();

		final OrderStorage orderStorage = switch (storageConfig.type()) {
			case MYSQL -> new MySQLOrderStorage((MySQLDatabaseProvider) provider);
			case SQLITE -> new SQLiteOrderStorage((SQLiteDatabaseProvider) provider);
		};

		manager = new OrderManager(orderStorage);
		manager.loadAll();

		// cmds
		OrderCommand.command(plugin).register();
	}

	public void shutdown() {
		if (manager != null) {
			manager.saveAll();
		}

		if (provider != null) {
			provider.disconnect();
		}

		manager = null;
		provider = null;
		config = null;
		scheduler = null;
	}
}
