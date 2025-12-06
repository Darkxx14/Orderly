package com.xyrisdev.orderly.util;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class FormatUtil {

	private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance(Locale.ENGLISH);
	private static final DecimalFormat DECIMAL = new DecimalFormat("#,###.##");

	public static @NotNull String format(long value) {
		return FORMATTER.format(value);
	}

	public static @NotNull String format(int value) {
		return FORMATTER.format(value);
	}

	public static String format(double value) {
		if (value % 1 == 0) {
			return FORMATTER.format((long) value);
		}
		return DECIMAL.format(value);
	}

	public static @NotNull String compact(long value) {
		double abs = Math.abs(value);

		if (abs >= 1_000_000_000L) {
			return trim(value / 1_000_000_000D) + "B";
		}

		if (abs >= 1_000_000L) {
			return trim(value / 1_000_000D) + "M";
		}

		if (abs >= 1_000L) {
			return trim(value / 1_000D) + "K";
		}

		return format(value);
	}

	public static String compact(double value) {
		double abs = Math.abs(value);

		if (abs >= 1_000_000_000D) {
			return trim(value / 1_000_000_000D) + "B";
		}

		if (abs >= 1_000_000D) {
			return trim(value / 1_000_000D) + "M";
		}

		if (abs >= 1_000D) {
			return trim(value / 1_000D) + "K";
		}

		return format(value);
	}

	private static String trim(double value) {
		return (value % 1 == 0)
				? String.valueOf((long) value)
				: DECIMAL.format(value);
	}
}
