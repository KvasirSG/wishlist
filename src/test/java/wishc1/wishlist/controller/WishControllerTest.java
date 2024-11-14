package wishc1.wishlist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wishc1.wishlist.model.Wish;
import wishc1.wishlist.service.WishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WishControllerTest {

    @Mock
    private WishService wishService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private WishController wishController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showNewWishForm_ShouldReturnAddWishView() {
        String viewName = wishController.showNewWishForm(model);

        assertEquals("add-wish", viewName);
        verify(model).addAttribute(eq("wish"), any(Wish.class));
    }

    @Test
    void addNewWish_ShouldRedirectToWishes_WhenWishIsSaved() {
        Wish wish = new Wish();
        String viewName = wishController.addNewWish(wish, redirectAttributes);

        assertEquals("redirect:/wishes", viewName);
        verify(wishService).saveWish(wish);
        verify(redirectAttributes).addFlashAttribute("success", "New wish added to available items.");
    }

    @Test
    void showAddWishForm_ShouldReturnAddWishView() {
        String viewName = wishController.showAddWishForm(model);

        assertEquals("add-wish", viewName);
        verify(model).addAttribute(eq("wish"), any(Wish.class));
    }

    @Test
    void addWishToReadyList_ShouldAddWishToReadyListAndRedirect() {
        Wish wish = new Wish();
        List<Wish> readyWishes = new ArrayList<>();

        String viewName = wishController.addWishToReadyList(wish, readyWishes, redirectAttributes);

        assertEquals("redirect:/wishes/ready", viewName);
        assertEquals(1, readyWishes.size());
        verify(redirectAttributes).addFlashAttribute("success", "Wish added to ready list.");
    }

    @Test
    void viewReadyWishes_ShouldReturnReadyWishesView() {
        List<Wish> readyWishes = new ArrayList<>();

        String viewName = wishController.viewReadyWishes(readyWishes, model);

        assertEquals("ready-wishes", viewName);
        verify(model).addAttribute("readyWishes", readyWishes);
    }

    @Test
    void finalizeReadyWishes_ShouldAddReadyWishesToDatabaseAndClearList() {
        List<Wish> readyWishes = new ArrayList<>();
        readyWishes.add(new Wish());

        String viewName = wishController.finalizeReadyWishes(readyWishes, redirectAttributes);

        assertEquals("redirect:/wishes", viewName);
        assertEquals(0, readyWishes.size());  // List should be cleared after saving
        verify(wishService).addWishes(readyWishes);
        verify(redirectAttributes).addFlashAttribute("success", "All ready wishes have been saved.");
    }

    @Test
    void listWishes_ShouldReturnListWishesView() {
        List<Wish> wishes = new ArrayList<>();
        when(wishService.getAllWishes()).thenReturn(wishes);

        String viewName = wishController.listWishes(model);

        assertEquals("list-wishes", viewName);
        verify(model).addAttribute("wishes", wishes);
    }

    @Test
    void showEditWishesPage_ShouldReturnEditWishView() {
        List<Wish> wishes = new ArrayList<>();
        when(wishService.getAllWishes()).thenReturn(wishes);

        String viewName = wishController.showEditWishesPage(model);

        assertEquals("edit-wish", viewName);
        verify(model).addAttribute("wishes", wishes);
    }

    @Test
    void updateWish_ShouldRedirectToWishesAfterUpdate() {
        Long wishId = 1L;
        Wish wishDetails = new Wish();

        String viewName = wishController.updateWish(wishId, wishDetails, redirectAttributes);

        assertEquals("redirect:/wishes", viewName);
        verify(wishService).updateWish(wishId, wishDetails);
        verify(redirectAttributes).addFlashAttribute("success", "Wish updated successfully.");
    }

    @Test
    void deleteSelectedWishes_ShouldRedirectToEditPageAfterDeletion() {
        List<Long> selectedWishes = List.of(1L, 2L);

        String viewName = wishController.deleteSelectedWishes(selectedWishes);

        assertEquals("redirect:/wishes/edit", viewName);
        verify(wishService, times(2)).deleteWish(anyLong());
    }

    @Test
    void updateSelectedWishes_ShouldRedirectToEditPageAfterUpdating() {
        List<Long> selectedWishes = List.of(1L, 2L);
        List<String> names = List.of("Wish 1", "Wish 2");
        List<String> descriptions = List.of("Description 1", "Description 2");

        when(wishService.getWishById(1L)).thenReturn(Optional.of(new Wish()));
        when(wishService.getWishById(2L)).thenReturn(Optional.of(new Wish()));

        String viewName = wishController.updateSelectedWishes(selectedWishes, names, descriptions);

        assertEquals("redirect:/wishes/edit", viewName);
        verify(wishService, times(2)).saveWish(any(Wish.class));
    }
}
