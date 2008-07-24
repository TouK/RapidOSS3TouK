package com.ifountain.compass.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import java.io.Reader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 24, 2008
 * Time: 11:35:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class WhiteSpaceLowerCaseAnalyzer extends Analyzer {
    public WhiteSpaceLowerCaseAnalyzer() {
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new WhiteSpaceLowerCaseTokenizer(reader);
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
        if (tokenizer == null) {
            tokenizer = new WhiteSpaceLowerCaseTokenizer(reader);
            setPreviousTokenStream(tokenizer);
        } else tokenizer.reset(reader);
        return tokenizer;
    }
}

