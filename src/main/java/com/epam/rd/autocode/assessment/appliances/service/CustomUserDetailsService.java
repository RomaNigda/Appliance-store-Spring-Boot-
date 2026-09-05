package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.entity.ClientEntity;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.User;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository,
                                    ClientRepository clientRepository) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Спочатку шукаємо серед Employee
        Optional<EmployeeEntity> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            return employee.get();
        }

        // Потім серед Client
        Optional<ClientEntity> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            return client.get();
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}