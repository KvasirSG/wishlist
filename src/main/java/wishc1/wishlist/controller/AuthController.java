package wishc1.wishlist.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wishc1.wishlist.exception.UserAlreadyExistsException;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.repository.AppUserRepository;
import wishc1.wishlist.security.CustomUserDetails;
import wishc1.wishlist.service.AppUserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Controller
public class AuthController {
    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final MessageSource messageSource;

    @Autowired
    public AuthController(AppUserService appUserService, AppUserRepository appUserRepository, MessageSource messageSource) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.messageSource = messageSource;
    }

    // Registration Page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            // Redirect to profile if the user is already authenticated
            return "redirect:/profile";
        }

        // Otherwise, show the registration page
        model.addAttribute("appUser", new AppUser());  // Empty form data object
        return "register";  // Returns the "register.html" Thymeleaf template
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("appUser") AppUser appUser, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // Check for validation errors
        if (result.hasErrors()) {
            return "register";  // Reload the registration page with validation errors
        }

        try {
            appUserService.saveUser(appUser);
            // Retrieve the localized success message
            String successMessage = messageSource.getMessage("registration.success", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("success", successMessage);
            return "redirect:/login";
        } catch (UserAlreadyExistsException e) {
            if (e.getMessage().contains("Email")) {
                result.rejectValue("email", "error.appUser", e.getMessage());  // Add error to email field
            } else if (e.getMessage().contains("Username")) {
                result.rejectValue("username", "error.appUser", e.getMessage());  // Add error to username field
            }
            return "register";  // Reload the registration page with error message
        }
    }


    // Login Page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";  // Returns the "login.html" Thymeleaf template
    }

    @GetMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser appUser = userDetails.getAppUser();
            model.addAttribute("appUser", appUser);
        }
        return "profile";
    }
}


