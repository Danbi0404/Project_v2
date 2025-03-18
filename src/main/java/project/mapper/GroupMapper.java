package project.mapper;

import org.apache.ibatis.annotations.*;
import project.beans.GroupBean;
import java.util.List;

@Mapper
public interface GroupMapper {

    @Insert("INSERT INTO teach_group (group_key, tutor_key, user_key, room_name, teach_language, created_date) " +
            "VALUES (teach_group_seq.nextval, #{tutor_key}, #{user_key}, #{room_name}, #{teach_language}, SYSDATE)")
    @Options(useGeneratedKeys = true, keyProperty = "group_key", keyColumn = "group_key")
    void createGroup(GroupBean groupBean);

    @Select("SELECT tg.*, u.user_name as tutor_name, " +
            "(SELECT COUNT(*) FROM teach_group WHERE tutor_key = #{tutorKey}) as member_count, " +
            "(SELECT COUNT(*) FROM group_join_request WHERE group_key = tg.group_key AND request_status = 'pending') as pending_requests " +
            "FROM teach_group tg " +
            "JOIN tutor t ON tg.tutor_key = t.tutor_key " +
            "JOIN users u ON t.user_key = u.user_key " +
            "WHERE tg.tutor_key = #{tutorKey} " +
            "ORDER BY tg.created_date DESC")
    List<GroupBean> getGroupsByTutorKey(@Param("tutorKey") int tutorKey);

    @Select("SELECT tg.*, u.user_name as tutor_name, " +
            "(SELECT COUNT(*) FROM teach_group WHERE tutor_key = tg.tutor_key) as member_count, " +
            "(SELECT COUNT(*) FROM group_join_request WHERE group_key = tg.group_key AND request_status = 'pending') as pending_requests " +
            "FROM teach_group tg " +
            "JOIN tutor t ON tg.tutor_key = t.tutor_key " +
            "JOIN users u ON t.user_key = u.user_key " +
            "WHERE tg.group_key = #{groupKey}")
    GroupBean getGroupByKey(@Param("groupKey") int groupKey);

    @Update("UPDATE teach_group SET room_name = #{room_name}, teach_language = #{teach_language} " +
            "WHERE group_key = #{group_key}")
    void updateGroup(GroupBean groupBean);

    @Delete("DELETE FROM teach_group WHERE group_key = #{groupKey}")
    void deleteGroup(int groupKey);

    @Select("SELECT COUNT(*) FROM teach_group WHERE tutor_key = #{tutorKey}")
    int countGroupsByTutorKey(int tutorKey);
}