package com.ifountain.comp.utils;

import com.ifountain.comp.cli.CommandLineUtility;
import com.ifountain.comp.exception.RCliException;
import com.ifountain.comp.exception.RapidException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Date: Sep 1, 2008
 * Time: 1:42:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsCssCombiner extends CommandLineUtility {
    public static final String TOOL_NAME = "jscsscombiner";
    public final static String JSPATTERN = "\\s*<\\s*script[^>]*src=\\s*\"([^http][^\\s]*)\"[^>]*(/>|>[^<]*</\\s*script>)\\s*";
    public final static String CSSPATTERN = "\\s*<\\s*link[^>]*href=\"([^\\s]*)\"[^>]*(/>|>[^<]*</\\s*link>)\\s*";
    public static final String URLPATTERN = "url\\(\\s*['\"]?([^'\")]+)['\"]?\\s*\\)";
    public static final String FILE_PATH_OPTION = "file";
    public static final String TARGET_PATH_OPTION = "target";
    public static final String APP_PATH_OPTION = "applicationPath";
    public static final String ROOT_PATH_OPTION = "rootPath";
    public static final String SUFFIX_OPTION = "suffix";
    public static final String MEDIA_PATH_OPTION = "mediaPath";

    private String rootPath = "";
    private String appPath = "";
    private String targetPath = "";
    private String filePath = "";
    private String suffix = "";
    private String mediaPath = "images";


    public JsCssCombiner() {
        super(TOOL_NAME, TOOL_NAME + ".log");
    }

    protected String getBaseDirectory() {
        throw new RuntimeException("");
    }

    protected Options createOptions() {
        OptionBuilder.withArgName(FILE_PATH_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription(".html, .jsp, .gsp or etc. file that includes js and css paths.");
        Option filePath = OptionBuilder.create(FILE_PATH_OPTION);

        OptionBuilder.withArgName(TARGET_PATH_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("Target directory where the modified file will be created.");
        Option targetPath = OptionBuilder.create(TARGET_PATH_OPTION);

        OptionBuilder.withArgName(APP_PATH_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("Application path where js and css files are served.");
        Option appPath = OptionBuilder.create(APP_PATH_OPTION);

        OptionBuilder.withArgName(ROOT_PATH_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("Root application path.");
        Option rootPath = OptionBuilder.create(ROOT_PATH_OPTION);

        OptionBuilder.withArgName(SUFFIX_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("Suffix to append generated file's names to prevent caching.");
        Option suffix = OptionBuilder.create(SUFFIX_OPTION);

        OptionBuilder.withArgName(MEDIA_PATH_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("Path to where image files are copied.");
        Option media = OptionBuilder.create(MEDIA_PATH_OPTION);

        Options options = new Options();
        options.addOption(filePath);
        options.addOption(targetPath);
        options.addOption(appPath);
        options.addOption(rootPath);
        options.addOption(suffix);
        options.addOption(media);

        return options;
    }

    protected void processOption(Option option) throws RapidException {
        if (option.getOpt().equals(FILE_PATH_OPTION)) {
            this.filePath = getRequiredOptionValue(option);
        } else if (option.getOpt().equals(TARGET_PATH_OPTION)) {
            this.targetPath = getRequiredOptionValue(option);
        } else if (option.getOpt().equals(APP_PATH_OPTION)) {
            this.appPath = getRequiredOptionValue(option);
        } else if (option.getOpt().equals(ROOT_PATH_OPTION)) {
            this.rootPath = getRequiredOptionValue(option);
        } else if (option.getOpt().equals(SUFFIX_OPTION)) {
            this.suffix = getRequiredOptionValue(option);
        } else if (option.getOpt().equals(MEDIA_PATH_OPTION)) {
            this.mediaPath = getRequiredOptionValue(option);
        }
    }

    protected void execute() throws RapidException {
        System.out.println("Merging: " + filePath);
        StringBuffer wholeHTML = new StringBuffer();
        try {
            File file = new File(filePath);
            getFileContentAsString(file, wholeHTML);
            List jsPaths = new ArrayList();
            List cssPaths = new ArrayList();
            String fileContentMinusPaths = setPathsAndRemoveFromWholeHTML(wholeHTML.toString(), JSPATTERN, jsPaths);
            fileContentMinusPaths = setPathsAndRemoveFromWholeHTML(fileContentMinusPaths, CSSPATTERN, cssPaths);
            writeModifiedHTML(fileContentMinusPaths, file);
            writeCombinedJS(jsPaths, file);
            writeCombinedCSS(cssPaths, file);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occured during combine. Reason: " + e.getMessage());
        }
    }

    protected void getFileContentAsString(File fileToBeModified, StringBuffer wholeFileContent) throws IOException, RapidException {
        if (!fileToBeModified.exists()) {
            if (!fileToBeModified.exists()) {
                throw new RCliException(RCliException.FILE_NOT_FOUND, new Object[]{fileToBeModified.getAbsolutePath()});
            }
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileToBeModified));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                wholeFileContent.append(line + "\n");
            }
        }
        finally {
            bufferedReader.close();
        }
    }

    public String setPathsAndRemoveFromWholeHTML(String wholeHTML, String patternString, List paths) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(wholeHTML);
        String firstPath;
        while (matcher.find()) {
            firstPath = findActualPath(matcher.group(1));
            paths.add(firstPath);
        }
        return matcher.replaceAll("");
    }

    private String findActualPath(String path) {
        if (path.startsWith("/"))
            return rootPath + path;
        else
            return appPath + "/" + path;
    }

    public void writeModifiedHTML(String htmlWithNoScripts, File file) throws Exception {
        String fileName = getFileName(file);
        String combinedJSName = fileName + suffix + ".js";
        String combinedCSSName = fileName + suffix + ".css";
        String replace = "<head>\n\t<script src=\"" + combinedJSName + "\"></script>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"" + combinedCSSName + "\"/>\n";
        String htmlWithScriptsInserted = htmlWithNoScripts.replaceFirst("<head>", replace);
        createFile(htmlWithScriptsInserted, targetPath + "/" + file.getName());
    }

    public void createFile(String content, String filePath) throws Exception {
        File newFile = new File(filePath);
        if (newFile.getParentFile() != null) {
            newFile.getParentFile().mkdirs();
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(newFile));
            bufferedWriter.write(content);
        }
        finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }


    public String getFileName(File file) {
        String fileName = file.getName();
        int indexOfSlash = 0;
        if (fileName.lastIndexOf("/") == -1) {
            indexOfSlash = 0;
        } else {
            indexOfSlash = fileName.lastIndexOf("/");
        }
        fileName = fileName.substring(indexOfSlash, fileName.lastIndexOf("."));
        return fileName;
    }

    private void writeCombinedCSS(List cssPaths, File file) throws Exception {
        StringBuffer combinedFile = new StringBuffer();
        for (int i = 0; i < cssPaths.size(); i++) {
            StringBuffer currentBuffer = new StringBuffer();
            File cssFile = new File((String) cssPaths.get(i)).getAbsoluteFile();
            String cssDir = cssFile.getParent();
            getFileContentAsString(cssFile, currentBuffer);
            String currentCss = currentBuffer.toString();
            currentBuffer = new StringBuffer();
            Pattern pattern = Pattern.compile(URLPATTERN);

            Matcher matcher = pattern.matcher(currentCss);
            int end = 0;
            while (matcher.find()) {
                String wholeUrl = matcher.group();
                String url = matcher.group(1);
                if(!url.startsWith("/") && !url.startsWith("http") && !url.startsWith("ftp")){
                    File mediaFile = new File(cssDir + "/" + url);
                    if(mediaFile.exists()){
                        String mediaFileName = mediaFile.getName();
                        int indexOfLastDot = mediaFileName.lastIndexOf(".");
                        String targetUrl = mediaPath + "/" + mediaFileName.substring(0, indexOfLastDot) +
                                suffix + "." + mediaFileName.substring(indexOfLastDot + 1, mediaFileName.length());
                        String targetFileName = targetPath + "/" + targetUrl;
                        File targetFile = new File(targetFileName);
                        if(!targetFile.exists()){
                            System.out.println("Copying " + mediaFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
                            org.apache.commons.io.FileUtils.copyFile(mediaFile, targetFile);    
                        }
                        matcher.appendReplacement(currentBuffer, wholeUrl.replaceAll(url, targetUrl));
                    }
                }
                end = matcher.end();
            }
            currentBuffer.append(currentCss.substring(end, currentCss.length()));
            combinedFile.append(currentBuffer);

        }

        createFile(combinedFile.toString(), targetPath + "/" + getFileName(file) + suffix + ".css");

    }

    private void writeCombinedJS(List jsPaths, File file) throws Exception {
        StringBuffer combinedFile = new StringBuffer();
        for (int i = 0; i < jsPaths.size(); i++) {
            getFileContentAsString(new File((String) jsPaths.get(i)), combinedFile);
        }
        createFile(combinedFile.toString(), targetPath + "/" + getFileName(file) + suffix + ".js");
    }

    protected void validateArgs() throws RapidException {
    }

    public static void main(String[] args) {
        JsCssCombiner combiner = new JsCssCombiner();
        try {
            combiner.run(args);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
