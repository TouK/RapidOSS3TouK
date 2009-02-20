def inputDir = new File("c:/helpFiles");
def outputDir = new File("c:/helpFilesOut");
def static final String HELP_START_TAG = "<p><!-- DesignerBegin --></p>";
def static final String HELP_END_TAG = "<p><!-- DesignerEnd --></p>";
outputDir.mkdirs();
inputDir.listFiles().each{File helpFile->
    def helpFileContent = helpFile.getText();
    def startIndex = helpFileContent.indexOf(HELP_START_TAG);
    def endIndex = helpFileContent.indexOf(HELP_END_TAG);
    if(startIndex != -1 && endIndex != -1)
    {
        helpFileContent = helpFileContent.substring(startIndex+HELP_START_TAG.length(), endIndex);        
    }
    else
    {
        helpFileContent = "No help available";
        println  "No help content found in file ${helpFile.getPath()}"
    }
    def file = new File("${outputDir.getPath()}/${helpFile.getName()}");
    file.setText (helpFileContent);
}