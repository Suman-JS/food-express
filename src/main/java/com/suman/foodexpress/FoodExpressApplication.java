package com.suman.foodexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FoodExpressApplication {

  public static void main(String[] args) {
    SpringApplication.run(FoodExpressApplication.class, args);
  }
}
