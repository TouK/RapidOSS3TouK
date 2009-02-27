/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Feb 25, 2009
 * Time: 5:25:25 PM
 */

import ui.designer.*;

def configuration = new File("grails-app/conf/uiconfiguration.xml").getText();
def controller = new UiDesignerController();
controller.params["configuration"] = configuration;
controller.save();
