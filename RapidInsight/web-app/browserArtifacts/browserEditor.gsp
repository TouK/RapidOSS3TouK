<g:if test="${property.type == Boolean.class || property.type == boolean.class}">
    <g:checkBox name="${property.name}" value="${domainObject[property.name]}"></g:checkBox>
</g:if>
<g:elseif test="${Number.class.isAssignableFrom(property.type) || (property.type.isPrimitive() && property.type != boolean.class)}">
    <g:if test="${!cp}">
        <g:if test="${property.type == Byte.class}">
            <g:select from="${-128..127}" name="${property.name}" value="${domainObject[property.name]}"></g:select>
        </g:if>
        <g:else>
            <input type="text" name="${property.name}" value="${fieldValue(bean: domainObject, field: property.name)}"/>
        </g:else>
    </g:if>
    <g:else>
        <g:if test="${cp.range}">
             <g:select from="${new IntRange(cp.range.from, cp.range.to)}" name="${property.name}" value="${domainObject[property.name]}" noselection="${['':'']}"></g:select>
        </g:if>
        <g:else>
             <input type="text" name="${property.name}" value="${fieldValue(bean: domainObject, field: property.name)}" />
        </g:else>
    </g:else>
</g:elseif>
<g:elseif test="${property.type == String.class || property.type == URL.class}">
   <g:if test="${!cp}">
       <input type="text" name="${property.name}" value="${fieldValue(bean: domainObject, field: property.name)}"/>
   </g:if>
    <g:else>
        <g:if test="${cp.inList}">
           <g:select name="${property.name}" from="${domainObject.constraints[property.name].inList.collect{it.encodeAsHTML()}}" value="${domainObject[property.name]}"></g:select>
        </g:if>
        <g:else>
           <input ${cp.password?  'type="password"':'type="text"'} ${!cp.editable ? 'readonly="readonly"':''} ${cp.maxSize ? 'maxlength="' + cp.maxSize + '"':''} name="${property.name}" value="${fieldValue(bean: domainObject, field: property.name)}"/>
        </g:else>
    </g:else>
</g:elseif>
<g:elseif test="${property.type == Date.class || property.type == java.sql.Date.class || property.type == java.sql.Time.class || property.type == Calendar.class}">
   <g:if test="${!cp}">
        <g:datePicker name="${property.name}" value="${domainObject[property.name]}"></g:datePicker>
   </g:if>
   <g:else>
        <g:if test="${!cp.editable}">
            <g:fieldValue bean="${domainObject}" field="${property.name}"></g:fieldValue>
        </g:if>
        <g:else>
            <g:datePicker name="${property.name}" value="${domainObject[property.name]}"  ></g:datePicker>
        </g:else>
   </g:else>
</g:elseif>
<g:elseif test="${property.type == TimeZone.class}">
   <g:timeZoneSelect name="${property.name}" value="${domainObject[property.name]}"></g:timeZoneSelect>
</g:elseif>
<g:elseif test="${property.type == Locale.class}">
   <g:localeSelect name="${property.name}" value="${domainObject[property.name]}"></g:localeSelect>
</g:elseif>
<g:elseif test="${property.type == Currency.class}">
   <g:currencySelect name="${property.name}" value="${domainObject[property.name]}"></g:currencySelect>
</g:elseif>
<g:elseif test="${property.type == ([] as Byte[]).class || property.type == ([] as byte[]).class}" >
    <input type="file" name="${property.name}" />
</g:elseif> 