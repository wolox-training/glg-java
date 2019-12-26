package wolox.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wolox.training.services.OpenLibraryService;

@Configuration
public class ApplicationConfig {

    @Bean
    public OpenLibraryService openLibraryService() {
        return new OpenLibraryService();
    }
}
