package edu.farmingdale.csc311.lostfound.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        String name = username.trim();

        String error = null;
        if (name.length() < 3) {
            error = "Username must be at least 3 characters.";
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters.";
        } else if (userRepository.existsByUsername(name)) {
            error = "That username is already taken.";
        }

        if (error == null) {
            try {
                userRepository.save(new AppUser(name, passwordEncoder.encode(password)));
            } catch (DataIntegrityViolationException e) {
                error = "That username is already taken.";   // two people registered at once
            }
        }

        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("username", name);
            return "register";
        }
        return "redirect:/login?registered";
    }
}
