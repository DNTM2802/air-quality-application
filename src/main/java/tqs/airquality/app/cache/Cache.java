package tqs.airquality.app.cache;

import tqs.airquality.app.models.AirQuality;

import java.util.HashMap;
import java.util.Map;

public class CurrentDayCache<T> {
    private long ttl;
    private int requests;
    private int misses;
    private int hits;

    private Map<String, T> cachedRequests;
    private Map<String, Long> cachedRequestsTtl;

    public CurrentDayCache(long ttl) {
        this.ttl = ttl;
        this.requests = 0;
        this.misses = 0;
        this.hits = 0;
        this.cachedRequests = new HashMap<>();
        this.cachedRequestsTtl = new HashMap<>();
    }

    public void saveRequestToCache(String address, T obj) {
        this.cachedRequests.put(address, obj);
        this.cachedRequestsTtl.put(address, System.currentTimeMillis() + this.ttl * 1000);
    }

    public T getRequestFromCache(String identifier) {
        this.requests++;
        T cachedObj = null;
        if (!this.cachedRequestsTtl.containsKey(identifier)){
            this.misses++;
        } else if (System.currentTimeMillis() > this.cachedRequestsTtl.get(identifier)){
            this.misses++;
            this.cachedRequestsTtl.remove(identifier);
            this.cachedRequests.remove(identifier);
        } else {
            this.hits++;
            cachedObj = cachedRequests.get(identifier);
            this.cachedRequestsTtl.put(identifier, System.currentTimeMillis() + this.ttl * 1000);

        }
        return cachedObj;
    }

    public int getRequests() {
        return requests;
    }

    public int getMisses() {
        return misses;
    }

    public int getHits() {
        return hits;
    }
}
