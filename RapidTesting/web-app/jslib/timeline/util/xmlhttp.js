Timeline.XmlHttp=new Object();


Timeline.XmlHttp._onReadyStateChange=function(xmlhttp,fError,fDone){
switch(xmlhttp.readyState){









case 4:

try{
if(xmlhttp.status==0
||xmlhttp.status==200
){
if(fDone){
fDone(xmlhttp);
}

}else{
if(fError){
fError(
xmlhttp.statusText,

xmlhttp.status,

xmlhttp

);
}

}

}catch(e){

Timeline.Debug.exception(e);

}

break;
}
};


Timeline.XmlHttp._createRequest=function(){
if(Timeline.Platform.browser.isIE){
var programIDs=[
"Msxml2.XMLHTTP",
"Microsoft.XMLHTTP",
"Msxml2.XMLHTTP.4.0"
];
for(var i=0;i<programIDs.length;i++){
try{
var programID=programIDs[i];
var f=function(){
return new ActiveXObject(programID);
};
var o=f();






Timeline.XmlHttp._createRequest=f;

return o;
}catch(e){

}
}
throw new Error("Failed to create an XMLHttpRequest object");
}else{
try{
var f=function(){
return new XMLHttpRequest();
};
var o=f();






Timeline.XmlHttp._createRequest=f;

return o;
}catch(e){
throw new Error("Failed to create an XMLHttpRequest object");
}
}
};


Timeline.XmlHttp.get=function(url,fError,fDone){
var xmlhttp=Timeline.XmlHttp._createRequest();

xmlhttp.open("GET",url,true);
xmlhttp.onreadystatechange=function(){
Timeline.XmlHttp._onReadyStateChange(xmlhttp,fError,fDone);
};
xmlhttp.send(null);
};


Timeline.XmlHttp.post=function(url,body,fError,fDone){
var xmlhttp=Timeline.XmlHttp._createRequest();

xmlhttp.open("POST",url,true);
xmlhttp.onreadystatechange=function(){
Timeline.XmlHttp._onReadyStateChange(xmlhttp,fError,fDone);
};
xmlhttp.send(body);
};

Timeline.XmlHttp._forceXML=function(xmlhttp){
try{
xmlhttp.overrideMimeType("text/xml");
}catch(e){
xmlhttp.setrequestheader("Content-Type","text/xml");
}
};