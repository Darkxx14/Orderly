package com.xyrisdev.orderly.menu.state.item.option.wrapper;

import com.xyrisdev.orderly.menu.state.item.option.ItemStateOption;

import java.util.List;

public record ItemStateOptions<T extends Enum<T>>(T defaultValue, List<ItemStateOption<T>> options) {}
