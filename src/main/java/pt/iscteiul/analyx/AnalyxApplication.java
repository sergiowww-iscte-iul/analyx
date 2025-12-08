package pt.iscteiul.analyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AnalyxApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyxApplication.class, args);
	}

}
