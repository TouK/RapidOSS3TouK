import com.ifountain.compass.DefaultCompassConfiguration
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.compass.core.config.CompassEnvironment

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
/**
* Grails Searchable Plugin configuration
*
* This file is created by "grails install-searchable-config"
*/
class SearchableConfiguration {
    public SearchableConfiguration()
    {
        if(System.getProperty("compass.transaction.lockTimeout") != null)
        {
            compassSettings.put("compass.transaction.lockTimeout", System.getProperty("compass.transaction.lockTimeout"));
        }
        compassSettings.put (CompassEnvironment.REGISTER_SHUTDOWN_HOOK, "false");
        compassSettings.put(org.compass.core.lucene.LuceneEnvironment.LockFactory.TYPE, org.compass.core.lucene.LuceneEnvironment.LockFactory.Type.SINGLE_INSTANCE);
    }
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
    Map compassSettings = DefaultCompassConfiguration.getDefaultSettings(ConfigurationHolder.getConfig());
             
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
