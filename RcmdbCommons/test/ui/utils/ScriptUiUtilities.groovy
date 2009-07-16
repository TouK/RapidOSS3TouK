package utils

import com.thoughtworks.selenium.Selenium
import junit.framework.Assert

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 14, 2009
* Time: 1:53:13 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptUiUtilities {
    public static createOnDemandScript(Selenium selenium, String scriptName, Map otherParams = [:], List allowedGroups = [], boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/script/list")
        selenium.clickAndWait("link=Scripts");
        selenium.clickAndWait("link=New Script");
        selenium.type("name", scriptName);
        allowedGroups.each{groupName->
            selenium.addSelection("availableallowedGroupsSelect", "label=${groupName}");
            selenium.click("allowedGroupsListsAdd");
        }
        CommonUiTestUtils.setAccordingToTypes (selenium, otherParams)
        selenium.clickAndWait("//input[@value='Create']");
        def scriptId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "Script created")
        }
        return scriptId;
    }

    public static createPeriodicScript(Selenium selenium, String scriptName, Map otherParams,  boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/script/list")
        selenium.clickAndWait("link=Scripts");
        selenium.clickAndWait("link=New Script");
        selenium.type("name", scriptName);
        selenium.select("type", "label=Scheduled");
        selenium.select("scheduleType", "label=Periodic");
        CommonUiTestUtils.setAccordingToTypes (selenium, otherParams)
        selenium.clickAndWait("//input[@value='Create']");
        def scriptId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "Script created")
        }
        return scriptId;
    }

    public static createCronScript(Selenium selenium, String scriptName, Map otherParams, List allowedGroups = [], boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/script/list")
        selenium.clickAndWait("link=Scripts");
        selenium.clickAndWait("link=New Script");
        selenium.type("name", scriptName);
        selenium.select("type", "label=Scheduled");
        selenium.select("scheduleType", "label=Cron");
        CommonUiTestUtils.setAccordingToTypes (selenium, otherParams)
        selenium.clickAndWait("//input[@value='Create']");
        def scriptId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "Script created")
        }
        return scriptId;
    }

    public static updateScript(Selenium selenium, String scriptName, Map updatedParams, List allowedGroups = [], boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/script/list")
        selenium.clickAndWait("link=${scriptName}");
        selenium.clickAndWait("_action_Edit");
        CommonUiTestUtils.clearToSelectList(selenium, "allowedGroupsSelect");
        CommonUiTestUtils.addToSelectList(selenium, "availableallowedGroupsSelect", allowedGroups);
        CommonUiTestUtils.setAccordingToTypes (selenium, updatedParams)
        selenium.clickAndWait("_action_Update");
        def scriptId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "Script ${scriptId} updated")
        }
    }

    public static deleteScriptById(Selenium selenium, String scriptId, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/script/show/" + scriptId);
        Assert.assertTrue ("Script ${scriptId} does not exist".toString(), selenium.getLocation().indexOf("/script/show") >= 0);
        selenium.click("_action_Delete");
        Assert.assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad ("30000");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/script/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/script/list"));
            CommonUiTestUtils.assertPageMessage (selenium, "Script " + scriptId + " deleted")
        }
    }

    public static deleteScriptByName(Selenium selenium, String scriptName, boolean validate = true)
    {
        def res = CommonUiTestUtils.search(selenium, "script.CmdbScript", "name:${scriptName}")
        if (res.size() == 1)
        {
            deleteScriptById(selenium, res[0].id, validate)
        }
        else
        {
            if (validate)
            {
                Assert.fail("No scripts found with name ${scriptName}");
            }
        }
    }

    public static runScriptById(Selenium selenium, String scriptId,  Map params=[:], String timeout = "30000")
    {
        def res = CommonUiTestUtils.search(selenium, "script.CmdbScript", "id:${scriptId}")
        if (res.size() == 1)
        {
            runScriptByName(selenium, res[0].name, params, timeout)
        }
        else
        {
            Assert.fail("No scripts found with id ${scriptId}");
        }
    }

    public static runScriptByName(Selenium selenium, String scriptName, Map params = [:],String timeout = "30000")
    {
        def url = "/RapidSuite/script/run/" + scriptName
        if(params)
        {
            url+="?"
            def nameValuePairs = [];
            params.each{paramName, paramValue->
                nameValuePairs <<"${paramName}=${paramValue}"                
            }
            url += nameValuePairs.join("&");
        }
        selenium.openAndWait(url, timeout);
        Assert.assertTrue ("Script ${scriptName} does not exist".toString(), selenium.getLocation().indexOf("/script/list") < 0);
    }
}