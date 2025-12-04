package com.xyrisdev.orderly.util.input.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InputCallback {
	void onInput(@NotNull Player player, @NotNull String input);
}
