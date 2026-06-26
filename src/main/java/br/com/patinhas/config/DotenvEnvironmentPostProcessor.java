package br.com.patinhas.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final String PROPERTY_SOURCE_NAME = "dotenv";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Path envFile = Path.of(System.getProperty("user.dir"), ".env");
		if (!Files.isRegularFile(envFile)) {
			return;
		}

		Map<String, Object> properties = loadEnvFile(envFile);
		if (properties.isEmpty()) {
			return;
		}

		environment.getPropertySources().addAfter(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
				new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
	}

	private Map<String, Object> loadEnvFile(Path envFile) {
		Map<String, Object> properties = new HashMap<>();
		try {
			List<String> lines = Files.readAllLines(envFile);
			for (String line : lines) {
				parseLine(line).ifPresent(entry -> properties.put(entry.key(), entry.value()));
			}
		} catch (IOException ignored) {
			// .env ausente ou ilegível: segue com variáveis de ambiente do SO
		}
		return properties;
	}

	private java.util.Optional<EnvEntry> parseLine(String line) {
		String trimmed = line.trim();
		if (trimmed.isEmpty() || trimmed.startsWith("#")) {
			return java.util.Optional.empty();
		}

		int separator = trimmed.indexOf('=');
		if (separator <= 0) {
			return java.util.Optional.empty();
		}

		String key = trimmed.substring(0, separator).trim();
		String value = unquote(trimmed.substring(separator + 1).trim());
		if (key.isEmpty()) {
			return java.util.Optional.empty();
		}

		return java.util.Optional.of(new EnvEntry(key, value));
	}

	private String unquote(String value) {
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
