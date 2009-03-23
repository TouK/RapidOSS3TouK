import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.queryParser.FastCharStream
import org.codehaus.groovy.grails.plugins.searchable.lucene.LuceneUtils
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.lucene.analysis.Token
import org.apache.lucene.search.TermQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.Query
import com.ifountain.compass.analyzer.WhiteSpaceLowerCaseAnalyzer
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BooleanClause
import org.compass.core.impl.DefaultCompassQueryBuilder
import org.apache.lucene.search.RangeQuery

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Pinar Kinikoglu
 * Date: Apr 2, 2008
 * Time: 9:16:40 AM
 * To change this template use File | Settings | File Templates.
 */

QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
Query q = parser.parse("this:[aaaa TO assad] is a query")
if(q instanceof BooleanQuery)
{
    def clauses = ((BooleanQuery)q).getClauses();
    def bq = new RangeQuery()
    clauses.each{BooleanClause clause->
        println clause.getQuery().class.name;

    }
}
//println q.class.name
//Set s = new HashSet();
//q.extractTerms (s)
//s.each{
//    println it.text;
//    println it.class.name
//}
//return s;
