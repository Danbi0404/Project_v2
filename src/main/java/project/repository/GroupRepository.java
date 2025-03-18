package project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import project.beans.GroupBean;
import project.mapper.GroupMapper;

import java.util.List;

@Repository
public class GroupRepository {

    @Autowired
    private GroupMapper groupMapper;

    public void createGroup(GroupBean groupBean) {
        groupMapper.createGroup(groupBean);
    }

    public List<GroupBean> getGroupsByTutorKey(int tutorKey) {
        return groupMapper.getGroupsByTutorKey(tutorKey);
    }

    public GroupBean getGroupByKey(int groupKey) {
        return groupMapper.getGroupByKey(groupKey);
    }

    public void updateGroup(GroupBean groupBean) {
        groupMapper.updateGroup(groupBean);
    }

    public void deleteGroup(int groupKey) {
        groupMapper.deleteGroup(groupKey);
    }

    public int countGroupsByTutorKey(int tutorKey) {
        return groupMapper.countGroupsByTutorKey(tutorKey);
    }
}