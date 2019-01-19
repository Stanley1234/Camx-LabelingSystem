package com.camx.labeling;

import com.camx.labeling.config.RestConfig;
import com.camx.labeling.entity.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RestConfig.class, DaoConfig.class, JpaConfig.class})
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class);
   }
}
