package com.stocks.service;

import com.stocks.model.Stock;
import com.stocks.repository.HistoricalPriceRepository;
import com.stocks.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private HistoricalPriceRepository historicalPriceRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void addStock_savesWhenSymbolIsUnique() {
        Stock input = new Stock(null, "AAPL", "Apple Inc.", "Technology", "NASDAQ");
        Stock saved = new Stock(1L, "AAPL", "Apple Inc.", "Technology", "NASDAQ");

        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.empty());
        when(stockRepository.save(input)).thenReturn(saved);

        Stock result = stockService.addStock(input);

        assertEquals(saved, result);
        verify(stockRepository).findBySymbol("AAPL");
        verify(stockRepository).save(input);
    }

    @Test
    void addStock_throwsWhenSymbolAlreadyExists() {
        Stock input = new Stock(null, "AAPL", "Apple Inc.", "Technology", "NASDAQ");
        Stock existing = new Stock(1L, "AAPL", "Apple Inc.", "Technology", "NASDAQ");

        when(stockRepository.findBySymbol("AAPL")).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> stockService.addStock(input));

        assertEquals("Duplicate symbol: AAPL", ex.getMessage());
        verify(stockRepository).findBySymbol("AAPL");
        verifyNoInteractions(historicalPriceRepository);
    }
}

