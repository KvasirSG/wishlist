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
    @Size(min = 8)
    private String password;

    @NotBlank
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

    public String getEmail() {
        return email;
    }
}

