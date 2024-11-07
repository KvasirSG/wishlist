package wishc1.wishlist.model;

import jakarta.persistence.GenerationType;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    // Constructors, Getters, and Setters
    public Wish() {}

    public Wish(String name, String description, LocalDateTime addedDate) {
        this.name = name;
        this.description = description;
        this.addedDate = addedDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
}