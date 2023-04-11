package com.example.yobee.configuration;


import com.example.yobee.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;


    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",
            /* User */
            "/api/v1/user/signup",
            "/api/v1/user/login",
            "/api/v1/user/login/social",
            "/api/v1/user/refresh",
            "/api/v1/token",
            "/api/v1/user/email-check",
            "/api/v1/user/nickname/duplicate",
            "/api/v1/user/email/duplicate",
            "/api/v1/user/email/auth/check",
            "/api/v1/user/email/issue/password",
            "/api/v1/recipe/getData2"

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(PERMIT_URL_ARRAY).permitAll() // join, login, swagger 은 항상 허용
                .anyRequest().authenticated()
//              .antMatchers(HttpMethod.POST).authenticated()
//              .antMatchers(HttpMethod.GET).authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
