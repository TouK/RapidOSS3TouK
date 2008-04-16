package com.ifountain.comp.cli;

import com.ifountain.comp.exception.RCliException;
import com.ifountain.comp.test.util.RCompTestCase;
import com.ifountain.comp.test.util.file.FileTestUtils;
import com.ifountain.comp.test.util.file.TestFile;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.*;

import java.io.File;
import java.util.*;/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
public class CommandLineUtilityTest extends RCompTestCase{
    protected void setUp() throws Exception
        {
            super.setUp();
            FileTestUtils.deleteFile(CommandLineUtility.CMD_OPTIONS_FILE_NAME);
        }

        protected void tearDown() throws Exception
        {
            super.tearDown();
            FileTestUtils.deleteFile(CommandLineUtility.CMD_OPTIONS_FILE_NAME);
        }

        public void testInitializeLogger() throws Exception
        {
            String tooleName = "newtool";
            String logFilePath = "logs/newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected org.apache.commons.cli.Options createOptions() {return null;};
                protected void execute() throws RCliException {};
                protected void validateArgs() throws RCliException {
                }
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };
            utility.initializeLogger();
            assertEquals("Default level should be info", Level.INFO, utility.getLogger().getLevel());
            assertFalse(utility.getLogger().getAdditivity());
            Enumeration appenders = utility.getLogger().getAllAppenders();
            assertTrue("Appander should exist", appenders.hasMoreElements());
            Appender appender = (Appender) appenders.nextElement();
            assertEquals("Appander should be daily rolling file appender", DailyRollingFileAppender.class.getName(), appender.getClass().getName());
            assertEquals(PatternLayout.class.getName(), appender.getLayout().getClass().getName());
            assertEquals("%d %p : %m%n", ((PatternLayout)appender.getLayout()).getConversionPattern());
            assertEquals(new TestFile(logFilePath), new File(((DailyRollingFileAppender)appender).getFile()));
        }

        public void testInitializeLoggerWillStartConsoleAppenderIfExceptionOccursWhileCreatingFileAppender() throws Exception
        {
            String tooleName = "newtool";
            String fileName = "invalidlogfile";
            FileTestUtils.deleteFile(fileName);
            new TestFile(fileName).mkdirs();
            CommandLineUtility utility = new CommandLineUtility(tooleName, fileName)
            {
                protected void validateArgs() throws RCliException {
                }
                protected org.apache.commons.cli.Options createOptions() {return null;};
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };
            utility.initializeLogger();
            assertEquals("Default level should be info", Level.INFO, utility.getLogger().getLevel());

            Enumeration appenders = utility.getLogger().getAllAppenders();
            assertTrue("Appander should exist", appenders.hasMoreElements());
            Appender appender = (Appender) appenders.nextElement();
            assertEquals("Appander should be console appender", ConsoleAppender.class.getName(), appender.getClass().getName());
            assertEquals(PatternLayout.class.getName(), appender.getLayout().getClass().getName());
            assertEquals("%d %p : %m%n", ((PatternLayout)appender.getLayout()).getConversionPattern());
        }

        public void testOptionsWillBeSetToOptionsReturnedByCreateOptions() throws Exception
        {

            final String optionName = "password";

            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected void validateArgs() throws RCliException {
                }
                protected Options createOptions() {
                    System.out.println(optionName);
                    OptionBuilder.withArgName(optionName);
                    OptionBuilder.hasArg();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option = OptionBuilder.create(optionName);

                    Options createdOptions = new Options();
                    createdOptions.addOption(option);
                    return createdOptions;
                };
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };


            Collection options = utility.getOptions().getOptions();
            assertEquals(1, options.size());
            Option option = (Option) options.iterator().next();
            assertEquals(optionName, option.getArgName());
        }
        public void testIfCreateOptionReturnsNullOptionWillBeSetToEmptyOption() throws Exception
        {


            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected void validateArgs() throws RCliException {
                }
                protected Options createOptions() {
                    return null;
                };
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };

            assertNotNull(utility.getOptions());

            Collection options = utility.getOptions().getOptions();
            assertEquals(0, options.size());
        }

        public void testIfRequiredOptionIsMissingThrowsException() throws Exception
        {

            final String optionName = "password";

            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected void validateArgs() throws RCliException {
                }
                protected Options createOptions() {
                    OptionBuilder.withArgName(optionName);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option = OptionBuilder.create(optionName);

                    Options createdOptions = new Options();
                    createdOptions.addOption(option);
                    return createdOptions;
                };
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };

            try
            {
                utility.run(new String[0]);
                fail("Should throw exception because required option not specified");
            }
            catch (RCliException e)
            {
                RCliException expectedException = new RCliException(RCliException.MISSING_COMMAND_LINE_OPTION, new String[]{"-"+optionName});
                assertEquals(expectedException.toString(), e.toString());
            }
        }



        public void testIfNoValueSpecifiedForOptionWithArgThrowsException() throws Exception
        {

            final String optionName = "password";

            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected void validateArgs() throws RCliException {
                }
                protected Options createOptions() {
                    OptionBuilder.withArgName(optionName);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option = OptionBuilder.create(optionName);

                    Options createdOptions = new Options();
                    createdOptions.addOption(option);
                    return createdOptions;
                };
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };

            try
            {
                utility.run(new String[]{"-" + optionName});
                fail("Should throw exception because required option not specified");
            }
            catch (RCliException e)
            {
                RCliException expectedException = new RCliException(RCliException.MISSING_ARGUMENT_FOR_COMMAND_LINE_OPTION, new String[]{"no argument for:password"});
                assertEquals(expectedException.toString(), e.toString());
            }
        }

        public void testLoadingOptionsFromOptionFile() throws Exception
        {
            final String optionName1 = "password";
            final String optionName2 = "username";
            final String optionValue1 = "user1pass";
            final String optionValue2 = "username1";
            File optionsFile = new TestFile(CommandLineUtility.CMD_OPTIONS_FILE_NAME);

            ArrayList lines = new ArrayList();
            lines.add(optionName1 + "=" + optionValue1);
            lines.add(optionName2 + "=" + optionValue2);

            FileTestUtils.generateFile(optionsFile.getPath(), lines);

            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            final List processedOptionsList = new ArrayList();



            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {

                    OptionBuilder.withArgName(optionName2);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option2 = OptionBuilder.create(optionName2);

                    OptionBuilder.withArgName(optionName1);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option1 = OptionBuilder.create(optionName1);


                    Options createdOptions = new Options();
                    createdOptions.addOption(option2);
                    createdOptions.addOption(option1);

                    return createdOptions;
                };
                protected void execute() throws RCliException {};
                protected void validateArgs() throws RCliException
                {
                }
                protected void processOption(Option option) throws RCliException
                {
                    processedOptionsList.add(option);
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }

            };

            utility.run(new String[0]);

            assertEquals(2, processedOptionsList.size());
            for (Iterator iter = processedOptionsList.iterator(); iter.hasNext();)
            {
                Option element = (Option) iter.next();
                assertTrue(element.getOpt().equals(optionName1) || element.getOpt().equals(optionName2));
                if(element.getOpt().equals(optionName2))
                {
                    assertEquals(optionValue2, element.getValue());
                }
                else
                {
                    assertEquals(optionValue1, element.getValue());
                }
            }
        }



        public void testLoadingOptionsFromOptionFileWillBeOverwritedByUserEnteredOptions() throws Exception
        {
            final String optionName1 = "password";
            final String optionName2 = "username";
            final String optionValue1 = "user1pass";
            final String optionValue2 = "username1";
            File optionsFile = new TestFile(CommandLineUtility.CMD_OPTIONS_FILE_NAME);

            ArrayList lines = new ArrayList();
            lines.add(optionName1 + "=" + optionValue1);
            lines.add(optionName2 + "=" + optionValue2);

            FileTestUtils.generateFile(optionsFile.getPath(), lines);

            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            final List processedOptionsList = new ArrayList();



            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {

                    OptionBuilder.withArgName(optionName2);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option2 = OptionBuilder.create(optionName2);

                    OptionBuilder.withArgName(optionName1);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option1 = OptionBuilder.create(optionName1);


                    Options createdOptions = new Options();
                    createdOptions.addOption(option2);
                    createdOptions.addOption(option1);

                    return createdOptions;
                };
                protected void validateArgs() throws RCliException
                {
                }
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                    processedOptionsList.add(option);
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };

            utility.run(new String[]{"-" + optionName1, "user_entered_value"});

            for (Iterator iter = processedOptionsList.iterator(); iter.hasNext();)
            {
                Option element = (Option) iter.next();
                assertTrue(element.getOpt().equals(optionName1) || element.getOpt().equals(optionName2));
                if(element.getOpt().equals(optionName2))
                {
                    assertEquals(optionValue2, element.getValue());
                }
                else
                {
                    assertEquals("user_entered_value", element.getValue());
                }
            }
        }

        public void testPrintUsageWillBeCalledAfterExceptionsInParseArgs() throws Exception
        {
            final String optionName1 = "password";
            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            final List methodCalls = new ArrayList();
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {
                    OptionBuilder.withArgName(optionName1);
                    OptionBuilder.hasArg();
                    OptionBuilder.isRequired();
                    OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
                    Option option1 = OptionBuilder.create(optionName1);


                    Options createdOptions = new Options();
                    createdOptions.addOption(option1);
                    return createdOptions;
                };
                protected void validateArgs() throws RCliException
                {
                }
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                protected void printUsage(Options options)
                {
                    methodCalls.add("printUsage");
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };

            try
            {
                utility.run(new String[0]);
                fail("Should throw exception");
            }
            catch (RCliException e)
            {
            }

            assertEquals(1, methodCalls.size());
        }



        public void testPrintUsageWillBeCalledAfterExceptionsInValidateArgs() throws Exception
        {
            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            final List methodCalls = new ArrayList();
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {
                    return new Options();
                };
                protected void validateArgs() throws RCliException
                {
                    throw new RCliException(RCliException.INVALID_COMMAND_LINE_OPTIONS);
                }
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                    throw new RCliException(RCliException.INVALID_COMMAND_LINE_OPTIONS);
                }
                protected void printUsage(Options options)
                {
                    methodCalls.add("printUsage");
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };

            try
            {
                utility.run(new String[0]);
                fail("Should throw exception");
            }
            catch (RCliException e)
            {
            }

            assertEquals(1, methodCalls.size());
        }

        public void testGetRequiredOptionValue() throws Exception
        {
            final String optionName1 = "password";
            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {
                    return null;
                };
                protected void validateArgs() throws RCliException
                {
                }
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };
            OptionBuilder.withArgName(optionName1);
            OptionBuilder.hasArg();
            OptionBuilder.isRequired();
            OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
            Option option1 = OptionBuilder.create(optionName1);
            option1.addValue(" newvalue ");
            String value = utility.getRequiredOptionValue(option1);
            assertEquals("newvalue", value);
        }

        public void testGetRequiredOptionValueThrowsExceptionIfRequiredParameterIsEmpty() throws Exception
        {
            final String optionName1 = "password";
            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {
                    return null;
                };
                protected void validateArgs() throws RCliException
                {
                }
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };
            OptionBuilder.withArgName(optionName1);
            OptionBuilder.hasArg();
            OptionBuilder.isRequired();
            OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
            Option option1 = OptionBuilder.create(optionName1);
            option1.addValue("  ");
            try
            {
                utility.getRequiredOptionValue(option1);
                fail("Should throw exception because option is empty");
            }
            catch (RCliException e)
            {
                assertEquals(new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[]{optionName1}).getMessage(), e.getMessage());
            }
        }

        public void testGetRequiredOptionValueDoesnotThrowsExceptionIfParameterIsEmptyAndNotRequired() throws Exception
        {
            final String optionName1 = "password";
            String tooleName = "newtool";
            String logFilePath = "newtool.log";
            CommandLineUtility utility = new CommandLineUtility(tooleName, logFilePath)
            {
                protected Options createOptions() {
                    return null;
                };
                protected void validateArgs() throws RCliException
                {
                }
                protected void execute() throws RCliException {};
                protected void processOption(Option option) throws RCliException
                {
                }
                @Override
                protected String getBaseDirectory() {
                    return TestFile.TESTOUTPUT_DIR;
                }
            };
            OptionBuilder.withArgName(optionName1);
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("Password required to authenticate to Rapid Suite");
            Option option1 = OptionBuilder.create(optionName1);
            option1.addValue(" newvalue ");
            String value = utility.getRequiredOptionValue(option1);
            assertEquals("newvalue", value);

        }
    
}
