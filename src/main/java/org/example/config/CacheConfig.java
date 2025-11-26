package org.example.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion para la chache de Spring.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot will auto-configure a simple ConcurrentMapCacheManager
}
