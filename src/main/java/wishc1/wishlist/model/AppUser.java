package wishc1.wishlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    @Size(min = 3, message = "Username must be at least 3 characters")
    @Column(unique = true)
    private String username;

    public AppUser() {}

    public AppUser(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public void setPassword(String encode) {
        this.password = encode;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(@NotBlank @Email String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

