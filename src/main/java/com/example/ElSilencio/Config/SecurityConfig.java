package com.example.ElSilencio.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/habitaciones/nuevo", "/registro", "/habitaciones", "/css/**",
                                "/js/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/usuarios/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/reservas/**", "/empleado/**").hasAnyRole("ADMIN", "EMPLEADO")
                        .requestMatchers("/mis-reservas/**", "/clientes/**").hasRole("CLIENTE")
                        .requestMatchers("/habitaciones/**").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(successHandler()) // redirección dinámica según rol
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/usuarios");
            } else if (role.equals("ROLE_EMPLEADO")) {
                response.sendRedirect("/reservas");
            } else if (role.equals("ROLE_CLIENTE")) {
                response.sendRedirect("/mis-reservas");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    // @Bean
    // public UserDetailsService userDetailsService() {
    // UserDetails admin = User.withUsername("admin")
    // .password("{noop}admin123") // {noop} = sin encriptar (solo pruebas)
    // .roles("ADMIN")
    // .build();

    // UserDetails empleado = User.withUsername("empleado")
    // .password("{noop}empleado123")
    // .roles("EMPLEADO")
    // .build();

    // UserDetails cliente = User.withUsername("cliente")
    // .password("{noop}cliente123")
    // .roles("CLIENTE")
    // .build();

    // return new InMemoryUserDetailsManager(admin, empleado, cliente);
    // }
}
