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