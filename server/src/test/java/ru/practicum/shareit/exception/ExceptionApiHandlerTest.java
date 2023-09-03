package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExceptionApiHandlerTest {
    @InjectMocks
    private ExceptionApiHandler exceptionApiHandler;

    @Test
    public void testNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("предмет не найден");
        Map<String, Integer> response = exceptionApiHandler.handleNotFound(notFoundException);
        assertEquals(response.get("предмет не найден"), 404);
    }

    @Test
    public void testPermissionException() {
        PermissionException permissionException = new PermissionException("доступ запрещён");
        Map<String, Integer> response = exceptionApiHandler.handlePermission(permissionException);
        assertEquals(response.get("доступ запрещён"), 403);
    }

    @Test
    public void testBadRequestException() {
        BadRequestException badRequestException = new BadRequestException("не верный запрос");
        Map<String, Integer> response = exceptionApiHandler.handleBadRequest(badRequestException);
        assertEquals(response.get("не верный запрос"), 400);
    }

    @Test
    public void testEmailBusyException() {
        EmailBusyException emailBusyException = new EmailBusyException("почта занята");
        Map<String, Integer> response = exceptionApiHandler.handleEmailBusy(emailBusyException);
        assertEquals(response.get("почта занята"), 409);
    }
}
