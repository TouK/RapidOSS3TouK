package org.grails.logmanager.utils

import org.apache.log4j.LogManager
import org.apache.log4j.spi.LoggerRepository

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Feb 16, 2009
 * Time: 12:16:38 AM
 * To change this template use File | Settings | File Templates.
 */

public class LogUtils {
    public static final int NEW_LINE = '\n';

    public static List getLoggers() {
        LoggerRepository repo = LogManager.getLoggerRepository();
        def loggerList = [];
        repo.getCurrentCategories().each {
            loggerList.add(it);
        }
        return loggerList;
    }

    public static void removeAllAppenders() {
        LogManager.getLoggerRepository().getCurrentLoggers().each {
            it.removeAllAppenders();
        }
    }

    public static final long getLastNLineOffset(String logFile, long numberOfLines) throws IOException {
        if(numberOfLines <= 0)
        {
            throw new Exception("number of lines should be gerater than 0")
        }
        RandomAccessFile acc = new RandomAccessFile(logFile, "r");
        def offset = -1;
        try {
            offset = acc.length() -1;
            def lineCount = 0;
            while (offset >= 0) {
                acc.seek (offset);
                int c = acc.read();
                switch (c) {
                    case NEW_LINE:
                        lineCount++;
                        if(lineCount >= numberOfLines)
                        {
                            return offset+1;
                        }
                        break;
                }
                offset--;
            }
            return 0;
        }
        finally {
            acc.close();
        }
        return offset;
    }
    public static final String readLine(java.io.RandomAccessFile logFile) throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = logFile.read()) {
                case -1:
                    eol = true;
                    break;
                case NEW_LINE:
                    input.append('\n');
                    eol = true;
                    break;
                default:
                    input.append((char) c);
                    break;
            }
        }
        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }


    public static long readLog(String logFile, long offset, long maxNumberOfRows, List lines) {
        RandomAccessFile acc = new RandomAccessFile(logFile, "r");
        try {
            if (offset > acc.length()) return acc.length();
            acc.seek(offset);

            int lineCount = 0;
            while (lineCount < maxNumberOfRows) {
                lineCount++;
                String line = readLine(acc);
                if (line == null) {
                    break;
                }
                else {
                    lines.add(line);
                }
            }
            offset = acc.getFilePointer();
        }
        finally {
            acc.close();
        }
        return offset;
    }


}