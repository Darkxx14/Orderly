package com.xyrisdev.orderly.menu.state.model;

import org.jetbrains.annotations.NotNull;

public record StateOption<T extends Enum<T>>(@NotNull T value, @NotNull String displayName, boolean enabled) {}
