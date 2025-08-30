package it.unipi.adrien.koumgang.smartnewsagreggatortomcat;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "Smart News Aggregator API",
                version = "1.0.0",
                description = "Articles Aggregator API with Swagger 3 (OpenAPI)",
                contact = @Contact(name = "Adrien Koumgang", email = "adrientkoumgang@gmail.com")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local"),
                @Server(url = "http://localhost:8080", description = "Production")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@ApplicationPath("/api")
public class SmartNewsAggregatorApplication extends Application {

}