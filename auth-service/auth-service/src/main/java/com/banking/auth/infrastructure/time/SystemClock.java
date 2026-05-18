package com.banking.auth.infrastructure.time;

import com.banking.auth.application.port.out.ClockPort;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemClock implements ClockPort {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
