package com.xyrisdev.orderly.util.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

public final class OfflinePlayerCache {

	private final Cache<UUID, String> cache = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(10))
			.maximumSize(10_000)
			.build();

	public @NotNull String name(@NotNull UUID uuid) {
		final String result = cache.get(uuid, this::lookup);
		return result == null ? "Unknown" : result;
	}

	private String lookup(UUID uuid) {
		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		return offlinePlayer.getName();
	}
}
