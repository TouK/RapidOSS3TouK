package com.ifountain.rcmdb.auth

import auth.Group
import auth.SegmentFilter

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 1, 2009
* Time: 10:37:22 AM
* To change this template use File | Settings | File Templates.
*/
class SegmentQueryHelper {
    public static final String SEGMENT_FILTER = "segmentFilter";
    public static final String CLASSES = "classes";

    private static final String OWN_FILTER = "ownFilter"
    private static final String PARENT_FILTERS = "parentFilters"
    private static final String CHILDREN_HAVING_FILTERS = "childrenHavingFilters"

    private static SegmentQueryHelper helper;
    public static SegmentQueryHelper getInstance() {
        if (helper == null) {
            helper = new SegmentQueryHelper();
        }
        return helper;
    }

    private SegmentQueryHelper() {};
    private groupFilters;
    private classes;
    private parentClassHierarchy;
    private childClassHierarchy;
    private rootClassHierarchy;

    public synchronized void initialize(List classList) {
        groupFilters = [:]
        classes = [:];
        classList.each {Class c ->
            classes[c.name] = c;
        }
        calculateClassHierarchy(classList);
        Group.list().each {Group group ->
            calculateGroupFilters(group.name)
        }
    }

    public synchronized Map getGroupFilters(String groupName) {
        return groupFilters[groupName];
    }

    public synchronized void removeGroupFilters(String groupName) {
        groupFilters.remove(groupName);
    }

    public synchronized void calculateGroupFilters(String groupName) {
        Group group = Group.get(name: groupName)
        if (group) {
            def filterMap = [:]
            def segmentFilter = "";
            def segmentFilterType = group.segmentFilterType;
            if (segmentFilterType == Group.GLOBAL_FILTER) {
                segmentFilter = group.segmentFilter
            }
            else {
                filterMap[CLASSES] = calculateClassFilters(group.filters);
            }
            filterMap[SEGMENT_FILTER] = segmentFilter;
            groupFilters[groupName] = filterMap;
        }
    }

    private void calculateClassHierarchy(List classList) {
        parentClassHierarchy = [:]
        childClassHierarchy = [:]
        rootClassHierarchy = [:]
        classList.each {Class c ->
            parentClassHierarchy[c] = []
            childClassHierarchy[c] = []
        }
        classList.each {Class c ->
            classList.each {Class otherClass ->
                if (c != otherClass && c.isAssignableFrom(otherClass)) {
                    parentClassHierarchy[otherClass].add(c);
                    childClassHierarchy[c].add(otherClass);
                }
            }
        }
        classList.each {Class c ->
            if (parentClassHierarchy[c].size() == 0) {
                childClassHierarchy[c].each {Class childClass ->
                    rootClassHierarchy[childClass] = c;
                }
            }
        }
    }

    private Map calculateClassFilters(List segmentFilters) {
        def classFilters = [:]
        def tempClassMap = [:]
        segmentFilters.each {SegmentFilter filter ->
            String className = filter.className;
            if (classes.containsKey(className)) {
                Class domainClass = classes[className];
                if (tempClassMap[className] == null) {
                    tempClassMap[className] = [:]
                }
                tempClassMap[className][OWN_FILTER] = filter.filter;
                def childClasses = childClassHierarchy[domainClass];
                childClasses.each {Class childClass ->
                    def childClassName = childClass.name;
                    if (tempClassMap[childClassName] == null) {
                        tempClassMap[childClassName] = [:]
                    }
                    if (tempClassMap[childClassName][PARENT_FILTERS] == null) {
                        tempClassMap[childClassName][PARENT_FILTERS] = []
                    }
                    tempClassMap[childClassName][PARENT_FILTERS].add(filter.filter);
                }
                def parentClasses = parentClassHierarchy[domainClass];
                parentClasses.each {Class parentClass ->
                    def parentClassName = parentClass.name;
                    if (tempClassMap[parentClassName] == null) {
                        tempClassMap[parentClassName] = [:]
                    }
                    if (tempClassMap[parentClassName][CHILDREN_HAVING_FILTERS] == null) {
                        tempClassMap[parentClassName][CHILDREN_HAVING_FILTERS] = []
                    }
                    tempClassMap[parentClassName][CHILDREN_HAVING_FILTERS].add(className);
                }
            }
        }
        tempClassMap.each {className, queryConfig ->
            Class currentClass = classes[className];
            if (parentClassHierarchy[currentClass].size() == 0) {
                classFilters[className] = getClassQuery(currentClass, tempClassMap, null).toString()
            }
            else {
                List path = [];
                path.addAll(parentClassHierarchy[currentClass])
                path.add(currentClass);
                path.addAll(childClassHierarchy[currentClass])
                Class rootClass = rootClassHierarchy[currentClass]
                classFilters[className] = "alias:${className} AND (${getClassQuery(rootClass, tempClassMap, path)})".toString()
            }
        }
        //        tempClassMap.each {className, queryConfig ->
        //            def query = "";
        //            if (queryConfig.containsKey(OWN_FILTER)) {
        //                query = queryConfig[OWN_FILTER];
        //            }
        //            if (queryConfig.containsKey(PARENT_FILTERS)) {
        //                def parentQuery = "";
        //                def parentFilters = queryConfig[PARENT_FILTERS]
        //                if (parentFilters.size() == 1) {
        //                      parentQuery = parentFilters[0]
        //                }
        //                else {
        //                    def parentFilterArray = []
        //                    queryConfig[PARENT_FILTERS].each {String parentFilter ->
        //                        parentFilterArray.add("(${parentFilter})")
        //                    }
        //                    parentQuery = parentFilterArray.join(" AND ");
        //                }
        //                if(query != ""){
        //                    query = "${query} AND (${parentQuery})"
        //                }
        //                else{
        //                    query = parentQuery;
        //                }
        //            }
        //            if(queryConfig.containsKey(CHILDREN_HAVING_FILTERS)){
        //                def childrenQuery = "";
        //                def childrenHavingFilter = queryConfig[CHILDREN_HAVING_FILTERS]
        //                if(childrenHavingFilter.size() == 1){
        //                    def childClassName = childrenHavingFilter[0];
        //                    childrenQuery = "(alias:${childClassName} AND (${tempClassMap[childClassName][OWN_FILTER]})) OR (alias:* NOT alias:${childClassName})"
        //                }
        //                else{
        //                    def childrenQueryArray = [];
        //                    def excludeQueryBuf = new StringBuffer("(alias:*")
        //                    childrenHavingFilter.each{String childClassName ->
        //                        excludeQueryBuf.append(" NOT alias:${childClassName}");
        //                        childrenQueryArray.add("(alias:${childClassName} AND (${tempClassMap[childClassName][OWN_FILTER]}))")
        //                    }
        //                    excludeQueryBuf.append(")")
        //                    childrenQuery = "${childrenQueryArray.join(' OR ')} OR ${excludeQueryBuf.toString()}"
        //                }
        //                if(query != ""){
        //                    query = "${query} AND (${childrenQuery})"
        //                }
        //                else{
        //                    query = childrenQuery;
        //                }
        //            }
        //            classFilters[className] = query.toString();
        //        }
        return classFilters;
    }

    private String getClassQuery(Class currentClass, Map tempClassMap, List classPath) {
        String className = currentClass.getName();
        if (!tempClassMap.containsKey(className)) {
            return ""
        }
        String ownQuery = ""
        if (tempClassMap[className].containsKey(OWN_FILTER)) {
            if (parentClassHierarchy[currentClass].size() == 0) {
                ownQuery = tempClassMap[className][OWN_FILTER]
            }
            else {
                ownQuery = "alias:${className} AND (${tempClassMap[className][OWN_FILTER]})"
            }
        }
        List childrenHavingFilters = [];
        if (tempClassMap[className].containsKey(CHILDREN_HAVING_FILTERS)) {
            tempClassMap[className][CHILDREN_HAVING_FILTERS].each {String childClassName ->
                Class childClass = classes[childClassName];
                if (classPath == null || classPath.contains(childClass)) {
                    childrenHavingFilters.add(childClass)
                }
            }
        }
        if (childrenHavingFilters.size() == 0) {
            return ownQuery
        }
        else {
            StringBuffer queryBf = new StringBuffer("");
            StringBuffer exBf = new StringBuffer("(alias:*")
            childrenHavingFilters.each {Class childClass ->
                exBf.append(" NOT alias:${childClass.getName()}")
            }
            exBf.append(")")
            queryBf.append(exBf.toString());

            childClassHierarchy[currentClass].each {Class childClass ->
                if (childClass.getSuperclass() == currentClass) {
                    String childClassQuery = "";
                    if (classPath == null) {
                        childClassQuery = getClassQuery(childClass, tempClassMap, null);
                    }
                    else if (classPath.contains(childClass)) {
                        childClassQuery = getClassQuery(childClass, tempClassMap, classPath)
                    }
                    if (childClassQuery != "") {
                        queryBf.append(" OR (${childClassQuery})");
                    }
                }
            }
            if (ownQuery != "") {
                return "${ownQuery} AND (${queryBf.toString()})".toString()
            }
            return queryBf.toString()
        }
    }
}