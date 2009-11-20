Function.prototype.createDelegate = function(obj, args, appendArgs){
    var method = this;
    return function() {
        var callArgs = args || arguments;
        if(appendArgs === true){
            callArgs = Array.prototype.slice.call(arguments, 0);
            callArgs = callArgs.concat(args);
        }else if(typeof appendArgs == 'number'){
            callArgs = Array.prototype.slice.call(arguments, 0); // copy arguments first
            var applyArgs = [appendArgs, 0].concat(args); // create method call params
            Array.prototype.splice.apply(callArgs, applyArgs); // splice them in
        }
        return method.apply(obj || window, callArgs);
    };
};

String.prototype.encodeHtml = function() {
 var encodedHtml = escape(this);
 encodedHtml = encodedHtml.replace(/\//g,"%2F");
 encodedHtml = encodedHtml.replace(/\?/g,"%3F");
 encodedHtml = encodedHtml.replace(/=/g,"%3D");
 encodedHtml = encodedHtml.replace(/&/g,"%26");
 encodedHtml = encodedHtml.replace(/@/g,"%40");
 return encodedHtml;
}
