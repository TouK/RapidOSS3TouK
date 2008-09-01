package com.ifountain.comp.exception;

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
 * To change this template use File | Settings | File Templates.
 */
public class RCliException extends RapidException{

    public static final String CLI_ERROR_CODE_PREFIX = "RCLI";
    public static final String EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION = CLI_ERROR_CODE_PREFIX +"-0001";
    public static final String MISSING_COMMAND_LINE_OPTION = CLI_ERROR_CODE_PREFIX +"-0002";
    public static final String MISSING_ARGUMENT_FOR_COMMAND_LINE_OPTION = CLI_ERROR_CODE_PREFIX +"-0003";
    public static final String INVALID_COMMAND_LINE_OPTIONS = CLI_ERROR_CODE_PREFIX +"-0004";
    public static final String INVALID_COMMAND_LINE_OPTION_VALUE = CLI_ERROR_CODE_PREFIX +"-0005";
    public static final String FILE_NOT_FOUND = CLI_ERROR_CODE_PREFIX +"-0006";


    static
    {
        messagesMap.addExceptionMessage(INVALID_COMMAND_LINE_OPTIONS, "Options are invalid. Reason : {0}");
        messagesMap.addExceptionMessage(MISSING_COMMAND_LINE_OPTION, "Missing command line option {0}.");
        messagesMap.addExceptionMessage(MISSING_ARGUMENT_FOR_COMMAND_LINE_OPTION, "{0}.");
        messagesMap.addExceptionMessage(EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, "Empty argument value for option {0}.");
        messagesMap.addExceptionMessage(INVALID_COMMAND_LINE_OPTION_VALUE, "Invalid value {0} entered for option {1}.");
        messagesMap.addExceptionMessage(FILE_NOT_FOUND, "File {0} does not exist.");
    }

    public RCliException(String errorId, Object[] customMessages) {
        super(errorId, customMessages);
    }
    public RCliException(String errorId) {
            super(errorId);
        }

    
}
