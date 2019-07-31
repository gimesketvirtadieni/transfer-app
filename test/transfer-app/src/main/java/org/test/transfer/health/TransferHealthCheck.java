package org.test.transfer.health;

import com.codahale.metrics.health.HealthCheck;

public class TransferHealthCheck extends HealthCheck {

    @Override
    protected Result check() {
        return Result.healthy();
    }
}
