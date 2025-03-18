package project.mapper;

import org.apache.ibatis.annotations.*;
import project.beans.GroupJoinRequestBean;
import java.util.List;

@Mapper
public interface GroupJoinRequestMapper {

    @Insert("INSERT INTO group_join_request (request_key, group_key, user_key, request_status, request_message, request_date) " +
            "VALUES (group_join_request_seq.nextval, #{group_key}, #{user_key}, #{request_status}, #{request_message}, SYSDATE)")
    @Options(useGeneratedKeys = true, keyProperty = "request_key", keyColumn = "request_key")
    void createJoinRequest(GroupJoinRequestBean requestBean);

    @Select("SELECT gjr.*, u.user_name, u.user_nickname, u.user_image, tg.room_name " +
            "FROM group_join_request gjr " +
            "JOIN users u ON gjr.user_key = u.user_key " +
            "JOIN teach_group tg ON gjr.group_key = tg.group_key " +
            "WHERE gjr.group_key = #{groupKey} AND gjr.request_status = 'pending' " +
            "ORDER BY gjr.request_date DESC")
    List<GroupJoinRequestBean> getPendingRequestsByGroupKey(int groupKey);

    @Update("UPDATE group_join_request SET request_status = 'approved', response_date = SYSDATE " +
            "WHERE request_key = #{requestKey}")
    void approveRequest(int requestKey);

    @Update("UPDATE group_join_request SET request_status = 'rejected', reject_reason = #{rejectReason}, response_date = SYSDATE " +
            "WHERE request_key = #{requestKey}")
    void rejectRequest(@Param("requestKey") int requestKey, @Param("rejectReason") String rejectReason);

    @Select("SELECT COUNT(*) FROM group_join_request WHERE group_key = #{groupKey} AND request_status = 'pending'")
    int countPendingRequestsByGroupKey(int groupKey);
}