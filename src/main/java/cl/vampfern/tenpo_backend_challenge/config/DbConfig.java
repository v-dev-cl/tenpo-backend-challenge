package cl.vampfern.tenpo_backend_challenge.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

// https://stackoverflow.com/questions/58684134/can-spring-data-r2dbc-generate-a-schema
@Configuration
@EnableR2dbcRepositories
class DbConfig {

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory, @Value("${execute.db.script:true}") boolean executeDbScript) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        if (executeDbScript) {
            initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        }

        return initializer;
    }

}