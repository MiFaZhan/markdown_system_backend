package com.mifazhan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mifazhan.mapper")
public class MarkdownSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkdownSystemApplication.class, args);
	}

}