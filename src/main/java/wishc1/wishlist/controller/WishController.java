package wishc1.wishlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wishc1.wishlist.model.Wish;
import wishc1.wishlist.service.WishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes("readyWishes") // Store "ready" wishes in session for temporary storage
public class WishController {

    private final WishService wishService;

    @Autowired
    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    // Initialize the "ready" wishes list in session if not already present
    @ModelAttribute("readyWishes")
    public List<Wish> readyWishes() {
        return new ArrayList<>();
    }

    /**
     * Displays the form for adding a new wish to the repository (available items list).
     */
    @GetMapping("/add")
    public String showNewWishForm(Model model) {
        model.addAttribute("wish", new Wish());
        return "add-wish"; // Renders the new-wish.html template
    }

    /**
     * Processes the creation of a new wish and adds it to the available items list.
     */
    @PostMapping("/add")
    public String addNewWish(@ModelAttribute("wish") Wish wish, RedirectAttributes redirectAttributes) {
        wishService.saveWish(wish);
        redirectAttributes.addFlashAttribute("success", "New wish added to available items.");
        return "redirect:/wishes";
    }

    /**
     * Show form for adding a wish to the "ready" list.
     */
    @GetMapping("/wishes/add")
    public String showAddWishForm(Model model) {
        model.addAttribute("wish", new Wish());  // Make sure a new Wish object is added to the model
        return "add-wish";  // Renders the "add-wish.html" template
    }

    /**
     * Process adding a single wish to the "ready" list (temporary storage).
     */
    @PostMapping("/wishes/ready")
    public String addWishToReadyList(@ModelAttribute("wish") Wish wish,
                                     @ModelAttribute("readyWishes") List<Wish> readyWishes,
                                     RedirectAttributes redirectAttributes) {
        readyWishes.add(wish); // Add the wish to the session-based "ready" list
        redirectAttributes.addFlashAttribute("success", "Wish added to ready list.");
        return "redirect:/wishes/ready";  // Redirect to view "ready" wishes
    }

    /**
     * Show the list of "ready" wishes with an option to add all of them to the database.
     */
    @GetMapping("/wishes/ready")
    public String viewReadyWishes(@ModelAttribute("readyWishes") List<Wish> readyWishes, Model model) {
        model.addAttribute("readyWishes", readyWishes); // Pass the "ready" wishes list to the view
        return "ready-wishes";  // Returns the "ready-wishes.html" Thymeleaf template
    }

    /**
     * Add all "ready" wishes to the database and clear the session list.
     */
    @PostMapping("/wishes/addAll")
    public String finalizeReadyWishes(@ModelAttribute("readyWishes") List<Wish> readyWishes, RedirectAttributes redirectAttributes) {
        wishService.addWishes(readyWishes); // Save all "ready" wishes to the database
        readyWishes.clear(); // Clear the "ready" list after saving
        redirectAttributes.addFlashAttribute("success", "All ready wishes have been saved.");
        return "redirect:/wishes";  // Redirect to the main list of saved wishes
    }

    /**
     * Show a list of all saved wishes in the database.
     */
    @GetMapping("/wishes")
    public String listWishes(Model model) {
        List<Wish> wishes = wishService.getAllWishes(); // Get all saved wishes from the database
        model.addAttribute("wishes", wishes); // Pass the list of saved wishes to the view
        return "list-wishes";  // Returns the "list-wishes.html" Thymeleaf template
    }

    @GetMapping("/wishes/edit")
    public String showEditWishesPage(Model model) {
        List<Wish> wishes = wishService.getAllWishes(); // Fetch all wishes
        model.addAttribute("wishes", wishes); // Add to model for display
        return "edit-wish"; // Returns the edit-wishes.html template
    }

    /**
     * Process updating a specific wish and save the changes.
     */
    @PostMapping("/wishes/{id}/update")
    public String updateWish(@PathVariable Long id, @ModelAttribute("wish") Wish wishDetails, RedirectAttributes redirectAttributes) {
        wishService.updateWish(id, wishDetails); // Update the wish in the database
        redirectAttributes.addFlashAttribute("success", "Wish updated successfully.");
        return "redirect:/wishes";  // Redirect to the main list of saved wishes
    }


    @PostMapping("/wishes/deleteSelected")
    public String deleteSelectedWishes(@RequestParam List<Long> selectedWishes) {
        for (Long wishId : selectedWishes) {
            wishService.deleteWish(wishId); // Delete each selected wish
        }
        return "redirect:/wishes/edit"; // Redirect back to the edit page
    }

    @PostMapping("/wishes/updateAll")
    public String updateSelectedWishes(@RequestParam List<Long> selectedWishes,
                                       @RequestParam List<String> names,
                                       @RequestParam List<String> descriptions) {
        for (int i = 0; i < selectedWishes.size(); i++) {
            Long wishId = selectedWishes.get(i);
            Wish updatedWish = wishService.getWishById(wishId)
                    .orElseThrow(() -> new RuntimeException("Wish not found with id " + wishId));
            updatedWish.setName(names.get(i));
            updatedWish.setDescription(descriptions.get(i));
            wishService.saveWish(updatedWish); // Persist updates
        }
        return "redirect:/wishes/edit"; // Redirect back to the edit page
    }
}
