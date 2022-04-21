package noroff.boxinatorapi.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(info = @Info(title = "Boxinator API", description = "Boxinator resource server that handles shipments", version = "0.1.0"))
@SecurityScheme(name = "keycloak_implicit", type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/auth",
                tokenUrl = "${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/token",
                scopes = {
                        @OAuthScope(name = "openid", description = "OpenID Connect Endpoints"),
                        @OAuthScope(name = "profile", description = "Standard profile claims excluding phone and email"),
                        @OAuthScope(name = "email", description = "Include email address information in profile"),

                })))
public class OpenApi3Config {}
