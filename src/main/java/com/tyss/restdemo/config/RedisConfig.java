//package com.tyss.restdemo.config;
//
//import com.tyss.restdemo.dto.EmployeeResponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.time.Duration;
//
//@Configuration
//public class RedisConfig {
//
//    @Bean
//    public RedisCacheManager cacheManager(
//            RedisConnectionFactory redisConnectionFactory) {
//
//        JacksonJsonRedisSerializer<EmployeeResponse> serializer =
//                new JacksonJsonRedisSerializer<>(EmployeeResponse.class);
//
//        RedisCacheConfiguration configuration =
//                RedisCacheConfiguration.defaultCacheConfig()
//                        .entryTtl(Duration.ofMinutes(10))
//                        .serializeKeysWith(
//                                RedisSerializationContext.SerializationPair
//                                        .fromSerializer(
//                                                new StringRedisSerializer()
//                                        )
//                        )
//                        .serializeValuesWith(
//                                RedisSerializationContext.SerializationPair
//                                        .fromSerializer(serializer)
//                        );
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(configuration)
//                .build();
//    }
//}