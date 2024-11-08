package wishc1.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wishc1.wishlist.model.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email); // For authentication by email

    // Methods for custom validators
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

