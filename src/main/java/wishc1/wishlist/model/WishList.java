package wishc1.wishlist.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;

    private LocalDate eventDate;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToMany
    @JoinTable(
            name = "wishlist_wishes",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "wish_id")
    )
    private List<Wish> wishes;

    @ManyToMany
    @JoinTable(
            name = "wishlist_viewers",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> viewers;

    public WishList() {}

    public WishList(String eventName, LocalDate eventDate, AppUser owner) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.owner = owner;
        this.viewers = new HashSet<>();  // Initialize viewers to avoid NullPointerException
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public void setWishes(List<Wish> wishes) {
        this.wishes = wishes;
    }

    public void addWish(Wish wish) {
        this.wishes.add(wish);
    }

    // Getters and setters for all fields

    public Set<AppUser> getViewers() {
        return viewers;
    }

    public void setViewers(Set<AppUser> viewers) {
        this.viewers = viewers;
    }

    public void addViewer(AppUser viewer) {
        this.viewers.add(viewer);
    }

    public void removeViewer(AppUser viewer) {
        this.viewers.remove(viewer);
    }
}



