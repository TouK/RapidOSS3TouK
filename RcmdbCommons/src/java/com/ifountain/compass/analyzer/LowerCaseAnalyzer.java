package com.ifountain.compass.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import java.io.Reader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 5, 2009
 * Time: 11:03:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class LowerCaseAnalyzer extends Analyzer {
    public LowerCaseAnalyzer() {
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new LowerCaseTokenizer(reader);
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
        if (tokenizer == null) {
            tokenizer = new LowerCaseTokenizer(reader);
            setPreviousTokenStream(tokenizer);
        } else tokenizer.reset(reader);
        return tokenizer;
    }
}
