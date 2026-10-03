package com.tamim.calculator.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model representing a single calculation history entry.
 */
public class HistoryItem implements Serializable {

    private final long id;
    private final String expression;
    private final String result;
    private final long timestamp;

    public HistoryItem(String expression, String result) {
        this.timestamp = System.currentTimeMillis();
        this.id = this.timestamp;
        this.expression = expression;
        this.result = result;
    }

    public HistoryItem(long id, String expression, String result, long timestamp) {
        this.id = id;
        this.expression = expression;
        this.result = result;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
