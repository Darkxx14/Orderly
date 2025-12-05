package com.xyrisdev.orderly.util.input.compatibility;

import com.xyrisdev.library.logger.XLogger;
import com.xyrisdev.orderly.Orderly;
import com.xyrisdev.orderly.util.input.model.InputType;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class InputCompatibility {

	private static final int MIN_DIALOG_PROTOCOL = 771; // 1.21.6+
	private static boolean detected = false;
	private static boolean supportsDialogs = false;

	private InputType configuredType = InputType.DIALOG;
	private final List<InputType> fallback = new ArrayList<>();

	public static void detect() {
		detected = true;

		try {
			Class.forName("io.papermc.paper.dialog.Dialog");
			supportsDialogs = true;
		} catch (Throwable ignore) {
			supportsDialogs = false;
		}

		final ConfigurationSection section = Orderly.INSTANCE.config().getSection("text_input");

		if (section == null) {
			return;
		}

		configuredType = InputType.valueOf(section.getString("type", "adaptive").toUpperCase());
		fallback.clear();

		section.getStringList("fallback").stream()
				.map(s -> InputType.valueOf(s.toUpperCase()))
				.forEach(fallback::add);
	}

	public static boolean clientDialogs(@NotNull Player player) {
		return player.getProtocolVersion() >= MIN_DIALOG_PROTOCOL;
	}

	public static boolean dialogs(@NotNull Player player) {
		ensure();
		return supportsDialogs && clientDialogs(player);
	}

	public static @NotNull InputType resolve(@NotNull Player player) {
		ensure();

		if (configuredType == InputType.DIALOG) {
			if (dialogs(player)) {
				return InputType.DIALOG;
			}

			XLogger.custom().warn(
					"[Orderly] Dialog input forced in config but unsupported (server=" + supportsDialogs +
							", client=" + clientDialogs(player) + "). Falling back..."
			);

			return fallback.stream().findFirst().orElse(InputType.SIGN);
		}

		if (configuredType != InputType.ADAPTIVE) {
			return configuredType;
		}

		if (dialogs(player)) {
			return InputType.DIALOG;
		}

		return fallback.stream().findFirst().orElse(InputType.SIGN);
	}

	private static void ensure() {
		if (!detected) {
			detect();
		}
	}
}
