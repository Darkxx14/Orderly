package com.xyrisdev.orderly.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtil {

	public static String format(@NotNull String raw) {
		if (raw.isEmpty()) {
			return "";
		}

		return Arrays.stream(raw.toLowerCase().split("_"))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
				.collect(Collectors.joining(" "));
	}
}
