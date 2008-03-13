import api.RS;
def space = RS.getAppSpace();

def objs= space.getObjects("RsType==\"Device\""); 
for (obj in objs){
 		println(obj.InstanceName + " " +obj.ClassName);
}
