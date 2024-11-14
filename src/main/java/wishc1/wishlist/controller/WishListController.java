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
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to create a wishlist.");
            return "redirect:/login";
        }

        // Retrieve the authenticated user
        AppUser appUser = ((CustomUserDetails) authentication.getPrincipal()).getAppUser();
        wishList.setOwner(appUser);

        // Retrieve ready wishes from the session and set them on the wishList
        List<Wish> readyWishes = (List<Wish>) session.getAttribute("readyWishes");
        if (readyWishes != null) {
            wishList.setWishes(readyWishes);  // Add wishes to wishList
        }

        // Save the wishList with the associated wishes
        wishListService.saveWishList(wishList);

        // Clear session-ready wishes after saving
        session.removeAttribute("readyWishes");

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
    @GetMapping("/shared/{id}/view")
    public String viewSharedWishList(@PathVariable Long id, Model model, Authentication authentication) {
        AppUser currentUser = getCurrentUser();
        Optional<WishList> sharedWishList = wishListService.getWishListById(id);

        if (sharedWishList.isPresent() && sharedWishList.get().getViewers().contains(currentUser)) {
            model.addAttribute("wishList", sharedWishList.get());
            model.addAttribute("wishes", sharedWishList.get().getWishes());
            return "view-shared-wishlist";
        } else {
            model.addAttribute("error", "You do not have access to view this wishlist.");
            return "error";
        }
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
                                 @RequestParam(required = false) List<String> recipientUsernames,
                                 RedirectAttributes redirectAttributes) {
        if (recipientUsernames == null || recipientUsernames.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one recipient.");
            return "redirect:/wishlists/profile";
        }

        List<AppUser> recipients = appUserService.getUsersByUsernames(recipientUsernames);  // Get the list of users by email
        List<WishList> wishLists = wishListService.getWishListsByIds(wishListIds);

        for (WishList wishList : wishLists) {
            for (AppUser recipient : recipients) {
                wishListService.shareWishListWithUser(wishList, recipient);  // Share each wishlist with each user
            }
        }

        redirectAttributes.addFlashAttribute("success", "Wishlist(s) shared successfully.");
        return "redirect:/wishlists/profile";
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
        // Initialize "readyWishes" list in session if not already present
        if (session.getAttribute("readyWishes") == null) {
            session.setAttribute("readyWishes", new ArrayList<Wish>());
        }
        model.addAttribute("wishList", new WishList());
        model.addAttribute("availableWishes", wishService.getAllWishes());
        return "new-wishlist";
    }


    /**
     * Add a selected wish from the available list to the "ready" list in the session.
     */
    @PostMapping("/addReadyWish")
    public String addReadyWish(@RequestParam("wishId") Long wishId, HttpSession session) {
        // Retrieve the "readyWishes" list from session, initialize if null
        List<Wish> readyWishes = (List<Wish>) session.getAttribute("readyWishes");
        if (readyWishes == null) {
            readyWishes = new ArrayList<>();
            session.setAttribute("readyWishes", readyWishes);
        }

        // Fetch the selected wish and add it to "readyWishes"
        Wish selectedWish = wishService.getWishById(wishId).orElseThrow(() -> new IllegalArgumentException("Invalid wish ID"));
        readyWishes.add(new Wish(selectedWish.getName(), selectedWish.getDescription(), selectedWish.getAddedDate()));

        // Update session attribute
        session.setAttribute("readyWishes", readyWishes);
        return "redirect:/wishlists/new";
    }





    @GetMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser appUser = userDetails.getAppUser();

            // Fetch user's own wishlists
            model.addAttribute("appUser", appUser);
            model.addAttribute("wishLists", wishListService.getWishListsByOwner(appUser.getId()));

            // Fetch wishlists shared with the user
            List<WishList> sharedWishLists = wishListService.getWishListsSharedWithUser(appUser);
            model.addAttribute("sharedWishLists", sharedWishLists);

            model.addAttribute("users", appUserService.getAllUsers());

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
            model.addAttribute("wishes", wishList.get().getWishes()); // Ensure wishes are added here
            model.addAttribute("isOwner", isOwner);
        } else {
            model.addAttribute("error", "Wishlist not found.");
        }

        return "wishlist-wishes";
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

    @PostMapping("/{id}/delete")
    public String deleteWishlist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<WishList> wishList = wishListService.getWishListById(id);

        if (wishList.isPresent()) {
            if (wishList.get().getWishes().isEmpty()) {
                wishListService.deleteWishListById(id);
                redirectAttributes.addFlashAttribute("success", "Wishlist deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Wishlist has items. Remove items before deleting.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Wishlist not found.");
        }

        return "redirect:/wishlists/profile";
    }



}
