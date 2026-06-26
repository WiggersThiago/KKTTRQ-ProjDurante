package br.com.patinhas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.patinhas.config.DotenvLoader;

@SpringBootApplication
public class PatinhasApplication {

	public static void main(String[] args) {
		DotenvLoader.loadIntoSystemProperties();
		SpringApplication.run(PatinhasApplication.class, args);
	}

}
