package br.com.patinhas.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DotenvLoader {

	private DotenvLoader() {
	}

	public static void loadIntoSystemProperties() {
		findEnvFile().ifPresent(envFile -> parse(envFile).forEach((key, value) -> {
			if (System.getenv(key) == null && System.getProperty(key) == null) {
				System.setProperty(key, value);
			}
		}));
	}

	public static Map<String, Object> loadAsPropertyMap() {
		return findEnvFile()
				.map(file -> new HashMap<String, Object>(parse(file)))
				.orElseGet(HashMap::new);
	}

	private static Optional<Path> findEnvFile() {
		Path envFile = Path.of(System.getProperty("user.dir"), ".env");
		return Files.isRegularFile(envFile) ? Optional.of(envFile) : Optional.empty();
	}

	private static Map<String, String> parse(Path envFile) {
		Map<String, String> properties = new HashMap<>();
		try {
			String content = Files.readString(envFile, StandardCharsets.UTF_8);
			if (content.startsWith("\uFEFF")) {
				content = content.substring(1);
			}
			for (String line : content.split("\\R")) {
				parseLine(line).ifPresent(entry -> properties.put(entry.key(), entry.value()));
			}
		} catch (IOException ignored) {
			// .env ausente ou ilegível: segue com variáveis de ambiente do SO
		}
		return properties;
	}

	private static Optional<EnvEntry> parseLine(String line) {
		String trimmed = line.trim();
		if (trimmed.isEmpty() || trimmed.startsWith("#")) {
			return Optional.empty();
		}

		int separator = trimmed.indexOf('=');
		if (separator <= 0) {
			return Optional.empty();
		}

		String key = trimmed.substring(0, separator).trim();
		String value = unquote(trimmed.substring(separator + 1).trim());
		if (key.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(new EnvEntry(key, value));
	}

	private static String unquote(String value) {
		if (value.length() >= 2) {
			char first = value.charAt(0);
			char last = value.charAt(value.length() - 1);
			if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
				return value.substring(1, value.length() - 1);
			}
		}
		return value;
	}

	private record EnvEntry(String key, String value) {
	}

}
