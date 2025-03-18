package project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import project.beans.GroupJoinRequestBean;
import project.mapper.GroupJoinRequestMapper;

import java.util.List;

@Repository
public class GroupJoinRequestRepository {

    @Autowired
    private GroupJoinRequestMapper groupJoinRequestMapper;

    public void createJoinRequest(GroupJoinRequestBean requestBean) {
        groupJoinRequestMapper.createJoinRequest(requestBean);
    }

    public List<GroupJoinRequestBean> getPendingRequestsByGroupKey(int groupKey) {
        return groupJoinRequestMapper.getPendingRequestsByGroupKey(groupKey);
    }

    public void approveRequest(int requestKey) {
        groupJoinRequestMapper.approveRequest(requestKey);
    }

    public void rejectRequest(int requestKey, String rejectReason) {
        groupJoinRequestMapper.rejectRequest(requestKey, rejectReason);
    }

    public int countPendingRequestsByGroupKey(int groupKey) {
        return groupJoinRequestMapper.countPendingRequestsByGroupKey(groupKey);
    }
}