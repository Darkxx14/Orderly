package com.xyrisdev.orderly.command;

import com.xyrisdev.library.command.Commands;
import com.xyrisdev.library.command.meta.CommandMeta;
import com.xyrisdev.orderly.OrderPlugin;
import com.xyrisdev.orderly.menu.OrdersMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class OrderCommand {

	public static @NotNull Commands command(@NotNull OrderPlugin plugin) {
		return Commands.of("order")
				.meta(CommandMeta.builder().namespace("orderly").alias("orders"))
				.executes(context -> {
					if (!context.instanceOfPlayer()) {
						context.sender().sendRichMessage("<red>Only players can use this command!");
						return;
					}

					final Player player = context.senderAsPlayer();

					OrdersMenu.menu(player).open(player);
				})

				.error(ctx -> {
					ctx.action(sender -> {
						sender.sendRichMessage(ctx.type().toString());
					});
				})
				.build();
	}
}
