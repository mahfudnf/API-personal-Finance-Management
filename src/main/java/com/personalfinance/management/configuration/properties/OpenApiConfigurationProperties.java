package com.personalfinance.management.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "com.personalfinance.management.swagger")
public class OpenApiConfigurationProperties {
    private Properties properties = new Properties();

    @Data
    public class Properties{
        private String title;
        private String description;
        private String apiVersion;

        private Contact contact = new Contact();
        private Security security = new Security();

        @Data
        public class Contact {
            private String name;
            private String email;
            private String url;
        }

        @Data
        public class Security {
            private String name;
            private String scheme;
            private String bearerFormat;
        }

    }
}
