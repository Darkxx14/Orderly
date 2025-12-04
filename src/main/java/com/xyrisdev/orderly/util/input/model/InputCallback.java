package com.xyrisdev.orderly.util.input.model;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface InputCallback {
	void onInput(Player player, String input);
}
