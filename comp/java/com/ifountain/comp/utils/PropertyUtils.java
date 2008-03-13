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
package com.ifountain.comp.utils;

import java.text.MessageFormat;
import java.util.Properties;

import com.ifountain.comp.exception.ExceptionMessageConstants;


public class PropertyUtils {

	private PropertyUtils() {
	}

	public static String checkMandatoryFieldIsEntered(Properties props, String propertyKey) throws Exception {
	
	    String prop = props.getProperty(propertyKey);
	    if ((prop == null) || (prop.trim().length() == 0)) {
	        throw new Exception(PropertyUtils.exceptionMessageGeneratorForNullOrEmpty(propertyKey));
	    }
	    return prop.trim();
	}
	// ///////////////////////////////////////////////// GoKHaN
	// //////////////////////////////////////////////////

	public static boolean checkThatFieldIsBoolean(Properties props, String propertyKey)
	        throws Exception {
	
	    String prop = checkMandatoryFieldIsEntered(props, propertyKey);
	    if (!(prop.equalsIgnoreCase("true") || prop.equalsIgnoreCase("false"))) {
	        throw new Exception(PropertyUtils.exceptionMessageGeneratorForInvalidValueToBoolProperty(propertyKey,
	                prop));
	    }
	    return prop.equalsIgnoreCase("true");
	}

	public static int checkThatFieldIsPositiveIntegerOrZero(String propertyName, Properties props,
	        String propertyKey) throws Exception {
	
	    String prop = checkMandatoryFieldIsEntered(props, propertyKey);
	    try {
	        int propInt = Integer.parseInt(prop);
	        if (propInt < 0)
	            throw new Exception();
	        return propInt;
	    } catch (Exception ex) {
	        throw new Exception(PropertyUtils.exceptionMessageGeneratorForPositiveIntegerOrZero(propertyName,
	                propertyKey, prop));
	    }
	}

	public static int checkThatFieldIsPositiveInteger(String propertyName, Properties props,
	        String propertyKey) throws Exception {
	
	    String prop = checkMandatoryFieldIsEntered(props, propertyKey);
	    try {
	        int propInt = Integer.parseInt(prop);
	        if (propInt <= 0)
	            throw new Exception();
	        return propInt;
	    } catch (Exception ex) {
	        throw new Exception(PropertyUtils.exceptionMessageGeneratorForPositiveInteger(propertyName, propertyKey,
	                prop));
	    }
	}

	public static String checkValidityForAnyField(String propertyName, Properties props, String propertyKey,
	        String[] validValues) throws Exception {
	
	    String prop = checkMandatoryFieldIsEntered(props, propertyKey);
	    boolean OKFlag = false;
	    for (int i = 0; i < validValues.length; i++) {
	        if (prop.equals(validValues[i])) {
	            OKFlag = true;
	            break;
	        }
	    }
	    if (!OKFlag)
	        throw new Exception(PropertyUtils.exceptionMessageGeneratorForInvalidValueToAnyProperty(propertyName,
	                propertyKey, prop, validValues));
	    return prop;
	
	}

	public static String exceptionMessageGeneratorForNullOrEmpty(String propertyKey) {
	
	    return MessageFormat.format(ExceptionMessageConstants.MANDATORY_PROP_MISSING_OR_EMPTY, new Object[] { propertyKey });
	}

	public static String exceptionMessageGeneratorForInvalidValueToBoolProperty(String propertyKey,
	        String invalidValue) {
	    return MessageFormat.format(ExceptionMessageConstants.VALUE_SHOULD_BE_1_OF_2, new Object[] { propertyKey, "true", "false" });
	}

	public static String exceptionMessageGeneratorForPositiveInteger(String propertyKey) {
	
	    return PropertyUtils.exceptionMessageGeneratorForPositiveInteger("", propertyKey, "");
	}

	public static String exceptionMessageGeneratorForPositiveInteger(String propertyName, String propertyKey,
	        String invalidValue) {
	
	    return MessageFormat.format(ExceptionMessageConstants.VALUE_SHOULD_BE_POSITIVE_INT, new Object[] { propertyKey });
	}

	public static String exceptionMessageGeneratorForPositiveIntegerOrZero(String propertyKey) {
	
	    return PropertyUtils.exceptionMessageGeneratorForPositiveIntegerOrZero("", propertyKey, "");
	}

	public static String exceptionMessageGeneratorForPositiveIntegerOrZero(String propertyName,
	        String propertyKey, String invalidValue) {
	
	    return MessageFormat.format(ExceptionMessageConstants.VALUE_SHOULD_BE_POSITIVE_INT_OR_ZERO, new Object[] { propertyKey });
	}

	public static String exceptionMessageGeneratorForInvalidValueToAnyProperty(String propertyName,
	        String propertyKey, String invalidValue, String[] validValues) {
	
	    String message = "The property \"" + propertyName + "\" inside \"" + propertyKey
	            + "\" has the invalid value \"" + invalidValue + "\". The valid values are : ";
	
	    for (int i = 0; i < validValues.length - 1; i++) {
	        message += validValues[i] + ", ";
	    }
	    message += validValues[validValues.length - 1] + ".";
	    return message;
	}

	
}
