package com.xyrisdev.orderly.menu.state.item.option;

import org.jetbrains.annotations.NotNull;

public record ItemStateOption<T extends Enum<T>>(@NotNull T value, @NotNull String displayName, boolean enabled) {}
