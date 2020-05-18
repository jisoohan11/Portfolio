package com.drawgame;

import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.drawgame.config.FileStorageConfig;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageConfig.class })
@ComponentScan({ "com.drawgame", "com.drawgame.controller" })
public class DrawchatApp {

	public static void main(String[] args) {
		SpringApplication.run(DrawchatApp.class, args);
	}

}