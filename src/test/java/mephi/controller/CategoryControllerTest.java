package mephi.controller;

import mephi.dto.request.CreateCategory;
import mephi.model.Category;
import mephi.model.User;
import mephi.service.CategoryService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.CategoryView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;
    @Mock
    private CategoryView view;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    public void helpEmptyArgs() {
        categoryController.handleCommand(List.of());

        verify(view, times(1)).printHelp();
    }

    @Test
    public void helpCommand() {
        categoryController.handleCommand(List.of("help"));

        verify(view, times(1)).printHelp();
    }

    @Test
    public void unknownAction() {
        categoryController.handleCommand(List.of("unknown"));

        verify(view, times(1)).printUnknownAction();
    }

    @Test
    public void createSuccess() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "testUser", "hashedPassword");
        when(userService.getCurrentUser()).thenReturn(user);
        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readString("Название")).thenReturn("Продукты");
            mockedReader.when(() -> ConsoleReader.readBoolean("Режим только пополнения?")).thenReturn(false);
            mockedReader.when(() -> ConsoleReader.readInt("Бюджет")).thenReturn(1000);

            categoryController.handleCommand(List.of("create"));

            ArgumentCaptor<CreateCategory> dtoCaptor = ArgumentCaptor.forClass(CreateCategory.class);
            verify(categoryService, times(1)).create(dtoCaptor.capture());

            CreateCategory dto = dtoCaptor.getValue();
            assertEquals("Продукты", dto.name());
            assertEquals(userId, dto.userId());
            assertEquals(BigDecimal.valueOf(1000), dto.budget());
            assertFalse(dto.isDepositOnly());

            verify(view, times(1)).createSuccess("Продукты");
        }
    }

    @Test
    public void createDepositOnlySuccess() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "testUser", "hashedPassword");
        when(userService.getCurrentUser()).thenReturn(user);

        try (MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class)) {
            mockedReader.when(() -> ConsoleReader.readString("Название")).thenReturn("Зарплата");
            mockedReader.when(() -> ConsoleReader.readBoolean("Режим только пополнения?")).thenReturn(true);

            categoryController.handleCommand(List.of("create"));

            ArgumentCaptor<CreateCategory> dtoCaptor = ArgumentCaptor.forClass(CreateCategory.class);
            verify(categoryService, times(1)).create(dtoCaptor.capture());

            CreateCategory dto = dtoCaptor.getValue();
            assertEquals("Зарплата", dto.name());
            assertEquals(BigDecimal.ZERO, dto.budget());
            assertTrue(dto.isDepositOnly());

            mockedReader.verify(() -> ConsoleReader.readInt("Бюджет"), never());
            verify(view, times(1)).createSuccess("Зарплата");
        }
    }

    @Test
    public void listSuccess() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "testUser", "hashedPassword");
        List<Category> categories = List.of(new Category("Продукты", UUID.randomUUID(), BigDecimal.valueOf(1000), false), new Category("Подписки", UUID.randomUUID(), BigDecimal.valueOf(800), false));
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryService.getCategoriesByUserId(userId)).thenReturn(categories);

        categoryController.handleCommand(List.of("list"));

        verify(view, times(1)).printCategoriesListHeader();
        verify(view, times(1)).printCategoriesList(categories);
    }
}
