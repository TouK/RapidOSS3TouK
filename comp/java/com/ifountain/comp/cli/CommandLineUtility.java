package com.ifountain.comp.cli;

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.commons.cli.*;
import com.ifountain.comp.utils.RapidLogUtilities;
import com.ifountain.comp.exception.RCliException;
import com.ifountain.comp.exception.RapidException;

import java.util.Properties;
import java.util.Collection;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.IOException;

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
public abstract class CommandLineUtility {
    protected static Logger logger = Logger.getRootLogger();
    public static final String CMD_OPTIONS_FILE_NAME = "cmd.options";
    private String TOOL_NAME;
    private String logFile;
    private Options options;

    public CommandLineUtility(String toolName, String logFile) {
        this.TOOL_NAME = toolName;
        this.logFile = logFile;
        initializeLogger();
        setOptions(createOptions());
    }

    public Options getOptions() {
        return options;
    }

    private void setOptions(Options options) {
        if (options != null) {
            this.options = options;
        } else {
            this.options = new Options();
        }
    }

    protected void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(TOOL_NAME, options);
    }

    protected String getRequiredOptionValue(Option option) throws RCliException {
        String val = option.getValue().trim();
        if (option.isRequired() && val.length() == 0) {
            logger.warn("No " + option.getOpt() + " option is specified.");
            throw new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[]{option.getOpt()});
        }
        return val;
    }

    protected void initializeLogger() {
        logger.removeAllAppenders();
        Appender appender;
        logger.setAdditivity(false);
        try {
            DailyRollingFileAppender fileappender = RapidLogUtilities.getDailyRollingFileLogAppender(getBaseDirectory() + "/logs/" + logFile);
            fileappender.setAppend(true);
            appender = fileappender;
            appender.setName("DailyRollingFileAppender");
        } catch (Exception e) {
            appender = RapidLogUtilities.getConsoleLogAppender("System.err");
            appender.setName("ConsoleAppender");
        }
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
    }

    public Logger getLogger() {
        return logger;
    }

    private String[] mergeArgs(String[] args) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(getBaseDirectory() + "/conf/" + CMD_OPTIONS_FILE_NAME);
            props.load(in);
        }
        catch (Exception e) {
        }
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException e1) {
            }
        }

        Collection allOptions = options.getOptions();
        Properties usedProperties = new Properties();
        for (Iterator iter = allOptions.iterator(); iter.hasNext();) {
            Option element = (Option) iter.next();
            String value = props.getProperty(element.getOpt());
            if (value != null) {
                usedProperties.put("-" + element.getOpt(), value);
            }
        }


        for (int i = 0; i < args.length; i++) {
            usedProperties.remove(args[i]);
        }

        String[] newArgs = new String[args.length + usedProperties.size() * 2];
        System.arraycopy(args, 0, newArgs, 0, args.length);
        int lastIndex = args.length;
        Enumeration keys = usedProperties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = usedProperties.getProperty(key);
            newArgs[lastIndex] = key;
            lastIndex++;
            newArgs[lastIndex] = value;
            lastIndex++;
        }
        return newArgs;
    }

    public final void run(String[] args) throws RapidException {

        try {
            parseArgs(args);
            validateArgs();
        } catch (RCliException e) {
            printUsage(options);
            throw e;
        }
        execute();
    }

    public final void parseArgs(String[] args) throws RapidException {
        args = mergeArgs(args);
        CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (MissingOptionException e) {
            throw new RCliException(RCliException.MISSING_COMMAND_LINE_OPTION, new Object[]{e.getMessage()});
        }
        catch (MissingArgumentException e) {
            throw new RCliException(RCliException.MISSING_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[]{e.getMessage()});
        }
        catch (ParseException e) {
            throw new RCliException(RCliException.INVALID_COMMAND_LINE_OPTIONS, new Object[]{e.getMessage()});
        }


        Option[] optionList = line.getOptions();
        for (int i = 0; i < optionList.length; i++) {
            processOption(optionList[i]);
        }
    }


    public String getToolName() {
        return TOOL_NAME;
    }

    abstract protected String getBaseDirectory();

    abstract protected Options createOptions();

    abstract protected void processOption(Option option) throws RapidException ;

    abstract protected void execute() throws RapidException;

    abstract protected void validateArgs() throws RapidException;

}
