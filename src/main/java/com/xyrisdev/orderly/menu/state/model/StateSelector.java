package com.xyrisdev.orderly.menu.state.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public record StateSelector<T extends Enum<T>>(@NotNull List<StateOption<T>> options) {

	public StateSelector(@NotNull List<StateOption<T>> options) {
		this.options = options.stream()
				.filter(StateOption::enabled)
				.toList();
	}

	public @NotNull T first() {
		return options.getFirst().value();
	}

	public @NotNull T rotate(@NotNull T current, boolean forward) {
		final int index = IntStream.range(0, options.size())
				.filter(i -> options.get(i).value() == current)
				.findFirst()
				.orElse(0);

		int next = (index + (forward ? 1 : -1) + options.size()) % options.size();
		return options.get(next).value();
	}
}
