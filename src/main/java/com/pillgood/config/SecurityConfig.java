package com.pillgood.config;

import com.pillgood.security.CustomAuthenticationSuccessHandler;
import com.pillgood.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@CrossOrigin(origins = {"http://43.202.201.149:8080"}) // 프론트엔드 URL
@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler successHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/resources/**", "/public/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/login", "/css/**", "/images/**", "/js/**").permitAll()
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/uploads/**").permitAll() // 업로드된 파일 경로 허용
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/mypage").authenticated()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // 로그인 페이지 설정
                        // 사용자가 localhost:8080/login 페이지에서 로그인 폼을 작성하고 제출
                        // -> /api/members/login 으로 post 요청 전달
                        // 그러므로, 스프링 시큐리티는 '/login' url로 오는 post 요청을 처리
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .permitAll()
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("/login")
                )
                // 스프링 시큐리티와 커스텀 로그아웃 핸들러가 서로 충돌할 수 있음
                .logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                    .logoutSuccessUrl("/") // 로그아웃 성공 시 홈 페이지로 리다이렉트
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                )
                .cors(withDefaults())
                .csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://43.202.201.149:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedMethods(
//                        		HttpMethod.GET.name(),
//                        		HttpMethod.POST.name(),
//                        		HttpMethod.PUT.name(),
//                        		HttpMethod.DELETE.name(),
//                        		HttpMethod.OPTIONS.name()
//                        		)
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:./uploads/");
            }
        };
    }
}