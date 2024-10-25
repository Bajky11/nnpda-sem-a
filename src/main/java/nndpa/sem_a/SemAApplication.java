package nndpa.sem_a;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SemAApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemAApplication.class, args);
	}

}
