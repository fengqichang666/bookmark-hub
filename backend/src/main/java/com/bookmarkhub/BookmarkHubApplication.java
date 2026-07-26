package com.bookmarkhub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bookmarkhub.**.mapper")
public class BookmarkHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarkHubApplication.class, args);
	}

}
