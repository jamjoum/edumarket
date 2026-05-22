package com.edumarket;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║              EduMarket – Backend Application             ║
 * ╠══════════════════════════════════════════════════════════╣
 * ║  Stack : Spring Boot 3.3 · Java 21 · PostgreSQL          ║
 * ║  Features Java 21 :                                      ║
 * ║    • Virtual Threads (spring.threads.virtual.enabled)    ║
 * ║    • Switch Expressions / Pattern Matching               ║
 * ║    • Records                                             ║
 * ║  Design Patterns :                                       ║
 * ║    • MVC   → Controllers / Services / Repositories       ║
 * ║    • Strategy → PaymentStrategy (CC, PayPal, Crypto)     ║
 * ║    • Proxy    → CourseServiceProxy (cache + audit)       ║
 * ║    • Factory  → SubscriptionFactory                      ║
 * ║    • Singleton → Spring Beans (scope par défaut)         ║
 * ╚══════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title       = "EduMarket API",
        version     = "1.0.0",
        description = "Plateforme de cours en ligne – API REST complète",
        contact     = @Contact(name = "EduMarket Team", email = "api@edumarket.io"),
        license     = @License(name = "MIT")
    )
)
public class EduMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduMarketApplication.class, args);
    }
}
