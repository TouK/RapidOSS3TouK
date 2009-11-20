package org.grails.codeeditor.utils;
class CodeEditorFileUtils{
    public static File baseDir = new File("..");
    public static String getRelativeFilePath(File base, File file)
    {
        if(base.canonicalPath == file.canonicalPath)
        {
            return "."
        }
        def fileRelativePath = file.canonicalPath.substring (base.canonicalPath.length()+1)
        fileRelativePath = fileRelativePath.replaceAll("\\\\", "/");
    }

    public static File getFileRelativeToBaseDir(file)
    {
        File f = new File(baseDir, file);
        if(!f.getCanonicalPath().startsWith(baseDir.canonicalPath))
        {
            throw new Exception("you are not authorized to access files outside of base directory.");
        }
        return f;
    }
}