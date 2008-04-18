package com.ifountain.rcmdb.test.util;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 18, 2008
 * Time: 1:31:34 PM
 * To change this template use File | Settings | File Templates.
 */
class IntegrationTestUtils {
    private IntegrationTestUtils()
    {
    }

    def static resetController(controller)
    {
        controller.request.removeAllParameters()
        controller.response.setCommitted(false)
        controller.response.reset()
        controller.flash.message = ""
        controller.flash.errors = [];
        controller.params.clear()
    }
}