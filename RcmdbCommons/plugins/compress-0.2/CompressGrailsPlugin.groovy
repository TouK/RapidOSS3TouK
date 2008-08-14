import grails.util.GrailsUtil

class CompressGrailsPlugin {
    def version = 0.2
    def dependsOn = [:]

    def author = "Seymour Cakes"
    def authorEmail = "sey.mores(@)g_ma_il_com"
    def title = "Servlet filter to Compress content."
    def description = "Generic filter to GZip compress GSP, CSS, Javascript or any GZip supported files."

    def documentation = "http://grails.org/Compress+Plugin"

    def doWithWebDescriptor = { xml ->
        def contextParam = xml.'context-param'

        def compress =  application.config.compress ? (application.config.compress + application.config.compress[GrailsUtil.environment] ?: [:]) : null
            if(!compress || compress.enabled) {
            contextParam[contextParam.size() - 1] + {
                'filter' {
                    'filter-name'('CompressPlugin')
                    'filter-class'('com.planetj.servlet.filter.compression.CompressingFilter')
                    ["debug", "statsEnabled"].each { property ->
                        if(compress && compress[property]) {
                            'init-param' {
                                'param-name'(property)
                                'param-value'('true')
                            }
                        }
                    }
                    ["includePathPatterns", "excludePathPatterns", "includeContentTypes",
                     "excludeContentTypes", "includeUserAgentPatterns", "excludeUserAgentPatterns"].each { property ->
                        if(compress && compress[property] && !compress[property].isEmpty()) {
                            'init-param' {
                                'param-name'(property)
                                'param-value'(compress[property].join(","))
                            }
                        }
                    }
                    ["compressionThreshold", "javaUtilLogger", "jakartaCommonsLogger"].each { property ->
                        if(compress && compress[property]) {
                            'init-param' {
                                'param-name'(property)
                                'param-value'(compress[property].toString())
                            }
                        }
                    }
                }
            }

            def filter = xml.'filter'
            filter[filter.size() - 1] + {
                if(compress && compress.urlPatterns) {
                    compress.urlPatterns.each { pattern ->
                        'filter-mapping'{
                            'filter-name'('CompressPlugin')
                            'url-pattern'(pattern)
                        }
                    }
                } else {
                    'filter-mapping'{
                        'filter-name'('CompressPlugin')
                        'url-pattern'('/*')
                    }
                }
            }
        }
    }
}
