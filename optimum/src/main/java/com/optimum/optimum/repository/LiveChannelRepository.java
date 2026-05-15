package com.optimum.optimum.repository;

import com.optimum.optimum.model.LiveChannel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveChannelRepository extends JpaRepository<LiveChannel, UUID> {
    List<LiveChannel> findByCountryIgnoreCase(String country);
}
