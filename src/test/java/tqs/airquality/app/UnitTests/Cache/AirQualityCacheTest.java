package tqs.airquality.app.UnitTests.Cache;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import tqs.airquality.app.cache.Cache;
import tqs.airquality.app.models.AirQuality;
import tqs.airquality.app.utils.CacheType;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AirQualityCacheTest {

    private static final Logger LOGGER = Logger.getLogger( AirQualityCacheTest.class.getName() );

    // Instance to test
    private Cache<AirQuality> currentDayCache;

    // Fixed parameters
    private final CountDownLatch waiter = new CountDownLatch(1);
    private static final String address = "testLocation";
    private static final AirQuality aq = Mockito.mock(AirQuality.class);

    @BeforeEach
    void init() {
        currentDayCache = new Cache<>(1, CacheType.CURRENT_DAY);
        assertEquals(currentDayCache.getHits(),0);
        assertEquals(currentDayCache.getMisses(),0);
        assertEquals(currentDayCache.getRequests(),0);
    }

    @Test
    void whenRequestExistsAndNotExpired_thenReturnRequestAndIncreaseHitsAndRequests() {
        currentDayCache.saveRequestToCache(address, aq);
        AirQuality aq_result = currentDayCache.getRequestFromCache(address);
        assertEquals(currentDayCache.getHits(), 1);
        assertEquals(currentDayCache.getMisses(), 0);
        assertEquals(currentDayCache.getRequests(), 1);
        assertEquals(aq_result,aq);
    }

    @Test
    void whenRequestExistsAndExpired_thenReturnRequestAndIncreaseMissesAndRequests() throws InterruptedException {
        currentDayCache.saveRequestToCache(address, aq);
        waiter.await(2, TimeUnit.SECONDS);
        AirQuality aq_result = currentDayCache.getRequestFromCache(address);
        assertEquals(currentDayCache.getHits(), 0);
        assertEquals(currentDayCache.getMisses(), 1);
        assertEquals(currentDayCache.getRequests(), 1);
        assertNull(aq_result);
    }

    @Test
    void whenRequestNotExists_thenReturnNullAndIncreaseMissesAndRequests() {
        AirQuality aq_result = currentDayCache.getRequestFromCache(address);
        assertEquals(currentDayCache.getHits(), 0);
        assertEquals(currentDayCache.getMisses(), 1);
        assertEquals(currentDayCache.getRequests(), 1);
        assertNull(aq_result);
    }
}
