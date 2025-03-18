package project.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import project.beans.board.BoardLikeBean;

@Mapper
public interface BoardLikeMapper {

    /**
     * 게시글의 좋아요 수 조회
     */
    @Select("SELECT COUNT(*) FROM board_like WHERE board_key = #{board_key}")
    int getLikeCount(int board_key);

    /**
     * 사용자의 게시글 좋아요 여부 조회
     */
    @Select("SELECT COUNT(*) FROM board_like WHERE board_key = #{param1} AND user_key = #{param2}")
    int getUserLikeStatus(int board_key, int user_key);

    /**
     * 좋아요 추가
     */
    @Insert("INSERT INTO board_like (board_key, user_key) VALUES (#{board_key}, #{user_key})")
    void addLike(BoardLikeBean boardLikeBean);

    /**
     * 좋아요 삭제
     */
    @Delete("DELETE FROM board_like WHERE board_key = #{board_key} AND user_key = #{user_key}")
    void removeLike(BoardLikeBean boardLikeBean);
}
