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
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.security.CustomUserDetails;
import wishc1.wishlist.service.AppUserService;

@Controller
public class AuthController {
    private final AppUserService appUserService;

    @Autowired
    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // Registration Page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            // Redirect to dashboard if the user is already authenticated
            return "redirect:/profile";
        }

        // Otherwise, show the registration page
        model.addAttribute("appUser", new AppUser());  // Empty form data object
        return "register";  // Returns the "register.html" Thymeleaf template
    }

    // Process Registration
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("appUser") AppUser appUser, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // Check if email is already registered
        if (result.hasErrors()) {
            return "register";  // Reload the form with error messages if validation fails
        }

        // Save the new user
        appUserService.saveUser(appUser);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    // Login Page
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";  // Returns the "login.html" Thymeleaf template
    }

    // Profile Page
    @GetMapping("/profile")
    public String userProfile() {
        return "redirect:/wishlists/profile";
    }
}


