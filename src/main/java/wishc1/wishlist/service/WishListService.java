package wishc1.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.model.Wish;
import wishc1.wishlist.model.WishList;
import wishc1.wishlist.repository.WishListRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;

    @Autowired
    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    public WishList createWishList(String eventName, LocalDate eventDate, AppUser owner) {
        WishList wishList = new WishList(eventName, eventDate, owner);
        return wishListRepository.save(wishList);
    }

    public Optional<WishList> getWishListById(Long id) {
        return wishListRepository.findById(id);
    }

    public Optional<WishList> getWishListEventName(String eventName) {
        return wishListRepository.findByEventName(eventName);
    }

    public List<WishList> getWishListsByOwner(AppUser owner) {
        return wishListRepository.findAll().stream()
                .filter(wishList -> wishList.getOwner().equals(owner))
                .toList();
    }

    public WishList addWishToWishList(WishList wishList, Wish wish) {
        wishList.addWish(wish);
        return wishListRepository.save(wishList);
    }
    public void shareWishListWithUser(WishList wishList, AppUser user) {
        wishList.addViewer(user);
        wishListRepository.save(wishList);
    }

    public List<WishList> getWishListsSharedWithUser(AppUser user) {
        return wishListRepository.findByViewersContaining(user);
    }
}
