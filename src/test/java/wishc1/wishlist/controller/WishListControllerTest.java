package wishc1.wishlist.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wishc1.wishlist.model.AppUser;
import wishc1.wishlist.model.Wish;
import wishc1.wishlist.model.WishList;
import wishc1.wishlist.security.CustomUserDetails;
import wishc1.wishlist.service.AppUserService;
import wishc1.wishlist.service.WishListService;
import wishc1.wishlist.service.WishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WishListControllerTest {

    @Mock
    private WishListService wishListService;

    @Mock
    private AppUserService appUserService;

    @Mock
    private WishService wishService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpSession session;

    @InjectMocks
    private WishListController wishListController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(wishListController).build();
    }

    @Test
    @WithMockUser
    void showCreateWishListForm_ShouldReturnNewWishlistView() {
        String viewName = wishListController.showCreateWishListForm(model);

        assertEquals("new-wishlist", viewName);
        verify(model).addAttribute(eq("wishList"), any(WishList.class));
    }

    @Test
    @WithMockUser
    void createWishList_ShouldRedirectToProfile_WhenUserAuthenticated() {
        AppUser user = new AppUser();
        user.setId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        WishList wishList = new WishList();
        when(session.getAttribute("readyWishes")).thenReturn(new ArrayList<Wish>());
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        String viewName = wishListController.createWishList(wishList, auth, redirectAttributes, session);

        assertEquals("redirect:/profile", viewName);
        verify(wishListService).saveWishList(wishList);
        verify(session).removeAttribute("readyWishes");
        verify(redirectAttributes).addFlashAttribute("success", "Wishlist created successfully.");
    }

    @Test
    void addWishToWishList_ShouldRedirectToAddWish_WhenWishAndWishlistPresent() {
        Long wishlistId = 1L;
        Long wishId = 2L;
        WishList wishList = new WishList();
        Wish wish = new Wish();
        when(wishListService.getWishListById(wishlistId)).thenReturn(Optional.of(wishList));
        when(wishService.getWishById(wishId)).thenReturn(Optional.of(wish));

        String viewName = wishListController.addWishToWishList(wishlistId, wishId, redirectAttributes);

        assertEquals("redirect:/wishlists/" + wishlistId + "/addWish", viewName);
        verify(wishListService).addWishToWishList(wishList, wish);
        verify(redirectAttributes).addFlashAttribute("success", "Wish added to wishlist.");
    }

    @Test
    void addWishToWishList_ShouldRedirectToProfileWithError_WhenWishOrWishlistNotFound() {
        Long wishlistId = 1L;
        Long wishId = 2L;
        when(wishListService.getWishListById(wishlistId)).thenReturn(Optional.empty());

        String viewName = wishListController.addWishToWishList(wishlistId, wishId, redirectAttributes);

        assertEquals("redirect:/wishlists/" + wishlistId + "/addWish", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Wish or wishlist not found.");
    }

    @Test
    @WithMockUser
    void viewSharedWishList_ShouldReturnViewSharedWishlist_WhenUserAuthorized() {
        Long wishlistId = 1L;
        AppUser user = new AppUser();
        WishList wishList = new WishList();
        wishList.getViewers().add(user);
        when(appUserService.getLoggedInUser()).thenReturn(user);
        when(wishListService.getWishListById(wishlistId)).thenReturn(Optional.of(wishList));

        String viewName = wishListController.viewSharedWishList(wishlistId, model, mock(Authentication.class));

        assertEquals("view-shared-wishlist", viewName);
        verify(model).addAttribute("wishList", wishList);
    }

    @Test
    void removeWishFromWishList_ShouldRedirectToWishlistWishes_WhenWishAndWishlistFound() {
        Long wishlistId = 1L;
        Long wishId = 2L;
        WishList wishList = new WishList();
        Wish wish = new Wish();
        when(wishListService.getWishListById(wishlistId)).thenReturn(Optional.of(wishList));
        when(wishService.getWishById(wishId)).thenReturn(Optional.of(wish));

        String viewName = wishListController.removeWishFromWishList(wishlistId, wishId, redirectAttributes);

        assertEquals("redirect:/wishlists/" + wishlistId + "/wishes", viewName);
        verify(wishListService).removeWishFromWishList(wishList, wish);
        verify(redirectAttributes).addFlashAttribute("success", "Wish removed from wishlist.");
    }

    @Test
    void removeWishFromWishList_ShouldRedirectToWishlistWishesWithError_WhenWishOrWishlistNotFound() {
        Long wishlistId = 1L;
        Long wishId = 2L;
        when(wishListService.getWishListById(wishlistId)).thenReturn(Optional.empty());

        String viewName = wishListController.removeWishFromWishList(wishlistId, wishId, redirectAttributes);

        assertEquals("redirect:/wishlists/" + wishlistId + "/wishes", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Wish or wishlist not found.");
    }

    @Test
    void shareWishlists_ShouldRedirectToProfileWithError_WhenNoRecipientSelected() {
        List<Long> wishListIds = List.of(1L);
        List<String> recipientUsernames = new ArrayList<>();

        String viewName = wishListController.shareWishlists(wishListIds, recipientUsernames, redirectAttributes);

        assertEquals("redirect:/wishlists/profile", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Please select at least one recipient.");
    }

    @Test
    void shareWishlists_ShouldRedirectToProfileWithSuccess_WhenRecipientsAndWishlistsPresent() {
        List<Long> wishListIds = List.of(1L);
        List<String> recipientUsernames = List.of("user1", "user2");
        AppUser user1 = new AppUser();
        AppUser user2 = new AppUser();
        WishList wishList = new WishList();
        when(appUserService.getUsersByUsernames(recipientUsernames)).thenReturn(List.of(user1, user2));
        when(wishListService.getWishListsByIds(wishListIds)).thenReturn(List.of(wishList));

        String viewName = wishListController.shareWishlists(wishListIds, recipientUsernames, redirectAttributes);

        assertEquals("redirect:/wishlists/profile", viewName);
        verify(wishListService, times(2)).shareWishListWithUser(eq(wishList), any(AppUser.class));
        verify(redirectAttributes).addFlashAttribute("success", "Wishlist(s) shared successfully.");
    }
}
