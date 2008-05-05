import com.ifountain.rcmdb.util.RapidCMDBConstants
import java.text.MessageFormat
import org.codehaus.groovy.grails.commons.GrailsApplication

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
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 */
class ExecuteBatchController {

    private static final String SUCCESS_MESSAGE_TEMPLATE = "{0} of {1} actions executed successfully.";

    public static final String ADD_OBJECT = "AddObject";
    public static final String UPDATE_OBJECT = "UpdateObject";
    public static final String REMOVE_OBJECT = "RemoveObject";
    public static final String ADD_RELATION = "AddRelation";
    public static final String REMOVE_RELATION = "RemoveRelation";

    def index = {
        try {
            def numberOfSuccessfulActions = 0;
            def numberOfAllActions = 0;
            def errors = [];
            def data = getMandatoryParam(RapidCMDBConstants.DATA_PARAMETER, params[RapidCMDBConstants.DATA_PARAMETER]);
            def rows = new XmlSlurper().parseText(data).Action;
            numberOfAllActions = rows.size();
            def count = 0;
            for (row in rows) {
                count++;
                try {
                    def actionType = getMandatoryParam(RapidCMDBConstants.ACTION_TYPE, row."${RapidCMDBConstants.ACTION_TYPE}".text());
                    def model = getMandatoryParam(RapidCMDBConstants.MODEL, row."${RapidCMDBConstants.MODEL}".@Name.toString());
                    def modelClass = grailsApplication.getClassForName(model);
                    if (!modelClass) {
                        throw new Exception(message(code: "model.doesnot.exist", args: [model]))
                    }
                    if (actionType == ADD_OBJECT) {
                        def addParams = [:];
                        row.Model.children().each {
                            addParams.put(it.name(), it.text());
                        }
                        log.debug(getLogPrefix() + "Adding " + model + " with properties: " + addParams);
                        def object = modelClass.metaClass.invokeStaticMethod(modelClass, "add", [addParams] as Object[]);
                        if (object.hasErrors()) {
                            throw new Exception(getObjectErrors(object));
                        }
                        numberOfSuccessfulActions++;
                        log.debug(getLogPrefix() + "Object added successfully.");
                    }
                    else if (actionType == REMOVE_OBJECT) {
                        def keys = [:];
                        row."${RapidCMDBConstants.MODEL}".children().each {
                            keys.put(it.name(), it.text());
                        }
                        log.debug(getLogPrefix() + "Removing " + model + " with keys: " + keys);
                        def object = modelClass.metaClass.invokeStaticMethod(modelClass, "get", [keys] as Object[]);
                        if (!object) {
                            throw new Exception(message(code: "model.object.doesnot.exist", args: [modelClass.getName(), keys.toString()]));
                        }
                        object.remove();
                        numberOfSuccessfulActions++;
                        log.debug(getLogPrefix() + "Object removed successfully.");
                    }
                    else if (actionType == ADD_RELATION || actionType == REMOVE_RELATION) {
                        def relatedModel = getMandatoryParam(RapidCMDBConstants.RELATED_MODEL, row."${RapidCMDBConstants.RELATED_MODEL}".@Name.toString());
                        def relationName = getMandatoryParam(RapidCMDBConstants.RELATION_NAME, row."${RapidCMDBConstants.RELATION_NAME}".text());
                        def relatedModelClass = grailsApplication.getClassForName(relatedModel);
                        if (!relatedModelClass) {
                            throw new Exception(message(code: "model.doesnot.exist", args: [relatedModel]))
                        }
                        def keys = [:];
                        row."${RapidCMDBConstants.MODEL}".children().each {
                            keys.put(it.name(), it.text());
                        }
                        def relatedObjectKeys = [:];
                        row."${RapidCMDBConstants.RELATED_MODEL}".children().each {
                            relatedObjectKeys.put(it.name(), it.text());
                        }

                        def object = modelClass.metaClass.invokeStaticMethod(modelClass, "get", [keys] as Object[]);
                        if (!object) {
                            throw new Exception(message(code: "model.object.doesnot.exist", args: [modelClass.getName(), keys.toString()]));
                        }
                        def relatedObject = relatedModelClass.metaClass.invokeStaticMethod(relatedModelClass, "get", [relatedObjectKeys] as Object[]);
                        if (!relatedObject) {
                            throw new Exception(message(code: "model.object.doesnot.exist", args: [relatedModelClass.getName(), relatedObjectKeys.toString()]));
                        }
                        if (actionType == ADD_RELATION) {
                            log.debug(getLogPrefix() + "Adding relation " + relationName + " between objects " + object + " and  " + relatedObject);

                            def relationMap = [:];
                            relationMap[relationName] = relatedObject;
                            object.addRelation(relationMap);
                            if (object.hasErrors()) {
                                throw new Exception(getObjectErrors(object));
                            }
                            numberOfSuccessfulActions++;
                            log.debug(getLogPrefix() + "Relation added successfully.");
                        }
                        else {
                            log.debug(getLogPrefix() + "Removing relation " + relationName + " between objects " + object + " and  " + relatedObject);

                            def relationMap = [:];
                            relationMap[relationName] = relatedObject;
                            object.removeRelation(relationMap);
                            if (object.hasErrors()) {
                                throw new Exception(getObjectErrors(object));
                            }
                            numberOfSuccessfulActions++;
                            log.debug(getLogPrefix() + "Relation removed successfully.");
                        }
                    }
                    else if(actionType == UPDATE_OBJECT){
                        def keys = [:];
                        row."${RapidCMDBConstants.MODEL}".Keys.children().each {
                            keys.put(it.name(), it.text());
                        }
                        log.debug(getLogPrefix() + "Updating " + model + " with keys: " + keys);
                        def object = modelClass.metaClass.invokeStaticMethod(modelClass, "get", [keys] as Object[]);
                        if (!object) {
                            throw new Exception(message(code: "model.object.doesnot.exist", args: [modelClass.getName(), keys.toString()]));
                        }
                        def updateParams = [:];
                        row."${RapidCMDBConstants.MODEL}".children().each {
                            if(it.name() != "Keys"){
                              updateParams.put(it.name(), it.text());  
                            }

                        }
                        object.update(updateParams);
                        if (object.hasErrors()) {
                            throw new Exception(getObjectErrors(object));
                        }
                        numberOfSuccessfulActions++;
                        log.debug(getLogPrefix() + "Object updated successfully.");
                    }
                }
                catch (e) {
                    errors.add("Exception occured in action " + count + ": " + e.getMessage());
                }

            }
            if (errors.size() > 0) {
                renderErrors(errors)
            }
            else {
                render(contentType: "text/xml") {
                    Successful(getSuccessMessage(numberOfAllActions, numberOfSuccessfulActions));
                }
            }

        }
        catch (e) {
            renderErrors([e.getMessage()]);
        }
    }

    def getObjectErrors(object) {
        def errorMessage = "";
        object.errors.allErrors.each {
            errorMessage += message(error: it);
        };
        return errorMessage;
    }

    public static String getSuccessMessage(allCount, successCount)
    {
        return MessageFormat.format(SUCCESS_MESSAGE_TEMPLATE, [String.valueOf(successCount), String.valueOf(allCount)] as Object[]);
    }

    def getMandatoryParam(paramName, param) {
        if (!param || param.trim().length() < 1) {
            throw new Exception(message(code: "default.missing.mandatory.parameter", args: [paramName]));
        }
        return param;
    }

    def renderErrors(errorList) {
        render(contentType: 'text/xml') {
            Errors {
                for (error in errorList) {
                    Error(Message: error)
                }
            }
        };
    }

    def getLogPrefix() {
        return "[ExecuteBatch]: "
    }
}