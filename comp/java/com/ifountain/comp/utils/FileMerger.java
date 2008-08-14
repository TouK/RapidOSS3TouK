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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yekmer
 */

public class FileMerger {

    final static String JSPATTERN = "\\s*<\\s*script[^>]*src=\\s*\"([^http][^\\s]*)\"[^>]*(/>|>[^<]*</\\s*script>)\\s*";
    final static String CSSPATTERN = "\\s*<\\s*link[^>]*href=\"([^\\s]*)\"[^>]*(/>|>[^<]*</\\s*link>)\\s*";
    public static String ROOT_PATH = "";
    public static String APP_PATH = "";
    public static String TARGET_PATH = "";
    public static String FILE_PATH = "";
    public static boolean willCombineCss = false;

    public static void main(String[] args) {
        FILE_PATH = args[0];
        APP_PATH = args[1];
        TARGET_PATH = args[2];
        willCombineCss = Boolean.parseBoolean(args[3]);
        if (args.length > 4) {
            ROOT_PATH = args[4];
        }
        String[] htmlFiles = {FILE_PATH};
        FileMerger mergeJSandCSSFiles = new FileMerger();
        mergeJSandCSSFiles.mergeFiles(htmlFiles);

    }

    public void mergeFiles(String[] files) {
        for (int i = 0; i < files.length; i++) {
            System.out.println("Merging:" + files[i]);
            StringBuffer wholeHTML = new StringBuffer();
            try {

                File file = new File(files[i]);
                getFileContentAsString(file, wholeHTML);
                List jsPaths = new ArrayList();
                List cssPaths = new ArrayList();
                String fileContentMinusPaths = setPathsAndRemoveFromWholeHTML(
                        wholeHTML.toString(), JSPATTERN, jsPaths);

                if (willCombineCss) {
                    fileContentMinusPaths = setPathsAndRemoveFromWholeHTML(
                            fileContentMinusPaths, CSSPATTERN, cssPaths);
                    System.out.println("CSS files size " + cssPaths.size());
                    for (Iterator iter = cssPaths.iterator(); iter.hasNext();) {
                        String element = (String) iter.next();
                        System.out.println("path: " + element);
                    }
                }


                writeModifiedHTML(fileContentMinusPaths, file);
                writeMergedJS(jsPaths, file);
                if (willCombineCss) {
                    writeMergedCSS(cssPaths, file);    
                }

                System.out.println("jspaths: " + jsPaths);
                System.out.println("fileContentMinusPaths: " + fileContentMinusPaths);
                System.out.println("cssPaths: " + cssPaths);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occured during merge. Reason: " + e.getMessage());
            }
        }


    }

    private void writeMergedCSS(List cssPaths, File file) throws Exception {
        writeMergedFile(cssPaths, file, ".css");
    }

    private void writeMergedJS(List jsPaths, File file) throws Exception {
        writeMergedFile(jsPaths, file, ".js");
    }

    private void writeMergedFile(List paths, File file, String extension) throws Exception {
        StringBuffer mergedFile = new StringBuffer();

        for (int i = 0; i < paths.size(); i++) {
            getFileContentAsString(new File((String) paths.get(i)), mergedFile);
        }

        createFile(mergedFile.toString(), TARGET_PATH + "/" + getFileName(file) + extension);

    }

    public String setPathsAndRemoveFromWholeHTML(String wholeHTML,
                                                 String patternString, List paths) {
        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(wholeHTML);
        String firstPath = null;
        while (matcher.find()) {
            firstPath = findActualPath(matcher.group(1));
            paths.add(firstPath);
        }

        return matcher.replaceAll("");

    }

    // Be carefull it appends one more \n at the end of string
    protected void getFileContentAsString(File fileToBeMerged,
                                          StringBuffer wholeFileContent) throws IOException {
        if (!fileToBeMerged.exists()) {
            return;
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileToBeMerged));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wholeFileContent.append(line + "\n");
            }
        }
        finally {
            bufferedReader.close();
        }
    }

    private String findActualPath(String path) {
        if (path.startsWith("/"))
            return ROOT_PATH + path;
        else
            return APP_PATH + path;
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

    public void writeModifiedHTML(String htmlWithNoScripts, File file) throws Exception {
        String fileName = getFileName(file);
        String combinedJSName = fileName + ".js";
        String combinedCSSName = fileName + ".css";
        String replace ="<head>\n\t<script src=\"" + combinedJSName +"\"></script>\n";
        if(willCombineCss){
            replace = "<head>\n\t<script src=\"" + combinedJSName +"\"></script>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"" + combinedCSSName + "\"/>\n";
        }
        String htmlWithScriptsInserted = htmlWithNoScripts.replaceFirst("<head>",replace);

        createFile(htmlWithScriptsInserted, TARGET_PATH + "/" + file.getName());
    }

    public String getFileName(File file) {
        String fileName = file.getName();
        int indexOfSlash = 0;
        if (fileName.lastIndexOf("/") == -1) {
            indexOfSlash = 0;
        } else {
            indexOfSlash = fileName.lastIndexOf("/");
        }
        fileName = fileName.substring(indexOfSlash, fileName.indexOf("."));
        return fileName;
    }

}
