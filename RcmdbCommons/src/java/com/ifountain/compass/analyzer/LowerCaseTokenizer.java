package com.ifountain.compass.analyzer;

import org.apache.lucene.analysis.CharTokenizer;

import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 5, 2009
 * Time: 11:04:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class LowerCaseTokenizer extends CharTokenizer{
    public LowerCaseTokenizer(Reader reader) {
        super(reader);
    }

    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }

    protected boolean isTokenChar(char c) {
        return true;
    }
}
