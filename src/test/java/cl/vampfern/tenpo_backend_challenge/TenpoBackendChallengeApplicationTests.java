package cl.vampfern.tenpo_backend_challenge;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TenpoBackendChallengeApplication.class, properties = "execute.db.script=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TenpoBackendChallengeApplicationTests {

	private static RedisServer redisServer;

	@BeforeAll
	public static void startRedisServer() {
		redisServer = new RedisServerBuilder().port(6399).setting("maxmemory 256M").build();
		redisServer.start();
	}

	@AfterAll
	public static void stopRedisServer() {
		redisServer.stop();
	}

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
