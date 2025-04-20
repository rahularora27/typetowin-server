package com.rahul.typetowin;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NatsConfig {

    private static final Logger logger = LoggerFactory.getLogger(NatsConfig.class);

    @Value("${nats.server.url}")
    private String natsServerUrl;

    @Value("${nats.server.user}")
    private String natsServerUser;

    @Value("${nats.server.password}")
    private String natsServerPassword;

    @Bean
    public Connection natsConnection() {
        Connection nc = null;
        try {
            Options options = new Options.Builder()
                    .server(natsServerUrl)
                    .userInfo(natsServerUser, natsServerPassword)
                    .build();

            nc = Nats.connect(options);
            logger.info("Successfully connected to NATS server at: {}", natsServerUrl);
        } catch (Exception e) {
            logger.error("Failed to connect to NATS server: {}", e.getMessage(), e);
        }
        return nc;
    }
}
