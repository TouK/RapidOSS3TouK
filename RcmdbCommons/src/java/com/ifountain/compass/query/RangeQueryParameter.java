package com.ifountain.compass.query;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 17, 2009
 * Time: 11:34:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class RangeQueryParameter {
    String start;
    String end;
    boolean isInclusive;
    String field;

    public RangeQueryParameter(String field, String start, String end, boolean inclusive) {
        this.field = field;
        this.start = start;
        this.end = end;
        isInclusive = inclusive;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isInclusive() {
        return isInclusive;
    }

    public void setInclusive(boolean inclusive) {
        isInclusive = inclusive;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
