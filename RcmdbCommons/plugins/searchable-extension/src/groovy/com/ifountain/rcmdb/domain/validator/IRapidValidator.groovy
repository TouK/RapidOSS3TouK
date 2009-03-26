package com.ifountain.rcmdb.domain.validator

import org.springframework.validation.Validator

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 25, 2009
* Time: 6:07:57 PM
* To change this template use File | Settings | File Templates.
*/
interface IRapidValidator extends Validator{
    public void validate(java.lang.Object wrappedObject, Object realObject, org.springframework.validation.Errors errors);
}