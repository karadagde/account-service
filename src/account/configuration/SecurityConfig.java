package account.configuration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final AuthenticationEntryPoint restAuthenticationEntryPoint;


    public SecurityConfig(
            AuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
            Exception {


        http
                .httpBasic(
                        httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer
                                .authenticationEntryPoint(
                                        restAuthenticationEntryPoint))
//                .httpBasic(Customizer.withDefaults())
//                .exceptionHandling(ex -> ex.authenticationEntryPoint(
//                        restAuthenticationEntryPoint))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions().disable())
                .formLogin().disable()
                .authorizeHttpRequests(auth -> {
                            auth
                                    .requestMatchers(HttpMethod.POST,
                                            "/api/auth/signup")
                                    .permitAll()
                                    .requestMatchers(HttpMethod.POST,
                                            "/api/auth/changepass")
                                    .hasAnyRole("USER", "ADMINISTRATOR",
                                            "ACCOUNTANT")
                                    .requestMatchers(HttpMethod.GET,
                                            "/api/empl/payment")
                                    .hasAnyRole("USER", "ACCOUNTANT")
                                    .requestMatchers(HttpMethod.POST,
                                            "/api/acct/payments")
                                    .hasRole("ACCOUNTANT")
                                    .requestMatchers(HttpMethod.PUT,
                                            "/api/acct/payments")
                                    .hasRole("ACCOUNTANT")
                                    .requestMatchers("/api/admin/user/**")
                                    .hasRole("ADMINISTRATOR")
                                    .requestMatchers(HttpMethod.GET,
                                            "/api/security/events/")
                                    .hasRole("AUDITOR")
                                    .requestMatchers(HttpMethod.POST,
                                            "/actuator/shutdown")
                                    .permitAll();
                        }
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher
            (ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(
                applicationEventPublisher);
    }

    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher
            (ApplicationEventPublisher applicationEventPublisher) {
        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }


}
