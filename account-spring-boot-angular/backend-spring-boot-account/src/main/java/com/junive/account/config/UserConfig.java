package com.junive.account.config;

import com.junive.account.model.AppUser;
import com.junive.account.model.Role;
import com.junive.account.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserService userRoleService) {
        return args -> {
            //repository.deleteAll();
            userRoleService.saveRole(new Role(null, "ROLE_USER"));
            userRoleService.saveRole(new Role(null, "ROLE_MANAGER"));
            userRoleService.saveRole(new Role(null, "ROLE_ADMIN"));

            userRoleService.saveUser( new AppUser(null, "John Does",
                    "user", "password", "user@gmail.com", new ArrayList<>()) );
            userRoleService.saveUser( new AppUser(null, "Alex Kane",
                    "admin", "password", "admin@gmail.com" , new ArrayList<>()) );

            //userRoleService.addRoleToUser("user", "ROLE_USER");
            //userRoleService.addRoleToUser("admin", "ROLE_USER");
            userRoleService.addRoleToUser("admin", "ROLE_ADMIN");
            userRoleService.addRoleToUser("admin", "ROLE_MANAGER");
        };
    }

}
