package com.roma42.eventifyBack.schedule;

import com.roma42.eventifyBack.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduleConfig {

    final static private Logger log = LoggerFactory.getLogger(ScheduleConfig.class);
    final private EventService eventService;

    @Autowired
    public ScheduleConfig(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(initialDelayString = "${scheduling.delay.initial}",
            fixedDelayString = "${scheduling.delay.fixed}",
            timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void scheduledPrint() {
        this.eventService.deleteEventByEventDateBeforeNow();
        log.info("Events before " + LocalDateTime.now() + " have been deleted");
    }
}