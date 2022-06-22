package thkoeln.coco.ad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import thkoeln.coco.atc.Hiccup;


@SpringBootApplication
public class ProjectApplication {
	@Bean
	public Hiccup hiccup() {
		return Hiccup.galaxyNGC1300_small();
	}

	/**
	 * Entry method
	 * @param args
	 */
	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(ProjectApplication.class).profiles("hiccup").run(args);
	}
}
