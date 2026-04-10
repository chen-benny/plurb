package org.plurb.plurb;

import org.plurb.panorama.repository.TagRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.plurb")
@EnableJpaRepositories(basePackages = "org.plurb")
public class PlurbApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlurbApplication.class, args);
    }

}
