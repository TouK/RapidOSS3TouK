package com.ifountain.rcmdb.exception

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 20, 2009
* Time: 3:34:27 PM
*/
class MessageSourceException extends Exception {
    private String code;
    private Object[] args;

    public MessageSourceException(String code, Object[] arguments) {
        super(code);
        this.code = code;
        this.args = arguments;
    }

    public String getCode() {
        return code;
    }
    public Object[] getArgs() {
        return args;
    }

}