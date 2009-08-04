package utils

import com.thoughtworks.selenium.Selenium
import junit.framework.Assert

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 14, 2009
* Time: 2:48:07 PM
* To change this template use File | Settings | File Templates.
*/
class CommonUiTestUtils {
    private static final SHOW_URL_ELEMENT = "/show/"
    private static final UI_FROM_LIST_PREFIX = "available";

    public static String getPageErrorMessage(Selenium selenium)
    {
        def errorMessage = "";
        if(selenium.isElementPresent("pageFlashErrors"))
        {
            errorMessage += selenium.getText("//div[@id='pageFlashErrors']/ul/li")
        }
        if(selenium.isElementPresent("pageBeanErrors"))
        {
            errorMessage += selenium.getText("//div[@id='pageBeanErrors']//ul/li")
        }
        return errorMessage;
    }
    public static String getPageMessage(Selenium selenium)
    {
        def message = "";
        if(selenium.isElementPresent("pageMessage"))
        {
            message += selenium.getText("pageMessage")
        }
        return message.trim();
    }

    public static void assertPageMessage(Selenium selenium, String expectedMessage)
    {
            Assert.assertEquals("Expected message cannot be retreived. Error Message :${CommonUiTestUtils.getPageErrorMessage(selenium)}".toString(), expectedMessage, getPageMessage(selenium))
    }
    public static String getIdFromlocation(String location)
    {
        def index = location.lastIndexOf(SHOW_URL_ELEMENT);
        if(index >=0 )
        {
            return location.substring(index+SHOW_URL_ELEMENT.length(), location.length())
        }
        else return -1;
    }

    public static deleteInstance(Selenium selenium, String showUrl)
    {
        selenium.openAndWait(showUrl);
        selenium.click("_action_Delete");
        Assert.assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad ("30000");
    }

    public static String getIdFromRepository(Selenium selenium, String model, String query)
    {

        def res = CommonUiTestUtils.search(selenium, model, query)
        if (res.size() == 1)
        {
            return res[0].id;
        }
        else
        {
            return  null
        }
    }

    public static void clearToSelectList(Selenium selenium, String elementId)
    {
        def removeButtonId = elementId.substring(0, elementId.indexOf("Select"))+"ListsRemove";
        selenium.getSelectOptions (elementId).each{labelName->
            if(labelName != "")
            {
                selenium.select(elementId, "label=${labelName}");
                selenium.click(removeButtonId);
            }
        }
    }

    public static void addToSelectList(Selenium selenium, String elementId, List labels)
    {
        def addButtonId = elementId.substring(elementId.indexOf(UI_FROM_LIST_PREFIX)+UI_FROM_LIST_PREFIX.length(), elementId.indexOf("Select"))+"ListsAdd";
        labels.each{
            selenium.select(elementId, "label=${it}");
            selenium.click(addButtonId);
        }
    }

    public static  setAccordingToTypes(Selenium selenium, Map nameValues)
    {
        nameValues.each{fieldName, fieldValue->
            def type = selenium.getEval ("this.browserbot.getCurrentWindow().document.getElementsByName('${fieldName}')[0].type");
            if(type == "text" || type=="password")
            {
                selenium.type (fieldName, fieldValue);
            }
            else if(type == "checkbox")
            {
                selenium.getEval ("this.browserbot.getCurrentWindow().document.getElementsByName('${fieldName}')[0].checked=${fieldValue}")
            }
            else if(type.indexOf("select") >= 0)
            {
                selenium.select (fieldName, "label=${fieldValue}");
            }
        }
    }

    public static  search(Selenium selenium, String className, String query)
    {
        selenium.openAndWait("/RapidSuite/search?searchIn=${className}&query=${query}");
        def text = selenium.getPageText();
        def res = new XmlParser().parseText(text);
        def results = [];
        res.Object.each{
            results.add(it.attributes());
        }
        return results;
    }
}