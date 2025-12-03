package com.xyrisdev.orderly.command;

import com.xyrisdev.library.command.Commands;
import com.xyrisdev.library.command.meta.CommandMeta;
import com.xyrisdev.orderly.menu.OrdersMenu;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class OrderCommand {

	public static @NotNull Commands order(@NotNull JavaPlugin plugin) {
		return Commands.of("order")
				.meta(CommandMeta.builder().namespace("orderly").alias("orders"))
				.executes(context -> {
					if (!context.instanceOfPlayer()) {
						context.sender().sendRichMessage("<red>Only players can use this command!");
						return;
					}

					OrdersMenu.order().open(context.senderAsPlayer());
				})

				.error(ctx -> {
					ctx.action(sender -> {
						sender.sendRichMessage(ctx.type().toString());
					});
				})
				.build();
	}
}
