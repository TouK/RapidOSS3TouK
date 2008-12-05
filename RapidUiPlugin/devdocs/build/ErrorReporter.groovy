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
import org.mozilla.javascript.EvaluatorException

/**
* Created by IntelliJ IDEA.
* User: iFountain
* Date: Jul 15, 2008
* Time: 3:59:06 PM
* To change this template use File | Settings | File Templates.
*/
class ErrorReporter implements org.mozilla.javascript.ErrorReporter{
    StringBuffer  warningMessage = new StringBuffer();
    StringBuffer  errorMessage = new StringBuffer();
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (line < 0) {
            warningMessage.append("[WARNING] ").append(message);
        } else {
            warningMessage.append(line).append(':').append(lineOffset).append(':').append(message);
        }
    }

    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (line < 0) {
            errorMessage.append("[ERROR] ").append(message);
        } else {
            errorMessage.append(line).append(':').append(lineOffset).append(':').append(message);
        }
    }

    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }
}