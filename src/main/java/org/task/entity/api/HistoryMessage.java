package org.task.entity.api;

import lombok.Value;

import java.util.Calendar;

@Value
public class HistoryMessage {
    String name;
    String message;
    Calendar dateTime;
}
