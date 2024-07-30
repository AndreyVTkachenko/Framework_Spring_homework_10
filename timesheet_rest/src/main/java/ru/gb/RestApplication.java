package ru.gb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import ru.gb.model.*;
import ru.gb.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@EnableDiscoveryClient
@SpringBootApplication
public class RestApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(RestApplication.class, args);

		UserRepository userRepository = ctx.getBean(UserRepository.class);
		RoleRepository roleRepository = ctx.getBean(RoleRepository.class);
		UserRoleRepository userRoleRepository = ctx.getBean(UserRoleRepository.class);

		Role adminRole = createRole(roleRepository, "ADMIN");
		Role userRole = createRole(roleRepository, "USER");
		Role restRole = createRole(roleRepository, "REST");

		User admin = createUserWithRoles(
				userRepository,
				userRoleRepository,
				"admin",
				"$2a$12$LbAPCsHn8ZN5MUDqDmIX7e9n1YlDkCxEt0lW3Q2WuW0M1vteo8jvG",
				List.of(adminRole, userRole)
		); // admin
		User user = createUserWithRoles(
				userRepository,
				userRoleRepository,
				"user",
				"$2a$12$.dlnBAYq6sOUumn3jtG.AepxdSwGxJ8xA2iAPoCHSH61Vjl.JbIfq",
				List.of(userRole)
		); // user
		User rest = createUserWithRoles(
				userRepository,
				userRoleRepository,
				"rest",
				"$2a$12$ZYAuc1p3j.aO8sKVKV0VOeFmmdv0iQpnVZOAqlhYbR0gofAaxultO",
				List.of(restRole)
		); // rest

		ProjectRepository projectRepo = ctx.getBean(ProjectRepository.class);
		for (int i = 1; i <= 5; i++) {
			Project project = new Project();
			project.setName("Project #" + i);
			projectRepo.save(project);
		}

		EmployeeRepository employeeRepo = ctx.getBean(EmployeeRepository.class);
		for (int i = 1; i <= 5; i++) {
			Employee employee = new Employee();
			employee.setName("Employee #" + i);
			employeeRepo.save(employee);
		}

		TimesheetRepository timesheetRepo = ctx.getBean(TimesheetRepository.class);
		LocalDate createdAt = LocalDate.now();
		for (int i = 1; i <= 10; i++) {
			createdAt = createdAt.plusDays(1);

			Timesheet timesheet = new Timesheet();
			timesheet.setProjectId(ThreadLocalRandom.current().nextLong(1, 6));
			timesheet.setEmployeeId(ThreadLocalRandom.current().nextLong(1, 6));
			timesheet.setCreatedAt(createdAt);
			timesheet.setMinutes(ThreadLocalRandom.current().nextInt(100, 1000));

			timesheetRepo.save(timesheet);
		}
	}

	private static Role createRole(RoleRepository roleRepository, String roleName) {
		Role role = new Role();
		role.setName(roleName);
		return roleRepository.save(role);
	}

	private static User createUserWithRoles(
			UserRepository userRepository,
			UserRoleRepository userRoleRepository,
			String login, String password,
			List<Role> roles) {
		User user = new User();
		user.setLogin(login);
		user.setPassword(password);
		user = userRepository.save(user);

		User finalUser = user;
		roles.forEach(role -> {
			UserRole userRoleLink = new UserRole();
			userRoleLink.setUserId(finalUser.getId());
			userRoleLink.setRoleId(role.getId());
			userRoleRepository.save(userRoleLink);
		});

		return user;
	}
}
