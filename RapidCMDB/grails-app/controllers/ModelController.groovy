import groovy.text.SimpleTemplateEngine

class ModelController {

    def scaffold = Model;

    def generate = {
        def model = Model.findByName(params.id);
//        model.propertyMap.each
//        {
//            println it;
//
//        }
        def engine = new SimpleTemplateEngine();
        def t = engine.createTemplate(new File("./groovy-app/templates/DomainClassTemplate.txt"))
        def bindings = ["model":model,
                        "imports":"bunlar importlar"];
        new File("${model.name}.groovy").withWriter { w ->
            t.make(bindings).writeTo(w);
        }

    }
}
