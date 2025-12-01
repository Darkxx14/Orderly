package com.xyrisdev.orderly;

import com.xyrisdev.library.AbstractPlugin;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public final class OrderPlugin extends AbstractPlugin {

	@Getter
	private static OrderPlugin instance;

	@Override
	public void load() {
		instance = this;
	}

	@Override
	public void run() {
		Orderly.INSTANCE.start(this);
	}

	@Override
	public void shutdown() {
		Orderly.INSTANCE.shutdown();
		instance = null;
	}
}
