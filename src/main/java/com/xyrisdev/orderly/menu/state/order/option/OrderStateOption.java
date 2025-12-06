package com.xyrisdev.orderly.menu.state.order.option;

import org.jetbrains.annotations.NotNull;

public record OrderStateOption<T extends Enum<T>>(@NotNull T value, @NotNull String displayName, boolean enabled) {}
