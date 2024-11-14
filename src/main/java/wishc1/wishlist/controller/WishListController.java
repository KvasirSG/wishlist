package wishc1.wishlist.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.model.Wish;
import wishc1.wishlist.model.WishList;
import wishc1.wishlist.security.CustomUserDetails;
import wishc1.wishlist.service.AppUserService;
import wishc1.wishlist.service.WishListService;
import wishc1.wishlist.service.WishService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/wishlists")
public class WishListController {

    private final WishListService wishListService;
    private final AppUserService appUserService;
    private final WishService wishService;

    @Autowired
    public WishListController(WishListService wishListService, AppUserService appUserService, WishService wishService) {
        this.wishListService = wishListService;
        this.appUserService = appUserService;
        this.wishService = wishService;
    }

    /**
     * Display form to create a new wishlist.
     *
     * @param model the model to hold the WishList object
     * @return the create-wishlist page
     */
    @GetMapping("/create")
    public String showCreateWishListForm(Model model) {
        model.addAttribute("wishList", new WishList());
        return "new-wishlist";
    }

    /**
     * Handle creation of a new wishlist and automatically assign it to the authenticated user.
     *
     * @param wishList the wishlist to be created
     * @param authentication the authentication object containing the current user's details
     * @param redirectAttributes the redirect attributes for success/error messages
     * @return redirect to the main wishlists page
     */
    @PostMapping("/create")
    public String createWishList(@ModelAttribute WishList wishList,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to create a wishlist.");
            return "redirect:/login";
        }

        // Retrieve the currently authenticated user's email from the Authentication object
        AppUser appUser = ((CustomUserDetails) authentication.getPrincipal()).getAppUser();

        // Set the authenticated user as the owner of the new wishlist
        wishList.setOwner(appUser);
        wishListService.createWishList(wishList.getEventName(), wishList.getEventDate(), appUser);

        redirectAttributes.addFlashAttribute("success", "Wishlist created successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/{id}/addWish")
    public String showAddWishForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<WishList> wishList = wishListService.getWishListById(id);

        if (wishList.isPresent()) {
            model.addAttribute("wishList", wishList.get());
            model.addAttribute("availableWishes", wishService.getAllWishes());
        } else {
            redirectAttributes.addFlashAttribute("error", "Wishlist not found.");
            return "redirect:/wishlists/profile";
        }

        return "addWishToWishList"; // This should match the template name below
    }



    /**
     * Add a specific wish to a wishlist.
     *
     * @param id the ID of the wishlist
     * @param wishId the ID of the wish to be added
     * @param redirectAttributes the redirect attributes for success/error messages
     * @return redirect to the main wishlists page
     */
    @PostMapping("/{id}/addWish")
    public String addWishToWishList(@PathVariable Long id, @RequestParam("wishId") Long wishId, RedirectAttributes redirectAttributes) {
        Optional<WishList> wishList = wishListService.getWishListById(id);
        Optional<Wish> wish = wishService.getWishById(wishId);

        if (wishList.isPresent() && wish.isPresent()) {
            wishListService.addWishToWishList(wishList.get(), wish.get());
            redirectAttributes.addFlashAttribute("success", "Wish added to wishlist.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Wish or wishlist not found.");
        }

        // Redirecting to a GET request to display the updated wishlist
        return "redirect:/wishlists/" + id + "/addWish";
    }


    /**
     * Display the user's own wishlists.
     *
     * @return the user-wishlists page
     */
    @GetMapping
    public String viewUserWishLists() {
        return "redirect:/wishlists/profile";
    }

    /**
     * Display the wishlists that have been shared with the user.
     *
     * @param model the model to hold the shared wishlists
     * @return the shared-wishlists page
     */
    @GetMapping("/shared")
    public String viewSharedWishLists(Model model) {
        AppUser currentUser = getCurrentUser();
        List<WishList> sharedWishLists = wishListService.getWishListsSharedWithUser(currentUser);
        model.addAttribute("sharedWishLists", sharedWishLists);
        return "share-wishlist";
    }

    /**
     * Display the form to share multiple wishlists with multiple recipients.
     *
     * @param model the model to hold the wishlists and user list
     * @param authentication the authentication object to retrieve current user
     * @return the share-wishlists page
     */
    @GetMapping("/{id}/share")
    public String showShareWishListForm(@PathVariable Long id, Model model, Authentication authentication) {
        // Check if user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser currentUser = userDetails.getAppUser();

            // Retrieve the specific wishlist by ID
            Optional<WishList> wishList = wishListService.getWishListById(id);

            // If the wishlist exists and belongs to the current user
            if (wishList.isPresent() && wishList.get().getOwner().equals(currentUser)) {
                model.addAttribute("wishList", wishList.get());
                model.addAttribute("users", appUserService.getAllUsers());  // Fetch all users for sharing
                return "share-wishlist";  // Assuming `share-wishlist.html` is your template for sharing
            } else {
                model.addAttribute("error", "Wishlist not found or access denied.");
                return "error"; // Redirect or display an error page
            }
        }
        return "redirect:/login";  // Redirect to login if not authenticated
    }

    @PostMapping("/shareSelected")
    public String shareWishlists(@RequestParam List<Long> wishListIds,
                                 @RequestParam List<String> recipientUsernames,
                                 RedirectAttributes redirectAttributes) {
        // Your logic to handle sharing the wishlists with specified recipients
        redirectAttributes.addFlashAttribute("success", "Wishlist(s) shared successfully.");
        return "redirect:/wishlists/profile"; // Redirect after processing
    }



    /**
     * Share selected wishlists with specified recipients by their emails.
     *
     * @param wishListIds the list of selected wishlist IDs to share
     * @param recipientEmails the list of recipient emails to share with
     * @param redirectAttributes the redirect attributes for success/error messages
     * @return redirect to the wishlists page
     */
    @PostMapping("/shareMultiple")
    public String shareMultipleWishLists(@RequestParam List<Long> wishListIds,
                                         @RequestParam List<String> recipientEmails,
                                         RedirectAttributes redirectAttributes) {
        List<WishList> wishLists = wishListService.getWishListsByIds(wishListIds);
        List<AppUser> recipients = appUserService.getUsersByEmails(recipientEmails);

        if (wishLists.isEmpty() || recipients.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select wishlists and recipients.");
            return "redirect:/wishlists/share";
        }

        // Share each selected wishlist with each selected recipient
        for (WishList wishList : wishLists) {
            for (AppUser recipient : recipients) {
                wishListService.shareWishListWithUser(wishList, recipient);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Selected wishlists shared successfully with recipients.");
        return "redirect:/wishlists";
    }

    /**
     * Retrieve the currently logged-in user.
     *
     * @return the currently logged-in AppUser
     */
    private AppUser getCurrentUser() {
        return appUserService.getLoggedInUser();
    }
    /**
     * Show the form to start a new wishlist with dynamic "ready wishes" and available items.
     */
    @GetMapping("/new")
    public String showNewWishListForm(HttpSession session, Model model) {
        session.setAttribute("readyWishes", new ArrayList<Wish>());  // Initialize empty "ready wishes" list in session
        model.addAttribute("wishList", new WishList());
        model.addAttribute("availableWishes", wishService.getAllWishes());  // Fetch all available wishes
        return "new-wishlist";  // New template for creating the wishlist dynamically
    }

    /**
     * Add a selected wish from the available list to the "ready" list in the session.
     */
    @PostMapping("/addReadyWish")
    public String addReadyWish(@RequestParam("wishId") Long wishId, HttpSession session) {
        List<Wish> readyWishes = (List<Wish>) session.getAttribute("readyWishes");

        // Fetch the selected wish by ID and add a duplicate to readyWishes
        Wish selectedWish = wishService.getWishById(wishId).orElseThrow(() -> new IllegalArgumentException("Invalid wish ID"));
        Wish duplicateWish = new Wish(selectedWish.getName(), selectedWish.getDescription(), selectedWish.getAddedDate());
        readyWishes.add(duplicateWish);

        session.setAttribute("readyWishes", readyWishes);  // Update the session attribute
        return "redirect:/wishlists/new";  // Refresh the form to show updated wishes
    }
    @GetMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser appUser = userDetails.getAppUser();

            model.addAttribute("appUser", appUser);
            model.addAttribute("wishLists", wishListService.getWishListsByOwner(appUser.getId()));  // Fetch user's wishlists
            model.addAttribute("users", appUserService.getAllUsers());  // Fetch all users for sharing

            return "profile";
        }
        return "redirect:/login";
    }

    @GetMapping("/{id}/wishes")
    public String showWishesInWishlist(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<WishList> wishList = wishListService.getWishListById(id);

        if (wishList.isPresent()) {
            AppUser currentUser = ((CustomUserDetails) authentication.getPrincipal()).getAppUser();
            boolean isOwner = wishList.get().getOwner().equals(currentUser);

            model.addAttribute("wishList", wishList.get());
            model.addAttribute("wishes", wishList.get().getWishes());
            model.addAttribute("isOwner", isOwner);  // Pass ownership status to the view
        } else {
            model.addAttribute("error", "Wishlist not found.");
        }

        return "wishlist-wishes";  // Assuming this is the template name
    }

    @PostMapping("/{wishlistId}/wishes/{wishId}/remove")
    public String removeWishFromWishList(@PathVariable Long wishlistId, @PathVariable Long wishId, RedirectAttributes redirectAttributes) {
        Optional<WishList> wishListOptional = wishListService.getWishListById(wishlistId);
        Optional<Wish> wishOptional = wishService.getWishById(wishId);

        if (wishListOptional.isPresent() && wishOptional.isPresent()) {
            wishListService.removeWishFromWishList(wishListOptional.get(), wishOptional.get());
            redirectAttributes.addFlashAttribute("success", "Wish removed from wishlist.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Wish or wishlist not found.");
        }

        return "redirect:/wishlists/" + wishlistId + "/wishes";
    }




}
