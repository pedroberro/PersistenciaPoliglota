package org.example.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
	private DateUtil() {}

	public static final DateTimeFormatter ISO_INSTANT = DateTimeFormatter.ISO_INSTANT;

	public static Instant parseInstant(String iso) {
		return Instant.parse(iso);
	}

	public static String toIsoString(Instant i) {
		return i == null ? null : ISO_INSTANT.format(i);
	}

	public static OffsetDateTime nowOffset() {
		return OffsetDateTime.now(ZoneOffset.UTC);
	}

	public static OffsetDateTime toOffsetDateTime(Instant i) {
		return i == null ? null : i.atOffset(ZoneOffset.UTC);
	}
}
