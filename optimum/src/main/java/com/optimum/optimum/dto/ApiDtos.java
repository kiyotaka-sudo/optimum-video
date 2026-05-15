package com.optimum.optimum.dto;

import java.util.List;
import java.util.Map;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record ProblemDetails(String type, String title, int status, String detail, String traceId, Map<String, Object> extensions) {
    }

    public record PageResponse<T>(List<T> data, Map<String, Object> pagination) {
    }

    public record PlaybackSessionRequest(String contentId, String contentType, String profileId, String drmSystem,
                                         String videoQuality, String deviceId, Integer resumeFromSeconds) {
    }

    public record PlaybackSession(String sessionId, String streamUrl, String streamProtocol, int resumeFromSeconds,
                                  String sessionExpiresAt, Map<String, String> drm) {
    }

    public record ProgressUpdate(String contentId, String profileId, int positionSeconds, int durationSeconds, boolean completed) {
    }
}
