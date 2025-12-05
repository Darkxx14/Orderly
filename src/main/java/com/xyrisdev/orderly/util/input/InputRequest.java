package com.xyrisdev.orderly.util.input;

import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.util.input.model.InputCallback;
import com.xyrisdev.orderly.util.input.model.InputType;
import com.xyrisdev.orderly.util.input.wrapper.AnvilGUIWrapper;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.event.Listener;

import net.wesjd.anvilgui.AnvilGUI;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Builder
@SuppressWarnings("UnstableApiUsage")
public final class InputRequest implements Listener {

	@NotNull
	private final Player player;
	@NotNull
	private final InputType type;
	@NotNull
	private final String question;

	@Builder.Default
	private final String title = "Input";

	@NotNull
	private final InputCallback onComplete;
	@Nullable
	private final Consumer<Player> onCancel;

	public void open() {
		switch (type) {
			case ANVIL -> anvil();
			case SIGN -> sign();
			case DIALOG -> dialog();
		}
	}

	private void anvil() {
		final boolean[] guard = {false};

		AnvilGUIWrapper.of(OrderPlugin.instance())
				.title(title)
				.text(question)
				.close(p -> {
					if (!guard[0] && onCancel != null) onCancel.accept(p);
				})
				.click((slot, state) -> {
					if (slot == 2) {
						guard[0] = true;
						onComplete.onInput(player, state.getText());
						return List.of(AnvilGUI.ResponseAction.close());
					}
					return Collections.emptyList();
				})
				.open(player);
	}

	private void sign() {
		try {
			final boolean[] guard = {false};

			SignGUI gui = SignGUI.builder()
					.setLines("", "↑↑↑↑↑↑↑↑↑↑↑↑", question, "")
					.setType(Material.OAK_SIGN)
					.setColor(DyeColor.BLACK)
					.setHandler((p, result) -> {
						guard[0] = true;
						onComplete.onInput(p, result.getLine(0));
						return Collections.emptyList();
					})
					.callHandlerSynchronously(OrderPlugin.instance())
					.build();

			gui.open(player);

			Orderly.INSTANCE.scheduler().runTaskLater(() -> {
				if (!guard[0] && onCancel != null) {
					onCancel.accept(player);
				}
			}, 4L);

		} catch (SignGUIVersionException e) {
			player.sendRichMessage("<red>SignGUI does not support this server version.");
		}
	}

	private void dialog() {
		final boolean[] guard = {false};

		Dialog dialog = Dialog.create(builder -> builder
				.empty().base(DialogBase.builder(Component.text(title))
						.inputs(List.of(
								DialogInput.text("input", Component.text(question))
										.width(250)
										.initial("")
										.maxLength(128)
										.build()
						))
						.build()
				)
				.type(DialogType.confirmation(
						ActionButton.builder(Component.text("Confirm"))
								.width(120)
								.action(DialogAction.customClick((view, audience) -> {
									guard[0] = true;

									String input = view.getText("input");

									if (input == null || input.isBlank()) {
										input = "";
									}

									if (audience instanceof Player p) {
										onComplete.onInput(p, input);
									}
								}, ClickCallback.Options.builder()
										.uses(1)
										.lifetime(ClickCallback.DEFAULT_LIFETIME)
										.build()))
								.build(),

						ActionButton.builder(Component.text("Cancel"))
								.width(120)
								.action(null)
								.build()
				))
		);

		player.showDialog(dialog);

		Orderly.INSTANCE.scheduler().runTaskLater(() -> {
			if (!guard[0] && onCancel != null) {
				onCancel.accept(player);
			}
		}, 4L);
	}
}