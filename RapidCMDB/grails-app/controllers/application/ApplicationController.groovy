/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package application

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 1:35:56 AM
* To change this template use File | Settings | File Templates.
*/
class ApplicationController {
    public static final String RESTART_APPLICATION = "restart.application"
    def sessionFactory;
    def searchableService;
    def index = {render(view: "application")}

    def reloadControllers = {
        RsApplication.reloadControllers();
        flash.message = "Controllers reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }

    def reloadFilters = {
        RsApplication.reloadFilters();
        flash.message = "Filters reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }

    def reloadViewsAndControllers = {
        RsApplication.reloadViewsAndControllers();
        flash.message = "Views and controllers reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }
    
    def reloadViews = {
        RsApplication.reloadViews();
        flash.message = "Views reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }
}
