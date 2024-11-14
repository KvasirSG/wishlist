package wishc1.wishlist.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CustomErrorController customErrorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleError_ShouldReturn403View_WhenStatusIs403() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(403);

        String viewName = customErrorController.handleError(request);

        assertEquals("error/403", viewName);
    }

    @Test
    void handleError_ShouldReturn404View_WhenStatusIs404() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);

        String viewName = customErrorController.handleError(request);

        assertEquals("error/404", viewName);
    }

    @Test
    void handleError_ShouldReturn500View_WhenStatusIs500() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);

        String viewName = customErrorController.handleError(request);

        assertEquals("error/500", viewName);
    }

    @Test
    void handleError_ShouldReturnDefaultErrorView_WhenStatusIsUnknown() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(400);

        String viewName = customErrorController.handleError(request);

        assertEquals("error/error", viewName);
    }

    @Test
    void handleError_ShouldReturnDefaultErrorView_WhenStatusIsNull() {
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);

        String viewName = customErrorController.handleError(request);

        assertEquals("error/error", viewName);
    }
}
