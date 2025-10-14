package org.example.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class EncryptionUtil {
	private static final PasswordEncoder encoder = new BCryptPasswordEncoder(10);

	private EncryptionUtil() {}

	public static String hashPassword(String plain) {
		return encoder.encode(plain);
	}

	public static boolean verify(String plain, String hash) {
		return encoder.matches(plain, hash);
	}
}
