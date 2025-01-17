package cl.vampfern.tenpo_backend_challenge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class TenpoBackendChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenpoBackendChallengeApplication.class, args);
	}

}
