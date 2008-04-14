package com.ifountain.domain
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
 * User: mustafa
 * Date: Mar 30, 2008
 * Time: 1:25:57 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelGenerationException extends Exception{

    public ModelGenerationException(String message) {
        super(message); //To change body of overridden methods use File | Settings | File Templates.
    }

    public ModelGenerationException(String message, Throwable cause) {
        super(message, cause); //To change body of overridden methods use File | Settings | File Templates.
    }

    public static ModelGenerationException masterDatasourceDoesnotExists(String modelName)
    {
        return new ModelGenerationException("Master datasource doesnot exist for model $modelName")
    }

    public static ModelGenerationException couldNotDeleteOldControllerFile(String modelName)
    {
        return new ModelGenerationException("Could not delete old controller file of model ${modelName}")
    }

    public static ModelGenerationException moreThanOnemasterDatasourceDefined(String modelName)
    {
        return new ModelGenerationException("Only one master datasource should be specified for $modelName")
    }
    public static ModelGenerationException noKeySpecifiedForDatasource(String datasourceName, String modelName)
    {
        return new ModelGenerationException("No keys specified for datasource $datasourceName in model $modelName");
    }



}