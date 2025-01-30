package org.saysimple.event;

import lombok.Getter;

@Getter
public enum EventStatus {
    SUCCESS("SUCCESS"),
    FAIL("FAIL");

    private final String status;

    EventStatus(String status) {
        this.status = status;
    }
}
