package com.banking.auth.support.fake;

import com.banking.auth.application.port.out.ClockPort;

import java.time.Instant;

public class  FixedClock implements ClockPort {

    private final Instant fixedInstant;

    public FixedClock(Instant fixedInstant) {
        this.fixedInstant = fixedInstant;
    }

    @Override
    public Instant now() {
        return fixedInstant;
    }
}