package com.junive.employee.employee;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junive.employee.employee.exception.UserNotFoundException;

@Service
public class EmployeeService {
	private EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}
	
	public Employee addEmployee(Employee employee) {
		employee.setEmployeeCode(UUID.randomUUID().toString());
		return employeeRepository.save(employee);
	}
	
	public List<Employee> findAllEmployees() {
		return employeeRepository.findAll();
	}
	
	public Employee updateEmployee(Employee employee) {
		if (!employeeRepository.findById(employee.getId()).isPresent()) {
			throw new UserNotFoundException("Update : User by Id " + employee.getId()  + " was not found");
		}
		
		return employeeRepository.save(employee);
	}
	
	public Employee findEmployeeById(Long id) {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Find : User by Id " + id + " was not found"));
	}
	
	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id);
	}
	
}
