package com.blog.afaq;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        // Load the .env file from your project root
        Dotenv dotenv = Dotenv.configure()
                .directory("C:/Users/ranim/OneDrive/Bureau/blog")
                .ignoreIfMissing()
                .load();

        // Set environment variables as system properties
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME", ""));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD", ""));

        System.out.println("MAIL USER = " + System.getProperty("MAIL_USERNAME"));
        System.out.println("MAIL PASS = " + System.getProperty("MAIL_PASSWORD"));

        SpringApplication.run(BlogApplication.class, args);

    }

}
