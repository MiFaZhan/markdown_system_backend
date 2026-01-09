package org.example.markdown_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.mifazhan")
@MapperScan("com.mifazhan.mapper")
public class MarkdownSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkdownSystemApplication.class, args);
	}

}