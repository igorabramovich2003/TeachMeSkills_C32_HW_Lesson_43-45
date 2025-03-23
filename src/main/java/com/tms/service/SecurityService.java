package com.tms.service;

import com.tms.model.Security;
import com.tms.model.User;
import com.tms.model.dto.RegistrationRequestDto;
import com.tms.repository.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityService {

    private final SecurityRepository securityRepository;

    @Autowired
    public SecurityService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }
    public Optional<Security> getSecurityById(Long id) {
        return securityRepository.getSecurityById(id);
    }

    public Optional<Security> createSecurity(Security security) {
        boolean isCreated = securityRepository.createSecurity(security);
        return isCreated ? securityRepository.getSecurityById(security.getId()) : Optional.empty();
    }

    public Optional<Security> updateSecurity(Security security) {
        boolean isUpdated = securityRepository.updateSecurity(security);
        return isUpdated ? securityRepository.getSecurityById(security.getId()) : Optional.empty();
    }

    public boolean deleteSecurity(Long id) {
        return securityRepository.deleteSecurity(id);
    }

    public Optional<User> registration(RegistrationRequestDto requestDto) {
        return securityRepository.registration(requestDto);
    }
}
