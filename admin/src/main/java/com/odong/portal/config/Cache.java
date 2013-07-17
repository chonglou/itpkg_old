package com.odong.portal.config;

import net.rubyeye.xmemcached.CommandFactory;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedSessionLocator;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-24
 * Time: 上午3:45
 */
@Configuration("config.cache")
public class Cache {
    @Bean(destroyMethod = "shutdown")
    MemcachedClient getClient() throws IOException {
        return getClientBuilder().build();
    }

    @Bean
    XMemcachedClientBuilder getClientBuilder() {
        XMemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);
        builder.setConnectionPoolSize(poolSize);
        builder.setCommandFactory(getCommandFactory());
        builder.setSessionLocator(getSessionLocator());
        builder.setTranscoder(getTranscoder());
        return builder;
    }

    @Bean
    Transcoder getTranscoder() {
        return new SerializingTranscoder();
    }

    @Bean
    MemcachedSessionLocator getSessionLocator() {
        return new KetamaMemcachedSessionLocator();
    }

    @Bean
    CommandFactory getCommandFactory() {
        return new BinaryCommandFactory();
    }

    @Value("${memcached.servers}")
    private String servers;
    @Value("${memcached.pool_size}")
    private int poolSize;

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
