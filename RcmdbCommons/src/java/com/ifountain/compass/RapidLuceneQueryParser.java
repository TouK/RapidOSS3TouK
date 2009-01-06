package com.ifountain.compass;

import org.compass.core.lucene.engine.queryparser.DefaultLuceneQueryParser;
import org.apache.lucene.queryParser.CompassQueryParser;
import org.apache.lucene.queryParser.CompassMultiFieldQueryParser;
import org.apache.lucene.analysis.Analyzer;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 6:03:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidLuceneQueryParser extends DefaultLuceneQueryParser
{
    protected CompassQueryParser createQueryParser(String property, Analyzer analyzer, boolean forceAnalyzer) {
        return new RapidQueryParser(property, analyzer, getMapping(), getSearchEngineFactory(), forceAnalyzer);
    }

    protected CompassMultiFieldQueryParser createMultiQueryParser(String[] properties, Analyzer analyzer, boolean forceAnalyzer) {
        return new RapidMultiQueryParser(properties, analyzer, getMapping(), getSearchEngineFactory(), forceAnalyzer);
    }
}
