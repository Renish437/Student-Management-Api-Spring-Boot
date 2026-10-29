package com.renish.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Spring Data Redis 4 writes to the cache asynchronously by default, which also
     * applies to cache <b>clears</b> issued by {@code @CacheEvict(allEntries = true)}:
     * the eviction is only queued when the mutating method returns, so the very next
     * read can still be served the stale entry. For this application's read-through
     * caches every write has to be visible immediately, so writes are forced to run
     * synchronously.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
    public RedisCacheManagerBuilderCustomizer synchronousCacheWrites(
            RedisConnectionFactory connectionFactory
    ) {
        return builder -> builder.cacheWriter(
                RedisCacheWriter.create(connectionFactory, configurer -> configurer.immediateWrites(true))
        );
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                // your own classes
                .allowIfSubType("com.renish.")
                // JDK collections / maps / numbers / strings
                .allowIfSubType("java.util.")
                .allowIfSubType("java.lang.")
                .allowIfSubType("java.time.")          // LocalDateTime etc. if you have them
                // Spring Data Page support
                .allowIfSubType("org.springframework.data.domain.")
                .build();

        GenericJacksonJsonRedisSerializer serializer =
                GenericJacksonJsonRedisSerializer.builder()
                        .enableDefaultTyping(ptv)
                        .build();

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                )
                .disableCachingNullValues();
    }
}