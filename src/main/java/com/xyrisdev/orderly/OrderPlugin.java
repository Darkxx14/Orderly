package com.xyrisdev.orderly;

import com.xyrisdev.library.AbstractPlugin;
import com.xyrisdev.library.lib.feature.FeatureFlags;
import com.xyrisdev.library.lib.feature.FeatureRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(chain = true, fluent = true)
public final class OrderPlugin extends AbstractPlugin {

	@Getter
	private static OrderPlugin instance;

	@Override
	protected void load() {
		instance = this;
	}

	@Override
	protected void run() {
		Orderly.INSTANCE.start(this);
	}

	@Override
	protected void shutdown() {
		Orderly.INSTANCE.shutdown();
		instance = null;
	}

	@Override
	protected void feature(@NotNull FeatureRegistry registry) {
		registry.registrar()
				.disable(FeatureFlags.Conversation.PROCESS)
				.disable(FeatureFlags.CallBack.PROCESS)
				.disable(FeatureFlags.WorldGuard.REGION)
				.disable(FeatureFlags.Game.CRYSTAL)
				.disable(FeatureFlags.Game.ANCHOR);
	}
}
