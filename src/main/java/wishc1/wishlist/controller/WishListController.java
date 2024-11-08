package wishc1.wishlist.controller;

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
        return "create-wishlist";
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
        return "redirect:/wishlists";
    }

    /**
     * Display form to add a wish to a specific wishlist.
     *
     * @param id the ID of the wishlist
     * @param model the model to hold the wishlist and wishes
     * @return the add-wish-to-wishlist page, or redirect to main wishlists page if wishlist not found
     */
    @GetMapping("/{id}/addWish")
    public String showAddWishForm(@PathVariable Long id, Model model) {
        Optional<WishList> wishList = wishListService.getWishListById(id);
        if (wishList.isPresent()) {
            model.addAttribute("wishList", wishList.get());
            model.addAttribute("wishes", wishService.getAllWishes());
            return "add-wish-to-wishlist";
        } else {
            return "redirect:/wishlists";
        }
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

        return "redirect:/wishlists";
    }

    /**
     * Display the user's own wishlists.
     *
     * @param model the model to hold the user's wishlists
     * @return the user-wishlists page
     */
    @GetMapping
    public String viewUserWishLists(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser currentUser = userDetails.getAppUser();
            // Initialize wishlists to avoid LazyInitializationException
            List<WishList> wishLists = wishListService.getWishListsByOwner(currentUser.getId());
            model.addAttribute("wishLists", wishLists);
            return "user-wishlists";
        }
        return "redirect:/login";
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
        return "shared-wishlists";
    }

    /**
     * Display the form to share a wishlist with another user.
     *
     * @param id the ID of the wishlist to be shared
     * @param model the model to hold the wishlist and list of users
     * @return the share-wishlist page, or redirect to main wishlists page if wishlist not found
     */
    @GetMapping("/{id}/share")
    public String showShareWishListForm(@PathVariable Long id, Model model) {
        Optional<WishList> wishList = wishListService.getWishListById(id);
        if (wishList.isPresent()) {
            model.addAttribute("wishList", wishList.get());
            model.addAttribute("users", appUserService.getAllUsers()); // Fetch all users to share with
            return "share-wishlist";
        }
        return "redirect:/wishlists";
    }

    /**
     * Share a wishlist with a user by their email.
     *
     * @param id the ID of the wishlist to be shared
     * @param email the email of the user to share the wishlist with
     * @param redirectAttributes the redirect attributes for success/error messages
     * @return redirect to the main wishlists page
     */
    @PostMapping("/{id}/share")
    public String shareWishList(@PathVariable Long id, @RequestParam String email, RedirectAttributes redirectAttributes) {
        Optional<WishList> wishList = wishListService.getWishListById(id);
        Optional<AppUser> user = appUserService.findByEmail(email);

        if (wishList.isPresent() && user.isPresent()) {
            wishListService.shareWishListWithUser(wishList.get(), user.get());
            redirectAttributes.addFlashAttribute("success", "Wishlist shared successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Wishlist or user not found.");
        }

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
}
