package com.xyrisdev.orderly.menu.state.order.option.wrapper;

import com.xyrisdev.orderly.menu.state.order.option.OrderStateOption;

import java.util.List;

public record OrderStateOptions<T extends Enum<T>>(T defaultValue, List<OrderStateOption<T>> options) {}
