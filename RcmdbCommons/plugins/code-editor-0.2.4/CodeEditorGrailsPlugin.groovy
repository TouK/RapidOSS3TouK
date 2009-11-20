class CodeEditorGrailsPlugin {
    def version = "0.2.4"
    def dependsOn = [:]

    // TODO Fill in these fields
    def author = "enguzekli"
    def authorEmail = "mustafa.sener@gmail.com"
    def title = "CodeEditor a Web Based IDE For Grails Developers"
    def description = '''CodeEditor is a plugin which allows developers to edit any file on the serverside. Developers
can edit groovy, javascript, xml and any other file from their browsers. Code editor has code completion capabilities
for groovy files.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/CodeEditor+Plugin"

    def doWithSpring = {
        def configObjectClass = application.classLoader.loadClass("CodeEditorConfig");
        codeEditorConfiguration(configObjectClass)
        {
        }
    }

    def doWithApplicationContext = {applicationContext ->
        // TODO Implement post initialization spring config (optional)		
    }

    def doWithWebDescriptor = {xml ->
        // TODO Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = {ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def onChange = {event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = {event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
