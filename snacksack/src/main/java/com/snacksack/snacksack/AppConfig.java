package com.snacksack.snacksack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.menuclient.GreggsClient;
import com.snacksack.snacksack.menuclient.NandosClient;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Set;

@Configuration
@Slf4j
public class AppConfig {

    @Value("${env}")
    private String env;
    @Value("${redis.enabled}")
    private boolean redisEnabled;

    @Value("#{environment.REDIS_URL}")
    private String redisUrl;

    static final ObjectMapper objectMapper = new ObjectMapper();

    static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    static final Set<String> DEV_ENVS = Set.of("dev", "local-dev");

    @Bean
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Bean
    public SpoonsClient spoonsClient() {
        return new SpoonsClient(objectMapper, httpClient);
    }

    @Bean
    public NandosClient nandosClient() {
        return new NandosClient(objectMapper, httpClient);
    }

    @Bean
    public GreggsClient greggsClient() {
        return new GreggsClient(objectMapper, httpClient);
    }

    @Bean
    // Based on Heroku guide here: https://devcenter.heroku.com/articles/connecting-heroku-redis
    public JedisPool jedisPool() {
        if (!redisEnabled) {
            return new JedisPool();
        }

        if (DEV_ENVS.contains(this.env)) {
            log.info("Connecting to Redis on URL: {}", redisUrl);

        }

        try {
            final TrustManager noopTrustManager = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{noopTrustManager}, new java.security.SecureRandom());

            final HostnameVerifier noopHostNameVerifier = (hostname, session) -> true;

            final JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);

            return new JedisPool(poolConfig,
                    URI.create(redisUrl),
                    sslContext.getSocketFactory(),
                    sslContext.getDefaultSSLParameters(),
                    noopHostNameVerifier);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Failed to obtain Redis connection");
            throw new RuntimeException("Cannot obtain Redis connection!", e);
        }
    }
}