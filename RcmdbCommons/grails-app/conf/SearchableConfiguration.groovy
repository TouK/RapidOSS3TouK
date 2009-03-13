/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
import com.ifountain.rcmdb.domain.converter.CompassDateConverter
import com.ifountain.rcmdb.domain.converter.CompassDoubleConverter
import com.ifountain.rcmdb.domain.converter.CompassLongConverter
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.compass.analyzer.WhiteSpaceLowerCaseAnalyzer
import com.ifountain.rcmdb.domain.converter.CompassBooleanConverter
import com.ifountain.compass.index.WrapperIndexDeletionPolicy
import com.ifountain.compass.RapidQueryParser
import com.ifountain.compass.RapidLuceneQueryParser
import com.ifountain.rcmdb.domain.converter.CompassStringConverter

/**
* Grails Searchable Plugin configuration
*
* This file is created by "grails install-searchable-config"
*/
class SearchableConfiguration {

    /**
     * The location of the Compass index
     *
     * Examples: "/home/app/compassindex", "ram://app-index" or null to use the default
     */
    String compassConnection = System.getProperty("index.dir") != null?System.getProperty("index.dir"):new StringBuffer(System.getProperty("base.dir")).
                append(File.separator).
                append("data").
                toString();
    int mirrorBufferUpperLimit = 64;
    int mirrorBufferLowerLimit = 32;
    /**
     * Any settings you wish to pass to Compass
     *
     * Use this to configure custom/override default analyzers, query parsers, eg
     *
     *     Map compassSettings = [
     *         'compass.engine.analyzer.german.type': 'German'
     *     ]
     *
     * gives you an analyzer called "german" you can then use in mappings and queries, like
     *
     *    class Book {
     *        static searchable = { content analyzer: 'german' }
     *        String content
     *    }
     *
     *    Book.search("unter", analyzer: 'german')
     *
     * Documentation for Compass settings is here: http://www.compass-project.org/docs/1.2.1/reference/html/core-settings.html
     */
    def dateFormat = ConfigurationHolder.getConfig().toProperties()["rapidcmdb.date.format"];
    Map compassSettings = ["compass.converter.date.type":CompassDateConverter.class.name,
    "compass.converter.date.format":"${dateFormat}||yyyy-dd-MM||yyyy-dd-MM HH||yyyy-dd-MM HH:mm||yyyy-dd-MM HH:mm:ss||yyyy-dd-MM HH:mm:ss.SSS||MM-dd-yyyy||MM-dd-yyyy HH||MM-dd-yyyy HH:mm||MM-dd-yyyy HH:mm:ss||MM-dd-yyyy HH:mm:ss.SSS".toString(),
    "compass.converter.long.type":CompassLongConverter.class.name,
    "compass.converter.string.type":CompassStringConverter.class.name,
    "compass.converter.long.format":"#000000000000000000000000000000",
    "compass.converter.boolean.type":CompassBooleanConverter.class.name,
    "compass.converter.double.type":CompassDoubleConverter.class.name,
    "compass.converter.double.format":"#000000000000000000000000000000.00000000000000",
    "compass.engine.analyzer.default.type": WhiteSpaceLowerCaseAnalyzer.class.name,
    "compass.engine.store.wrapper.wrapper1.type":"com.ifountain.compass.CompositeDirectoryWrapperProvider",
    "compass.engine.store.wrapper.wrapper1.awaitTermination":"10000000",
    "compass.cache.first":"org.compass.core.cache.first.NullFirstLevelCache",
    "compass.transaction.lockTimeout":45,
    "compass.engine.store.indexDeletionPolicy.type":WrapperIndexDeletionPolicy.name,
    "compass.engine.queryParser.default.type":RapidLuceneQueryParser.class.name
    ];

    /**
     * Default mapping property exclusions
     *
     * No properties matching the given names will be mapped by default
     * ie, when using "searchable = true"
     *
     * This does not apply for classes using "searchable = [only/except: [...]]"
     * mapping by closure
     */
    List defaultExcludedProperties = []

    /**
     * Default property formats
     *
     * Value is a Map between Class and format string, eg
     *
     *     [(Date): "yyyy-MM-dd'T'HH:mm:ss"]
     *
     * Only applies to class properties mapped as "searchable properties", which are typically
     * simple class types that can be represented as Strings (rather than references
     * or components) AND only required if overriding the built-in format.
     */
    Map defaultFormats

    /**
     * Default search method options
     *
     * These can be overriden on a per-query basis by passing the search method a Map of options
     * containing those you want to override
     *
     * @param reload          whether to reload domain class instances from the DB: true|false
     *                        If true, the search  will be slower but objects will be associated
     *                        with the current Hibernate session
     * @param escape          whether to escape special characters in string queries: true|false
     * @param offset          the 0-based hit offset of the first page of results.
     *                        Normally you wouldn't change it from 0, it's only here because paging
     *                        works by using an offset + max combo for a specific page
     * @param max             the page size, for paged search results
     * @param defaultOperator if the query does not otherwise indicate, then the default operator
     *                        applied: "or" or "and".
     *                        If "and" means all terms are required for a match, if "or" means
     *                        any term is required for a match
     */
    Map defaultSearchOptions = [reload: false, escape: false, offset: 0, max: 10, defaultOperator: "and"]

    /**
     * Should changes made through GORM/Hibernate be mirrored to the index
     * automatically (using Compass::GPS)?
     *
     * If false, you must manage the index manually using index/indexAll/unindex/unindexAll/reindex/renindexAll
     */
    boolean mirrorChanges = false

    /**
     * Should the database be indexed at startup (using Compass:GPS)?
     *
     * Possible values: true|false|"fork"
     *
     * The value may be a boolean true|false or a string "fork", which means true,
     * and fork a thread for it
     *
     * If you use BootStrap.groovy to insert your data then you should use "true",
     * which means do a non-forking, otherwise "fork" is recommended
     */
    def bulkIndexOnStartup = false
}
