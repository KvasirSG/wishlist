package wishc1.wishlist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wishc1.wishlist.model.Wish;
import wishc1.wishlist.repository.WishRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WishService {

    private final WishRepository wishRepository;

    @Autowired
    public WishService(WishRepository wishRepository) {
        this.wishRepository = wishRepository;
    }

    public List<Wish> getAllWishes() {
        return wishRepository.findAll();
    }

    public Wish addWish(Wish wish) {
        wish.setAddedDate(LocalDateTime.now());
        return wishRepository.save(wish);
    }

    public void saveWish(Wish wish) {
        wishRepository.save(wish);
    }

    public void addWishes(List<Wish> wishes) {
        for (Wish wish : wishes) {
            wish.setAddedDate(LocalDateTime.now());
            wishRepository.save(wish);
        }
    }

    public Optional<Wish> getWishById(Long id) {
        return wishRepository.findById(id);
    }

    public Optional<Wish> getWishByName(String name) {
        return wishRepository.findByName(name);
    }

    public Wish updateWish(Long id, Wish wishDetails) {
        Wish wish = wishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wish not found with id " + id));

        wish.setName(wishDetails.getName());
        wish.setDescription(wishDetails.getDescription());
        return wishRepository.save(wish);
    }

    public void deleteWish(Long id) {
        if (wishRepository.existsById(id)) {
            wishRepository.deleteById(id);
        } else {
            throw new RuntimeException("Wish not found with id " + id);
        }
    }
}
