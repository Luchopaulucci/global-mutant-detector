package org.example.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for enabling Spring Cache.
 * Uses simple in-memory cache to speed up repeated DNA analysis requests.
 * This cache works alongside the database-based deduplication in MutantService.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot will auto-configure a simple ConcurrentMapCacheManager
}
