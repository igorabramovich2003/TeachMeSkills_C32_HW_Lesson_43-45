package com.tms.controller;

import com.tms.exception.AgeException;
import com.tms.model.Security;
import com.tms.model.User;
import com.tms.model.dto.RegistrationRequestDto;
import com.tms.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/security")
public class SecurityController {

    public SecurityService securityService;

    @Autowired
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute @Valid RegistrationRequestDto requestDto,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (Objects.equals(error.getCode(), "CustomAge")) {
                    throw new AgeException(error.getDefaultMessage());
                }
                System.out.println(error);
            }
            System.out.println(bindingResult.getAllErrors());
            return "registration";
        }

        Optional<User> resultUser = securityService.registration(requestDto);
        if (resultUser.isPresent()) {
            model.addAttribute("user", resultUser.get());
            return "user";
        }
        return "registration";
    }

    @GetMapping("/{id}")
    public String getSecurityById(@PathVariable Long id, Model model) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) {
            model.addAttribute("security", security.get());
            return "security";
        }
        model.addAttribute("message", "Security record not found");
        return "innerError";
    }

    @GetMapping("/create")
    public String getCreateSecurityPage(Model model) {
        model.addAttribute("security", new Security());
        return "createSecurity";
    }

    @PostMapping("/create")
    public String createSecurity(@ModelAttribute @Valid Security security,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "createSecurity";
        }
        Optional<Security> createdSecurity = securityService.createSecurity(security);
        if (createdSecurity.isPresent()) {
            model.addAttribute("security", createdSecurity.get());
            return "security";
        }
        model.addAttribute("message", "Failed to create record");
        return "innerError";
    }

    @GetMapping("/update/{id}")
    public String getUpdateSecurityPage(@PathVariable Long id, Model model) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) {
            model.addAttribute("security", security.get());
            return "editSecurity";
        }
        model.addAttribute("message", "Security record not found");
        return "innerError";
    }

    @PostMapping("/update/{id}")
    public String updateSecurity(@PathVariable Long id, @ModelAttribute @Valid Security security, Model model) {
        security.setId(id);
        Optional<Security> updatedSecurity = securityService.updateSecurity(security);
        if (updatedSecurity.isPresent()) {
            model.addAttribute("security", updatedSecurity.get());
            return "security";
        }
        model.addAttribute("message", "Failed to update security record");
        return "innerError";
    }

    @GetMapping("/delete/{id}")
    public String deleteSecurity(@PathVariable Long id, Model model) {
        boolean isDeleted = securityService.deleteSecurity(id);
        if (isDeleted) {
            model.addAttribute("message", "Security record deleted successfully");
            return "success";
        }
        model.addAttribute("message", "Failed to delete security record");
        return "innerError";
    }
}
