package com.accesscontrol.config;

import com.accesscontrol.entity.Role;
import com.accesscontrol.service.AccessRequestService;
import com.accesscontrol.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final AccessRequestService accessRequestService;

    public SecurityConfig(UserService userService, AccessRequestService accessRequestService) {
        this.userService = userService;
        this.accessRequestService = accessRequestService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login", "/css/**").permitAll()
                        .requestMatchers("/request_access", "/requests", "/request_edit").authenticated()
                        .requestMatchers("/approve", "/reject").hasRole("OWNER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/requests")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.accesscontrol.entity.User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            List<String> roles = accessRequestService.getUserRoles(user)
                    .stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            if (roles.isEmpty()) {
                roles.add("USER");
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(roles.toArray(new String[0]))
                    .build();
        };
    }
}