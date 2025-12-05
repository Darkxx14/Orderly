package com.xyrisdev.orderly.util.input.wrapper;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

// idk why i have this, TODO: merge this thing into the InputRequest itself
public final class AnvilGUIWrapper {

	private final AnvilGUI.Builder builder;

	private AnvilGUIWrapper(@NotNull Plugin plugin) {
		this.builder = new AnvilGUI.Builder().plugin(plugin);
	}

	@Contract("_ -> new")
	public static @NotNull AnvilGUIWrapper of(@NotNull Plugin plugin) {
		return new AnvilGUIWrapper(plugin);
	}

	public AnvilGUIWrapper title(@NotNull String title) {
		builder.title(title);
		return this;
	}

	public AnvilGUIWrapper text(@NotNull String text) {
		builder.text(text);
		return this;
	}

	public AnvilGUIWrapper close(@NotNull Consumer<Player> onClose) {
		builder.onClose(state -> onClose.accept(state.getPlayer()));
		return this;
	}

	public AnvilGUIWrapper click(@NotNull BiFunction<Integer, AnvilGUI.StateSnapshot, @NotNull List<AnvilGUI.ResponseAction>> onClick) {
		builder.onClick(onClick);
		return this;
	}

	public AnvilGUIWrapper preventClose() {
		builder.preventClose();
		return this;
	}

	public void open(@NotNull Player player) {
		builder.open(player);
	}
}