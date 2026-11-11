package mephi.controller;

import mephi.dto.request.CreateReport;
import mephi.model.*;
import mephi.service.CategoryService;
import mephi.service.ReportService;
import mephi.service.StatService;
import mephi.service.UserService;
import mephi.util.ConsoleReader;
import mephi.view.StatView;
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
public class StatControllerTest {
    @Mock
    private StatService statService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;
    @Mock
    private StatView view;
    @Mock
    private ReportService reportService;

    @InjectMocks
    private StatController statController;

    @Test
    public void helpEmptyArgs() {
        statController.handleCommand(List.of());

        verify(view, times(1)).printHelp();
    }

    @Test
    public void helpCommand() {
        statController.handleCommand(List.of("help"));

        verify(view, times(1)).printHelp();
    }

    @Test
    public void unknownAction() {
        statController.handleCommand(List.of("unknown"));

        verify(view, times(1)).printUnknownAction();
    }

    @Test
    public void shortSuccess() {
        StatShort statShort = new StatShort(
                new Stat(new BigDecimal("300.00"), new BigDecimal("150.00")),
                new BigDecimal("450.00")
        );
        when(statService.getStatShort()).thenReturn(statShort);

        statController.handleCommand(List.of("short"));

        verify(statService, times(1)).getStatShort();
        verify(view, times(1)).printShort(statShort);
    }

    @Test
    public void shortActionCaseInsensitive() {
        StatShort statShort = new StatShort(
                new Stat(new BigDecimal("300.00"), new BigDecimal("150.00")),
                new BigDecimal("450.00")
        );
        when(statService.getStatShort()).thenReturn(statShort);

        statController.handleCommand(List.of("SHORT"));

        verify(view, times(1)).printShort(statShort);
    }

    @Test
    public void fullSuccess() {
        Wallet wallet = new Wallet(UUID.randomUUID());
        StatFull statFull = new StatFull(List.of(), new Stat(BigDecimal.ZERO, BigDecimal.ZERO));
        when(userService.getWallet()).thenReturn(wallet);
        when(statService.getStatFull()).thenReturn(statFull);

        statController.handleCommand(List.of("full"));

        verify(statService, times(1)).getStatFull();
        verify(view, times(1)).printFullStats(statFull, wallet.getBalance());
    }

    @Test
    public void byCategorySuccess() {
        Wallet wallet = new Wallet(UUID.randomUUID());
        Category category = new Category("Продукты", wallet.getId(), BigDecimal.valueOf(1000), false);
        List<Category> categories = List.of(category);
        StatFull statFull = new StatFull(List.of(), new Stat(BigDecimal.ZERO, BigDecimal.ZERO));

        when(userService.getWallet()).thenReturn(wallet);
        when(categoryService.getCategoriesByUserId(wallet.getUserId())).thenReturn(categories);
        when(statService.getStatFullByCategories(any(UUID[].class))).thenReturn(statFull);
        MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class);
        mockedReader.when(() -> ConsoleReader.readIns("Категории (ввод через пробел)", 1, 1))
                .thenReturn(new int[]{1});

        statController.handleCommand(List.of("category"));

        ArgumentCaptor<UUID[]> categoriesIdCaptor = ArgumentCaptor.forClass(UUID[].class);
        verify(statService, times(1)).getStatFullByCategories(categoriesIdCaptor.capture());

        UUID[] categoriesId = categoriesIdCaptor.getValue();
        assertEquals(1, categoriesId.length);
        assertEquals(category.getId(), categoriesId[0]);

        verify(view, times(1)).printCategoryList(categories);
        verify(view, times(1)).printFullStats(statFull, wallet.getBalance());
    }

    @Test
    public void reportSuccess() {
        StatFull statFull = new StatFull(List.of(), new Stat(BigDecimal.ZERO, BigDecimal.ZERO));
        when(statService.getStatFull()).thenReturn(statFull);
        MockedStatic<ConsoleReader> mockedReader = mockStatic(ConsoleReader.class);

        mockedReader.when(() -> ConsoleReader.readString("Ведите имя отчета:")).thenReturn("Мой отчет");

        statController.handleCommand(List.of("report"));

        ArgumentCaptor<CreateReport<List<StatFullByCategory>>> reportCaptor =
                ArgumentCaptor.forClass(CreateReport.class);
        verify(reportService, times(1)).byCategories(reportCaptor.capture());

        CreateReport<List<mephi.model.StatFullByCategory>> report = reportCaptor.getValue();
        assertEquals(statFull.stats(), report.data());
        assertEquals("Мой отчет", report.name());
    }
}
