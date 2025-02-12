package tim.field.application.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import tim.field.application.User.service.CustomUserDetailsService;
import tim.field.application.User.service.UserService;
import tim.field.application.logging.filter.LoggingFilter;
import tim.field.application.logging.service.LogService;
import tim.field.application.util.GlobalResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private CustomLogoutHandler customLogoutHandler;

    @Autowired
    private LoggingFilter loggingFilter;

    @Autowired
    private final LogService logService;

    @Autowired
    private final UserService userService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, LogService logService, UserService userService) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
        this.userService = userService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(customUserDetailsService, jwtUtil, authenticationManager);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, customUserDetailsService, logService, userService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        JwtAuthenticationFilter jwtAuthenticationFilter = jwtAuthenticationFilter(authenticationManager);
        JwtAuthorizationFilter jwtAuthorizationFilter = jwtAuthorizationFilter();

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll()
                // 2FA requer privilégio específico
                .requestMatchers("/api/2fa/verify").hasAuthority("2FA_PRIVILEGE")
                // Outros endpoints por privilégio
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN_PRIVILEGE")
                .requestMatchers("/api/user/**").hasAuthority("USER_PRIVILEGE")
                .requestMatchers("/api/admin/permissions/**").hasAuthority("ADMIN_PRIVILEGE")
                .requestMatchers("/api/admin/groups/**").hasAuthority("ADMIN_PRIVILEGE")
                // Todos os outros requerem autenticação completa
                .anyRequest().authenticated()
            )
            // Configuração do AccessDeniedHandler
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            )
            // Adiciona os filtros na ordem correta
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Autenticação JWT
            .addFilterAfter(jwtAuthorizationFilter, JwtAuthenticationFilter.class)               // Autorização baseada em JWT
            .addFilterAfter(loggingFilter, JwtAuthorizationFilter.class)                         // Logging após autorização
            // Configuração de logout
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Remove o cookie de access token
                    Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", null);
                    // Configura o cookie para expirar imediatamente
                    accessTokenCookie.setHttpOnly(true);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setMaxAge(0);
                    // Adiciona o cookie na resposta
                    response.addCookie(accessTokenCookie);
                    // Define a resposta de sucesso
                    response.setContentType("application/json; charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(
                        GlobalResponse.success("Logout realizado com sucesso.", null).toJson()
                    );
                })
                .deleteCookies("JWT-TOKEN", "ACCESS_TOKEN")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:5501", "http://localhost:5501", "http://localhost:5502", "http://localhost:5173", "https://localhost:5173", "http://192.168.100.59:5173", "https://192.168.100.59:5173", "http://192.168.10.15:5173", "https://192.168.10.16:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }
}