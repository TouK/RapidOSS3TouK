package com.ifountain.compass.analyzer;

import org.apache.lucene.analysis.WhitespaceTokenizer;

import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 24, 2008
 * Time: 11:39:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class WhiteSpaceLowerCaseTokenizer extends WhitespaceTokenizer {
    public WhiteSpaceLowerCaseTokenizer(Reader reader) {
        super(reader);
    }

    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }
}
