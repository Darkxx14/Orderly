package com.xyrisdev.orderly.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public final class NumberUtil {

	public static Optional<Integer> toInt(String value) {
		if (value == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(Integer.parseInt(value.trim()));
		} catch (NumberFormatException ignored) {
			return Optional.empty();
		}
	}

	public static Optional<Long> toLong(String value) {
		if (value == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(Long.parseLong(value.trim()));
		} catch (NumberFormatException ignored) {
			return Optional.empty();
		}
	}

	public static Optional<Double> toDouble(String value) {
		if (value == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(Double.parseDouble(value.trim()));
		} catch (NumberFormatException ignored) {
			return Optional.empty();
		}
	}

	public static Optional<BigDecimal> toBigDecimal(String value) {
		if (value == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(new BigDecimal(value.trim()));
		} catch (Exception ignored) {
			return Optional.empty();
		}
	}

	public static int toIntOrDefault(String value, int fallback) {
		return toInt(value).orElse(fallback);
	}

	public static long toLongOrDefault(String value, long fallback) {
		return toLong(value).orElse(fallback);
	}

	public static double toDoubleOrDefault(String value, double fallback) {
		return toDouble(value).orElse(fallback);
	}

	public static BigDecimal toBigDecimalOrDefault(String value, BigDecimal fallback) {
		return toBigDecimal(value).orElse(fallback);
	}

	public static boolean isInt(String value) {
		return toInt(value).isPresent();
	}

	public static boolean isLong(String value) {
		return toLong(value).isPresent();
	}

	public static boolean isDouble(String value) {
		return toDouble(value).isPresent();
	}

	public static boolean isBigDecimal(String value) {
		return toBigDecimal(value).isPresent();
	}
}
