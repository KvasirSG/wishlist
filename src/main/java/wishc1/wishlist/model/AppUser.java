package wishc1.wishlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{appUser.email.notBlank}")
    @Email(message = "{appUser.email.invalid}")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "{appUser.email.pattern}"
    ) // Enforces a stricter email pattern
    @Column(unique = true)
    private String email;

    @NotBlank(message = "{appUser.password.notBlank}")
    @Size(min = 8, message = "{appUser.password.size}")
    private String password;

    @NotBlank(message = "{appUser.username.notBlank}")
    @Size(min = 3, message = "{appUser.username.size}")
    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishList> wishlists;

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

    public List<WishList> getWishlists() {
        return wishlists;
    }

    public void setWishlists(List<WishList> wishlists) {
        this.wishlists = wishlists;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppUser) {
            AppUser appUser = (AppUser) obj;
            return email.equals(appUser.email);
        }
        return false;
    }

    public void setId(long l) {
        this.id = l;
    }
}

