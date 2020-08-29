package com.isaccanedo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.*;

@SpringBootApplication
public class GracefulShutdownExampleApplication {

	//Thread Pool which runs some tasks
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

	public static void main(String[] args) {
		SpringApplication.run(GracefulShutdownExampleApplication.class, args);
	}

	@RestController
	class UserController {

		@GetMapping("/hello")
		public String hello() throws InterruptedException {
			System.out.println("Waiting for 5 seconds...");
			executor.execute(this::task);
			return "Hello World";
		}

		private void task() {
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Completed 5 seconds...");
		}
	}

	@PreDestroy
	public void destroy() {
		System.out.println("Triggered PreDestroy");

		//Verify if the threads have completed their tasks and then proceed with shutdown
		while (executor.getActiveCount() > 0) {
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Completed all active threads");
	}
}
