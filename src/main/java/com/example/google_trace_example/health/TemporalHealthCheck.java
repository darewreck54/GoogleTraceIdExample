package com.example.google_trace_example.health;

import com.codahale.metrics.health.HealthCheck;

public class TemporalHealthCheck extends HealthCheck {
    public TemporalHealthCheck() {}

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}