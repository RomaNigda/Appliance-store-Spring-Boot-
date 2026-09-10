package com.epam.rd.autocode.assessment.appliances.security;

import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.entity.UserEntity;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        // Шукаємо серед клієнтів
        return clientRepository.findByEmail(email)
                .map(user -> {
                    log.debug("Found client: {} with email: {}", user.getName(), user.getEmail());
                    return (UserDetails) user;
                })
                .orElseGet(() -> {
                    // Шукаємо серед працівників
                    EmployeeEntity employee = employeeRepository.findByEmail(email)
                            .orElseThrow(() -> {
                                log.warn("User not found with email: {}", email);
                                return new UsernameNotFoundException("User not found with email: " + email);
                            });
                    log.debug("Found employee: {} with email: {}", employee.getName(), employee.getEmail());
                    return (UserDetails) employee;
                });
    }
}