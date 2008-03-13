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
package com.ifountain.comp.exception;

import java.util.ResourceBundle;


/**
 * @author GoKHaN
 */

public interface ExceptionMessageConstants {

	public static final ResourceBundle bundle = ResourceBundle.getBundle("com.ifountain.comp.exception.ExceptionMessages");

    public static final String MANDATORY_PROP_MISSING_OR_EMPTY = bundle
            .getString("mandatoryPropertyMissingOrEmpty");

    public static final String VALUE_SHOULD_BE_POSITIVE_INT_OR_ZERO = bundle
            .getString("valueShouldBePositiveIntOrZero");
    public static final String VALUE_SHOULD_BE_POSITIVE_INT = bundle.getString("valueShouldBePositiveInt");
    public static final String VALUE_SHOULD_BE_FLOAT = bundle.getString("valueShouldBeFloat");
    public static final String VALUE_SHOULD_BE = bundle.getString("valueShouldBe");
    public static final String VALUE_SHOULD_BE_1_OF_2 = bundle.getString("valueShouldBe1Of2");
    public static final String VALUE_SHOULD_BE_1_OF_3 = bundle.getString("valueShouldBe1Of3");
    public static final String VALUE_SHOULD_BE_1_OF_4 = bundle.getString("valueShouldBe1Of4");
    public static final String VALUE_SHOULD_BE_1_OF_5 = bundle.getString("valueShouldBe1Of5");
    public static final String VALUE_SHOULD_BE_INT = bundle.getString("valueShouldBeInt");;

    public static final String INVALID_EXPRESSION = bundle.getString("invalidExpression");
    public static final String INVALID_PROPERTY = bundle.getString("invalidProperty");

    public static final String CONNECTION_UNDEFINED = bundle.getString("connectionUndefined");
    public static final String CAN_NOT_CONNECT = bundle.getString("canNotConnect");
    public static final String CONNECTION_LOST = bundle.getString("connectionLost");
    public static final String NULL_ADAPTER_STATUS_LISTENER = bundle.getString("nullAdapterStatusListener");
    /*
     * The constants to be used inside connection message constants, such as ".... can not connect to InCharge server"
     */
    public static final String INCHARGE = "InCharge";
    public static final String DATABASE = "Database";
    public static final String MAIL = "Mail";
    
    public static final String SQL_ERROR = bundle.getString("sqlError");
    public static final String SCRIPT_FILE_NOT_FOUND = bundle.getString("scriptFileCouldNotBeFound");
    public static final String CONNECTION_PARAM_NOT_DEFINED = bundle.getString("connectionParamNotDefined");

	public static final String NON_EXISTING_EVENT_EXCEPTION_MESSAGE = bundle.getString("nonExistingNetcoolEvent");
	public static final String NON_EXISTING_USER_EXCEPTION_MESSAGE = bundle.getString("nonExistingNetcoolUser");
	public static final String INVALID_PARAMETER_FOR_CMS = bundle.getString("invalidParameterForCMS");

    public static final String INVALID_NAME = bundle.getString("invalidName");
    
	
    
    
}
