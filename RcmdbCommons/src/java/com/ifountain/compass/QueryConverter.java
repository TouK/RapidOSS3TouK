package com.ifountain.compass;

import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.compass.core.CompassQueryBuilder;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 5, 2008
 * Time: 2:17:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryConverter
{
    public static Query convertQuery(Query query, QueryConverterStrategy strategy)
    {
       return  _convertQuery(query, strategy);
    }

    private static Query _convertQuery(Query query, QueryConverterStrategy strategy)
    {
        if(query instanceof BooleanQuery)
        {
            BooleanQuery tmpq = (BooleanQuery)query;
            BooleanClause []clauses = tmpq.getClauses();
            for(int i=0; i <clauses.length; i++)
            {
               Query clauseQuery =  _convertQuery(clauses[i].getQuery(), strategy);
                clauses[i].setQuery(clauseQuery);
            }
            return tmpq;
        }
        else if(query instanceof TermQuery)
        {
            TermQuery tmpq = (TermQuery)query;
            Term term = tmpq.getTerm();
            Term convertedTerm = strategy.getConvertedTerm(term);
            return new TermQuery(convertedTerm);
        }
        else if(query instanceof WildcardQuery)
        {
            MultiTermQuery tmpq = (MultiTermQuery)query;
            Term term = tmpq.getTerm();
            Term convertedTerm = strategy.getConvertedTerm(term);
            return new WildcardQuery(convertedTerm);
        }
        else if(query instanceof PrefixQuery)
        {
            PrefixQuery tmpq = (PrefixQuery)query;
            Term term = tmpq.getPrefix();
            Term convertedTerm = strategy.getConvertedTerm(term);
            return new PrefixQuery(convertedTerm);
        }
        else if(query instanceof FuzzyQuery)
        {
            FuzzyQuery tmpq = (FuzzyQuery)query;
            Term term = tmpq.getTerm();
            Term convertedTerm = strategy.getConvertedTerm(term);
            return new FuzzyQuery(convertedTerm, tmpq.getMinSimilarity(), tmpq.getPrefixLength());
        }
        else if(query instanceof ConstantScoreRangeQuery)
        {
            ConstantScoreRangeQuery tmpq = (ConstantScoreRangeQuery)query;
            Term upperTerm = strategy.getConvertedTerm(new Term(tmpq.getField(), tmpq.getUpperVal()));
            Term lowerTerm = strategy.getConvertedTerm(new Term(tmpq.getField(), tmpq.getLowerVal()));
            return new ConstantScoreRangeQuery(upperTerm.field(), lowerTerm.text(), upperTerm.text(), tmpq.includesLower(), tmpq.includesUpper());
        }
        return query;
    }
}
