package com.junive.employee.employee;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
public class EmployeeConfig {

    @Bean
    CommandLineRunner commandLineRunner(EmployeeRepository repository) {
        return args -> {
            repository.deleteAll();

            Employee emp1 = new Employee( "Chris Malones",  "chris@gmail.com", "UX/UI",  "00303949839",
                    "https://bootdey.com/img/Content/avatar/avatar3.png", UUID.randomUUID().toString());
            Employee emp2 = new Employee( "Alex Porter",  "alex@gmail.com", "PHP",  "00435359839",
                    "https://bootdey.com/img/Content/avatar/avatar5.png", UUID.randomUUID().toString());
            Employee emp3 = new Employee( "John Doe",  "does@gmail.com", "Javascript",  "23442949839",
                    "https://bootdey.com/img/Content/avatar/avatar3.png", UUID.randomUUID().toString());
            Employee emp4 = new Employee( "Sarah Jones",  "sarah@gmail.com", "SQL",  "00d435359839",
                    "https://bootdey.com/img/Content/avatar/avatar4.png", UUID.randomUUID().toString());


            repository.saveAll(List.of(emp1, emp2, emp3, emp4));

        };



    }

}
