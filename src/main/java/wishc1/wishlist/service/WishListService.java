package wishc1.wishlist.service;

import org.springframework.transaction.annotation.Transactional;
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
    private final WishService wishService;

    @Autowired
    public WishListService(WishListRepository wishListRepository, WishService wishService) {
        this.wishListRepository = wishListRepository;
        this.wishService = wishService;
    }

    public WishList createWishList(String eventName, LocalDate eventDate, AppUser owner) {
        WishList wishList = new WishList(eventName, eventDate, owner);
        return wishListRepository.save(wishList);
    }

    @Transactional(readOnly = true)
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
    @Transactional
    public WishList saveWishList(WishList wishList) {
        // Ensure all Wishes in the WishList are saved
        for (Wish wish : wishList.getWishes()) {
            if (wish.getId() == null) {  // if Wish is not yet persisted
                wishService.saveWish(wish);  // persist each Wish
            }
        }
        return wishListRepository.save(wishList);  // now save WishList
    }

    public void shareWishListWithUser(WishList wishList, AppUser user) {
        wishList.addViewer(user);
        wishListRepository.save(wishList);
    }
    @Transactional(readOnly = true)
    public List<WishList> getWishListsSharedWithUser(AppUser user) {
        return wishListRepository.findByViewersContaining(user);
    }

    @Transactional(readOnly = true)
    public List<WishList> getWishListsByOwner(Long ownerId) {
        return wishListRepository.findAllByOwnerId(ownerId);
    }

    public List<WishList> getWishListsByIds(List<Long> ids) {
        return wishListRepository.findAllById(ids);
    }

    public void removeWishFromWishList(WishList wishList, Wish wish) {
        wishList.getWishes().remove(wish);  // Remove the wish from the list
        wishListRepository.save(wishList);   // Update the wishlist in the database
    }

    public void deleteWishListById(Long id) {
        wishListRepository.deleteById(id);
    }




}
