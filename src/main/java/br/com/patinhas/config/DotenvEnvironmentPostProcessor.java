package br.com.patinhas.config;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

	private static final String PROPERTY_SOURCE_NAME = "dotenv";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Map<String, Object> properties = DotenvLoader.loadAsPropertyMap();
		if (properties.isEmpty()) {
			return;
		}

		environment.getPropertySources().addAfter(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
				new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
