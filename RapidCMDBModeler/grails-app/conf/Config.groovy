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
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text-plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.test.default.rollback = true

plugin {
    excludes = "hibernate"
}

rapidCMDB.temp.dir = "${System.getProperty("base.dir")}/../RapidCMDB/generatedModels".toString();
rapidCMDB.base.dir = "${System.getProperty("base.dir")}/../RapidCMDB".toString();
rapidcmdb.date.format = "yyyy-dd-MM HH:mm:ss";
rapidcmdb.invalid.names = [];
def invalidNameFile = new File("invalidNames.txt");
if (invalidNameFile.exists())
{
    def invalidNames = invalidNameFile.readLines();
    def trimmedInvalidNames = [];
    invalidNames.each {
        trimmedInvalidNames += it.trim();
    }
    rapidcmdb.invalid.names = trimmedInvalidNames;
}


rapidcmdb.property.validname = "[a-z]{2,}[_a-zA-Z0-9]*"
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
    appender.'modelsLog.datePattern'="'.'yyyy-MM-dd-HH-mm"
    appender.'modelsLog.layout' = "org.apache.log4j.PatternLayout"
    appender.'modelsLog.layout.ConversionPattern' = '%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'modelsLog.File' = "logs/Models.log"

    appender.rapidServerLog = "org.apache.log4j.DailyRollingFileAppender"
    appender.'rapidServerLog.datePattern'="'.'yyyy-MM-dd-HH-mm"
    appender.'rapidServerLog.layout' = "org.apache.log4j.PatternLayout"
    appender.'rapidServerLog.layout.ConversionPattern' = '%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'rapidServerLog.File' = "logs/RapidServer.log"

    appender.errorLog = "org.apache.log4j.DailyRollingFileAppender"
    appender.'errorLog.datePattern'="'.'yyyy-MM-dd-HH-mm"
    appender.'errorLog.layout' = "org.apache.log4j.PatternLayout"
    appender.'errorLog.layout.ConversionPattern' = '%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n'
    appender.'errorLog.File' = "logs/RapidServerErr.log"

    rootLogger = "warn,rapidServerLog"

    logger {
        // COPY THE FOLLOWING LINE TO DEFINE A NEW LOGGER THAT WILL USE THE NEW APPENDER CREATED ABOVE
        models = "info,modelsLog"

        grails = "warn,rapidServerLog"
        StackTrace = "warn,errorLog"
        org {
            springframework = "off"
            hibernate = "off"
        }
    }
    additivity.StackTrace = false
    additivity.grails = false
    additivity.models = false
}


