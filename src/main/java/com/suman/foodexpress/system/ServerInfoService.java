package com.suman.foodexpress.system;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.suman.foodexpress.system.dto.ServerInfoResponse;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class ServerInfoService {

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;
    private final String applicationName;
    private final String version;

    public ServerInfoService(DataSource dataSource,
                             RedisConnectionFactory redisConnectionFactory,
                             @Value("${spring.application.name:foodExpress}") String applicationName,
                             @Value("${app.info.version:0.0.1-SNAPSHOT}") String version) {
        this.dataSource = dataSource;
        this.redisConnectionFactory = redisConnectionFactory;
        this.applicationName = applicationName;
        this.version = version;
    }

    public ServerInfoResponse collect() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        Runtime jvm = Runtime.getRuntime();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        long committed = jvm.totalMemory();
        long used = committed - jvm.freeMemory();

        return new ServerInfoResponse(
                applicationName,
                version,
                Instant.ofEpochMilli(runtime.getStartTime()),
                TimeUnit.MILLISECONDS.toSeconds(runtime.getUptime()),
                new ServerInfoResponse.Jvm(
                        System.getProperty("java.vm.name"),
                        System.getProperty("java.vm.vendor"),
                        System.getProperty("java.version"),
                        runtime.getUptime()),
                new ServerInfoResponse.SystemInfo(
                        osBean.getName(),
                        osBean.getVersion(),
                        osBean.getArch(),
                        osBean.getAvailableProcessors(),
                        osBean.getSystemLoadAverage()),
                new ServerInfoResponse.Memory(
                        jvm.maxMemory(),
                        committed,
                        used,
                        jvm.freeMemory()),
                fetchDatabase(),
                fetchRedis());
    }

    private ServerInfoResponse.Database fetchDatabase() {
        try {
            try (Connection conn = openConnection()) {
                if (!conn.isValid(2)) {
                    return new ServerInfoResponse.Database(STATUS_DOWN, null, null);
                }
                DatabaseMetaData meta = conn.getMetaData();
                return new ServerInfoResponse.Database(
                        STATUS_UP, meta.getDatabaseProductName(), meta.getDatabaseProductVersion());
            }
        } catch (SQLException ex) {
            return new ServerInfoResponse.Database(STATUS_DOWN, null, null);
        }
    }

    private Connection openConnection() throws SQLException {
        if (dataSource instanceof HikariDataSource hikari) {
            long originalTimeout = hikari.getConnectionTimeout();
            hikari.setConnectionTimeout(2_000);
            try {
                return hikari.getConnection();
            } finally {
                hikari.setConnectionTimeout(originalTimeout);
            }
        }
        return dataSource.getConnection();
    }

    private ServerInfoResponse.Redis fetchRedis() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            if (!"PONG".equalsIgnoreCase(connection.ping())) {
                return new ServerInfoResponse.Redis(STATUS_DOWN, null);
            }
            String redisVersion = connection.serverCommands().info().getProperty("redis_version");
            return new ServerInfoResponse.Redis(STATUS_UP, redisVersion);
        } catch (Exception ex) {
            return new ServerInfoResponse.Redis(STATUS_DOWN, null);
        }
    }
}