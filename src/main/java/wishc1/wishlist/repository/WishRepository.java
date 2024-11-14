package wishc1.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wishc1.wishlist.model.Wish;

import java.util.Optional;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    // Find a wish by name
    Optional<Wish> findByName(String name);
}
