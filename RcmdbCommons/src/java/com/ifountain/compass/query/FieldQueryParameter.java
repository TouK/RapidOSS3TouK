package com.ifountain.compass.query;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 17, 2009
 * Time: 11:36:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class FieldQueryParameter {
    String field;
    String queryText;

    public FieldQueryParameter(String field, String queryText) {
        this.field = field;
        this.queryText = queryText;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
