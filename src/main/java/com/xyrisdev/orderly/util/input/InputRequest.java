package com.xyrisdev.orderly.util.input;

import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.util.input.model.InputCallback;
import com.xyrisdev.orderly.util.input.model.InputType;
import com.xyrisdev.orderly.util.input.wrapper.AnvilGUIWrapper;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Builder()
public final class InputRequest {

	@NonNull
	private final Player player;
	@NonNull
	private final InputType type;
	@NonNull
	private final String question;

	@Builder.Default
	private final String title = "Input";
	@NonNull
	private final InputCallback onComplete;
	private final Consumer<Player> onCancel;

	public void open() {
		switch (type) {
			case ANVIL -> anvil();
			case SIGN -> sign();
		}
	}

	private void anvil() {
		final boolean[] completed = {false};

		AnvilGUIWrapper.of(OrderPlugin.instance())
				.title(title)
				.text(question)
				.close(p -> {
					if (!completed[0] && onCancel != null) {
						onCancel.accept(p);
					}
				})
				.click((slot, state) -> {
					if (slot == 2) {
						completed[0] = true;
						onComplete.onInput(player, state.getText());
						return List.of(AnvilGUI.ResponseAction.close());
					}
					return Collections.emptyList();
				})
				.open(player);
	}

	private void sign() {
		try {
			SignGUI gui;

			final boolean[] completed = {false};

			gui = SignGUI.builder()
					.setLines("", "↑↑↑↑↑↑↑↑↑↑↑↑", question, "")
					.setHandler((p, result) -> {
						completed[0] = true;
						onComplete.onInput(p, result.getLine(0));
						return Collections.emptyList();
					})
					.callHandlerSynchronously(OrderPlugin.instance())
					.build();

			gui.open(player);

			// hacky workaround :DDD
			Orderly.INSTANCE.scheduler().runTaskLater(() -> {
				if (!completed[0] && onCancel != null) {
					onCancel.accept(player);
				}
			}, 4L);

		} catch (SignGUIVersionException e) {
			player.sendRichMessage("<red>SignGUI does not support this server version.");
		}
	}
}