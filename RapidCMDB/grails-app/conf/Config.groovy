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
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text-plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"
grails.test.default.rollback = true

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
    appender.rapidServerLog = "org.apache.log4j.FileAppender"
    appender.'rapidServerLog.layout'="org.apache.log4j.PatternLayout"
    appender.'rapidServerLog.layout.ConversionPattern'='[%r] %c{2} %m%n'
    appender.'rapidServerLog.File'="logs/RapidServer.log"


    appender.errorLog = "org.apache.log4j.FileAppender"
    appender.'errorLog.layout'="org.apache.log4j.PatternLayout"
    appender.'errorLog.layout.ConversionPattern'='[%r] %c{2} %m%n'
    appender.'errorLog.File'="logs/RapidServerErr.log"
    rootLogger="error,rapidServerLog"
    logger {
        grails="warn,rapidServerLog"
        grailsa.pp="warn,rapidServerLog"
        StackTrace="error,errorLog"
        org {
            springframework="off"
            hibernate="off"
        }
    }
    additivity.StackTrace=false
    additivity.rapidServerLog=true
}


