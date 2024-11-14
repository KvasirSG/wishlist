package wishc1.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.model.WishList;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByEventName(String eventName);
    List<WishList> findAllByOwnerId(Long ownerId);
    List<WishList> findByViewersContaining(AppUser viewer);
}
