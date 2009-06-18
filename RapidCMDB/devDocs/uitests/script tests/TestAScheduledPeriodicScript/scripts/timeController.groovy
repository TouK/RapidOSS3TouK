    
     import java.text.SimpleDateFormat;

     import org.apache.commons.lang.StringUtils

     def arrayL = new ArrayList();

     def  br=new BufferedReader(new FileReader(params.file));
     String line=null;
     while((line=br.readLine())!=null){
         if (line.endsWith("Hello from periodic"))
             arrayL.add(line)
     }
     arrayL.trimToSize()
     def arrayListSize = arrayL.size()-1
     def splitted = new String[3]
     splitted = StringUtils.split(arrayL.get(arrayListSize--),' ')
     def secondTime=splitted[1]
     splitted = StringUtils.split(arrayL.get(arrayListSize--),' ')
     def firstTime =splitted[1]
     SimpleDateFormat   formatter = new SimpleDateFormat("HH:mm:ss.SSS")
     Date date = (Date)formatter.parse(firstTime);
     long FlongDate=date.getTime();
     date = (Date)formatter.parse(secondTime);
     long SlongDate=date.getTime();

  return SlongDate-FlongDate