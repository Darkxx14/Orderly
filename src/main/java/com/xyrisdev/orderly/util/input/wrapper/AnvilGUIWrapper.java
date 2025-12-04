package com.xyrisdev.orderly.util.input.wrapper;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AnvilGUIWrapper {

	private final AnvilGUI.Builder builder;

	private AnvilGUIWrapper(Plugin plugin) {
		this.builder = new AnvilGUI.Builder().plugin(plugin);
	}

	@Contract("_ -> new")
	public static @NotNull AnvilGUIWrapper of(Plugin plugin) {
		return new AnvilGUIWrapper(plugin);
	}

	public AnvilGUIWrapper title(String title) {
		builder.title(title);
		return this;
	}

	public AnvilGUIWrapper text(String text) {
		builder.text(text);
		return this;
	}

	public AnvilGUIWrapper close(Consumer<Player> onClose) {
		builder.onClose(state -> onClose.accept(state.getPlayer()));
		return this;
	}

	public AnvilGUIWrapper click(@NotNull BiFunction<Integer, AnvilGUI.StateSnapshot, List<AnvilGUI.ResponseAction>> onClick) {
		builder.onClick(onClick);
		return this;
	}

	public AnvilGUIWrapper preventClose() {
		builder.preventClose();
		return this;
	}

	public void open(Player player) {
		builder.open(player);
	}
}