package org.scavino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {  DataSourceAutoConfiguration.class,
                                    WebMvcAutoConfiguration.class,
                                    WebSocketServletAutoConfiguration.class,
                                    AopAutoConfiguration.class,
                                    OAuth2ResourceServerAutoConfiguration.class,
                                    EmbeddedWebServerFactoryCustomizerAutoConfiguration.class })
@ComponentScan({ "org.scavino.route", "org.scavino.processor", "org.scavino.config", "org.scavino.service", "org.scavino.converters"  })
public class RouterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouterApplication.class, args);
    }

}