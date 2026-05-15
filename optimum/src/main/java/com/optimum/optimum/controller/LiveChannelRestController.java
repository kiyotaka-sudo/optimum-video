package com.optimum.optimum.controller;

import com.optimum.optimum.model.LiveChannel;
import com.optimum.optimum.service.LiveChannelService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
public class LiveChannelRestController {
    private final LiveChannelService liveChannelService;

    public LiveChannelRestController(LiveChannelService liveChannelService) {
        this.liveChannelService = liveChannelService;
    }

    @GetMapping
    Map<String, List<LiveChannel>> channels(@RequestParam(name = "country", required = false) String country) {
        return Map.of("channels", liveChannelService.list(country));
    }
}
