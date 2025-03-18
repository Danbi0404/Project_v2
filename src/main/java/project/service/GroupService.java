package project.service;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.beans.GroupBean;
import project.beans.GroupJoinRequestBean;
import project.beans.UserBean;
import project.repository.GroupJoinRequestRepository;
import project.repository.GroupRepository;

import java.util.List;

@Service
public class GroupService {

    @Resource(name = "loginUserBean")
    private UserBean loginUserBean;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupJoinRequestRepository groupJoinRequestRepository;

    /**
     * 그룹 생성
     */
    public boolean createGroup(GroupBean groupBean) {
        try {
            // 현재 로그인한 튜터 정보 설정
            groupBean.setUser_key(loginUserBean.getUser_key());

            groupRepository.createGroup(groupBean);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 튜터의 모든 그룹 조회
     */
    public List<GroupBean> getGroupsByTutorKey(int tutorKey) {
        return groupRepository.getGroupsByTutorKey(tutorKey);
    }

    /**
     * 특정 그룹 정보 조회
     */
    public GroupBean getGroupByKey(int groupKey) {
        return groupRepository.getGroupByKey(groupKey);
    }

    /**
     * 그룹 정보 업데이트
     */
    public boolean updateGroup(GroupBean groupBean) {
        try {
            groupRepository.updateGroup(groupBean);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 그룹 삭제
     */
    public boolean deleteGroup(int groupKey) {
        try {
            groupRepository.deleteGroup(groupKey);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 튜터의 그룹 수 조회
     */
    public int countGroupsByTutorKey(int tutorKey) {
        return groupRepository.countGroupsByTutorKey(tutorKey);
    }

    /**
     * 그룹 가입 신청 목록 조회
     */
    public List<GroupJoinRequestBean> getPendingRequestsByGroupKey(int groupKey) {
        return groupJoinRequestRepository.getPendingRequestsByGroupKey(groupKey);
    }

    /**
     * 가입 신청 승인
     */
    public boolean approveJoinRequest(int requestKey) {
        try {
            groupJoinRequestRepository.approveRequest(requestKey);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 가입 신청 거부
     */
    public boolean rejectJoinRequest(int requestKey, String rejectReason) {
        try {
            groupJoinRequestRepository.rejectRequest(requestKey, rejectReason);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}