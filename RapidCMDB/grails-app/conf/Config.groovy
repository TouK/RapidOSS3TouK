import com.ifountain.core.connection.DefaultTimeoutStrategy

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
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [ html: ['text/html','application/xhtml+xml','application/x-www-form-urlencoded','multipart/form-data'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text-plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      htc: 'text/x-component',
                      all: '*/*',
                      json: ['application/json','text/json']
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"
grails.test.default.rollback = true

domain.property.interceptorclass = "com.ifountain.rcmdb.domain.property.RapidCmdbDomainPropertyInterceptor";

plugin {
    excludes = "hibernate"
}
connection.pool.checker.interval = 10000;
connection.pool.timeout.strategy = DefaultTimeoutStrategy.name;
rapidCMDB.temp.dir = "${System.getProperty("base.dir")}/generatedModels".toString();
rapidCMDB.base.dir = System.getProperty("base.dir");
rapidcmdb.date.format = "yyyy-dd-MM HH:mm:ss.SSS";

//user authentication type local or ldap , any value other than ldap is evaluated as local
rapidCMDB.authentication.type="local"; 

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.yourexcitingapplication.com"
    }
}

// log4j configuration
log4j {
    // COPY AND MODIFY THE FOLLOWING SECTION TO CREATE A NEW LOG APPENDER
    appender.modelsLog = "org.apache.log4j.DailyRollingFileAppender"
    appender.'modelsLog.datePattern'="'.'yyyy-MM-dd"
    appender.'modelsLog.layout'="org.apache.log4j.PatternLayout"
    appender.'modelsLog.layout.ConversionPattern'='%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'modelsLog.File'="logs/Models.log"

    appender.rapidServerLog = "org.apache.log4j.DailyRollingFileAppender"
    appender.'rapidServerLog.datePattern'="'.'yyyy-MM-dd"
    appender.'rapidServerLog.layout'="org.apache.log4j.PatternLayout"
    appender.'rapidServerLog.layout.ConversionPattern'='%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'rapidServerLog.File'="logs/RapidServer.log"

    appender.errorLog = "org.apache.log4j.DailyRollingFileAppender"
    appender.'errorLog.datePattern'="'.'yyyy-MM-dd"
    appender.'errorLog.layout'="org.apache.log4j.PatternLayout"
    appender.'errorLog.layout.ConversionPattern'='%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'errorLog.File'="logs/RapidServerErr.log"

    appender.connectionLog = "org.apache.log4j.DailyRollingFileAppender"
    appender.'connectionLog.datePattern'="'.'yyyy-MM-dd"
    appender.'connectionLog.layout'="org.apache.log4j.PatternLayout"
    appender.'connectionLog.layout.ConversionPattern'='%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'connectionLog.File'="logs/Connection.log"
    rootLogger="warn,rapidServerLog"



    logger {
        // COPY THE FOLLOWING LINE TO DEFINE A NEW LOGGER THAT WILL USE THE NEW APPENDER CREATED ABOVE
        models="info,modelsLog"
        com.ifountain.core.connection="info,connectionLog"
        grails="warn,rapidServerLog"
        StackTrace="warn,errorLog"
        org {
            springframework="off"
            hibernate="off"
        }
    }
    additivity.StackTrace=false
    additivity.grails=false
    additivity.models=false
    additivity.'com.ifountain.core.connection'=false
}


