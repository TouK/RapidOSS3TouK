import java.util.regex.Pattern

def paths=[];

paths.add("web-app/memoryResults/histograms/2010_09_30_19_02_04.hist")
paths.add("web-app/memoryResults/histograms/2010_09_30_19_09_51.hist")


//extract data from files
def entriesPerFile=[];
paths.size().times{ index ->
    def file=new File(paths[index]);
    def entries=[:];
    def lines=file.readLines();
    if(lines.size()>3)
    {
      lines=lines[3..lines.size()-1];
      lines.each{ line ->
        def parts=[];
        def matcher=Pattern.compile("\\S*").matcher(line)

        while(matcher.find())
        {
            def part=matcher.group();
            if(part.size()>0)
            {
                parts.add(part);
            }
        }
        
        def values=[:];
        //values.num=parts[0];
        values.instances=Long.parseLong(parts[1]);
        values.bytes=Long.parseLong(parts[2]);
        values.className=parts[3];
        if(!values.className)
        {
            values.className=parts[0];
        }

        entries[values.className]=values;
      }
    }
    entriesPerFile[index]=entries;
}

//find allClassNames from all files
def allClassNames=[:];
entriesPerFile.each { entries ->
    entries.keySet().each{ className ->
        allClassNames[className]=true;
    }
}

//build comparisonData to sort and print
def comparisonData=[];

allClassNames.keySet().each{ className ->
    def comparisonRow=[];
    comparisonRow.add(className);
    ["bytes","instances"].each { propName ->

        paths.size().times{ index ->
            def newEntries=entriesPerFile[index];
            def newData=newEntries[className];
            def columnValue=null;
            if(index==0)
            {
                if(newData)
                {
                    columnValue=newData[propName];
                }
            }
            else if(index>0)
            {
                def oldEntries=entriesPerFile[index-1];
                def oldData=oldEntries[className];
                if(oldData && newData) //both have the class
                {
                    def sizeDiff=newData[propName]-oldData[propName];
                    columnValue=sizeDiff;
                }
                else if(!oldData && newData) //old does not exists , new exists
                {
                    columnValue=newData[propName];
                }
            }
            comparisonRow.add(columnValue);
        }
    }
    comparisonData.add(comparisonRow);
}

//sort with the last column
comparisonData.sort{ it[paths.size()]?it[paths.size()]:0l };

//print data
def buffer=new StringBuffer()
paths.size().times{ index ->
    buffer.append("File ${index+1} ${paths[index]} \n");
}
buffer.append("ClassName");
["Bytes","Instances"].each{ propName ->
    paths.size().times{ index ->
        if(index==0)
        {
            buffer.append( "\t${propName} File${index+1}");
        }
        else
        {
            buffer.append( "\t${propName} File${index+1} Diff");
        }
    }
}
buffer.append("\n")

def numberOfRows=comparisonData.size();
numberOfRows.times{ def index  ->
    def comparisonRow=comparisonData[numberOfRows-index-1];
    def columnIndex=0;
    comparisonRow.each{ column ->
        def columnValue=column;
        if(columnValue == null)
        {
            columnValue="---"
        }
        
        if(columnIndex>0)
        {
            columnValue="\t${columnValue}";
        }
        columnIndex++;
        buffer.append(columnValue);
    }
    buffer.append("\n")
}

def output=buffer.toString();

def sdf = new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
def currentTime=sdf.format(Calendar.getInstance().getTime());


def folder=new File("web-app/memoryResults/histograms");
if(!folder.exists())
{
	folder.mkdirs();
}

def file=new File("${folder.path}/histogramComparison_${currentTime}.txt");
file.setText(output);


return "Histogram Comparison file created at ${file.getAbsolutePath()}"