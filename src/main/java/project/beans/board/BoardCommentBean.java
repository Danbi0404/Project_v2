package project.beans.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCommentBean {
    private int comment_key;
    private int comment_board_key;
    private int comment_user_key;
    private String comment_text;
    private String created_date;

    // 댓글 작성자 정보 (JOIN 결과)
    private String user_name;
    private String user_nickname;
}
