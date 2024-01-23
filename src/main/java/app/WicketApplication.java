package app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

//The entry point of the application
//Spring boot application enhanced with Apache Wicket version 9 and Java 11
@SpringBootApplication
public class WicketApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder()
			.sources(WicketApplication.class)
			.run(args);
	}
	
}
