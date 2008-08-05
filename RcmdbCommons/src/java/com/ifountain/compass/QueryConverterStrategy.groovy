package com.ifountain.compass

import org.apache.lucene.index.Term

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 5, 2008
 * Time: 2:18:17 PM
 * To change this template use File | Settings | File Templates.
 */
interface QueryConverterStrategy {
    public Term getConvertedTerm(Term term);
}