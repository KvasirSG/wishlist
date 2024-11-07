package wishc1.wishlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wishc1.wishlist.model.AppUser;
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
        model.addAttribute("appUser", new AppUser());  // Empty form data object
        return "register";  // Returns the "register.html" Thymeleaf template
    }

    // Process Registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("appUser") AppUser appUser, Model model, RedirectAttributes redirectAttributes) {
        // Check if email is already registered
        if (appUserService.findByEmail(appUser.getEmail()).isPresent()) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
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
}
