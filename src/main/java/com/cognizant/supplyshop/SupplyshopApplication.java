package com.cognizant.supplyshop;

import com.cognizant.supplyshop.model.Role;
import com.cognizant.supplyshop.model.User;
import com.cognizant.supplyshop.repository.UserRepository;
import com.cognizant.supplyshop.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCrypt;

@SpringBootApplication
public class SupplyshopApplication implements CommandLineRunner {
	@Autowired UserRepository userRepository;
	@Autowired FilesStorageService filesStorageService;

	public static void main(String[] args) {
		SpringApplication.run(SupplyshopApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		filesStorageService.init(); // Initialize upload folder
		// If db is empty, initialize admin user
		if(userRepository.count() == 0) {
			User user = new User();
			user.setName("Admin Admin");
			user.setEmail("admin@admin.com");
			user.setPassword(BCrypt.hashpw("pass1234", BCrypt.gensalt(12)));
			user.setActive(true);
			user.setRole(Role.ADMIN);
			userRepository.save(user);
		}
	}
}
