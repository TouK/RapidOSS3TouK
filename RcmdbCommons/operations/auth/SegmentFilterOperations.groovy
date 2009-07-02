package auth

import com.ifountain.rcmdb.auth.SegmentQueryHelper

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 2, 2009
* Time: 10:57:17 AM
* To change this template use File | Settings | File Templates.
*/
class SegmentFilterOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

    def afterInsert() {
        calculateGroupFilters();
    }
    def afterUpdate(params) {
        calculateGroupFilters();
    }
    def afterDelete() {
        calculateGroupFilters();
    }

    def calculateGroupFilters() {
        def segmentGroup = null;
        if (groups.size() == 0) {
            segmentGroup = Group.get(id: groupId)
        }
        else{
            segmentGroup = groups[0]
        }
        if (segmentGroup != null) {
            SegmentQueryHelper.getInstance().calculateGroupFilters(segmentGroup.name)
        }
    }

}