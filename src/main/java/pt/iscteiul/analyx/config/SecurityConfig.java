package pt.iscteiul.analyx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// 1. Authorization rules
		http.cors(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(
								"/user/login",
								"/user/new-user",
								"/user/sign-up",
								"/css/**",
								"/js/**",
								"favicon.ico",
								"/node_modules/bootstrap/dist/css/**",
								"/node_modules/bootstrap/dist/js/**"
						)
						.permitAll() // Allow access to login and static resources
						.anyRequest().authenticated() // All other requests require authentication
				)
				// 2. Form Login configuration
				.formLogin(form -> form
						.loginPage("/user/login") // Specify custom login page URL (GET mapping)
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/", true) // Redirect after successful login
						.permitAll()
				)
				// 3. Logout configuration
				.logout(logout -> logout
						.logoutSuccessUrl("/user/login?logout") // Redirect after successful logout
						.permitAll()
				)
		;
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
