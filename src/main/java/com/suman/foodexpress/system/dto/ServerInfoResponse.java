package com.suman.foodexpress.system.dto;

import java.time.Instant;

public record ServerInfoResponse(
        String applicationName,
        String version,
        Instant startedAt,
        long uptimeSeconds,
        Jvm jvm,
        SystemInfo system,
        Memory memory,
        Database database,
        Redis redis) {

    public record Jvm(String name, String vendor, String version, long uptimeMillis) {
    }

    public record SystemInfo(String os, String osVersion, String arch, int processors,
                             double systemLoadAverage) {
    }

    public record Memory(long maxBytes, long committedBytes, long usedBytes, long freeBytes) {
    }

    public record Database(String status, String product, String version) {
    }

    public record Redis(String status, String version) {
    }
}