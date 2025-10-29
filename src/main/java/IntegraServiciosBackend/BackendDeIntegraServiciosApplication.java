package IntegraServiciosBackend;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class BackendDeIntegraServiciosApplication {

	private static Logger logger = LoggerFactory.getLogger(BackendDeIntegraServiciosApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BackendDeIntegraServiciosApplication.class, args);
		logger.info("Integraservicios is now running...");
	}

	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
