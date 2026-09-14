package com.epam.rd.autocode.assessment.appliances.util;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.entity.ClientEntity;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.entity.UserEntity;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Отримує поточного авторизованого користувача
     */
    @Loggable
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            
            // Шукаємо спочатку серед клієнтів
            return clientRepository.findByEmail(email)
                    .map(UserEntity.class::cast)
                    .orElseGet(() -> employeeRepository.findByEmail(email)
                            .map(UserEntity.class::cast)
                            .orElseThrow(() -> new SecurityException("User not found with email: " + email))
                    );
        }
        
        throw new SecurityException("Invalid authentication principal");
    }

    /**
     * Отримує поточного клієнта
     */
    public ClientEntity getCurrentClient() {
        UserEntity user = getCurrentUser();
        if (!user.isClient()) {
            throw new SecurityException("Current user is not a client");
        }
        return (ClientEntity) user;
    }

    /**
     * Отримує поточного працівника
     */
    public EmployeeEntity getCurrentEmployee() {
        UserEntity user = getCurrentUser();
        if (!user.isEmployee()) {
            throw new SecurityException("Current user is not an employee");
        }
        return (EmployeeEntity) user;
    }

    /**
     * Перевіряє, чи поточний користувач є Employee
     */
    public boolean isCurrentUserEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    /**
     * Перевіряє, чи поточний користувач є Client
     */
    public boolean isCurrentUserClient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
    }

    /**
     * Отримує email поточного користувача
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}