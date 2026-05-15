package com.optimum.optimum.service;

import com.optimum.optimum.model.LiveChannel;
import com.optimum.optimum.repository.LiveChannelRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LiveChannelService {
    private final LiveChannelRepository channels;

    public LiveChannelService(LiveChannelRepository channels) {
        this.channels = channels;
    }

    public List<LiveChannel> list(String country) {
        if (country == null || country.isBlank()) {
            return channels.findAll();
        }
        return channels.findByCountryIgnoreCase(country);
    }
}
