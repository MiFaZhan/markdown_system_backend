package org.example.markdown_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.mifazhan")
@MapperScan("com.mifazhan.mapper")
public class MarkdownSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkdownSystemApplication.class, args);
	}

	// 配置MyBatis-Plus分页插件
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return interceptor;
	}

}