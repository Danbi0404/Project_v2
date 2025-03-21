package project.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.beans.UserBean;
import project.beans.board.BoardBean;
import project.beans.board.BoardCommentBean;
import project.beans.board.BoardInfoBean;
import project.beans.board.PageBean;
import project.service.BoardService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Resource(name = "loginUserBean")
    private UserBean loginUserBean;

    // 기본 게시판 페이지 리다이렉트
    @GetMapping("")
    public String boardMain() {
        // 기본값으로 워들리 토킹 게시판으로 리다이렉트
        return "redirect:/board/wordly-talking";
    }

    // 워들리 토킹 게시판
    @GetMapping("/wordly-talking")
    public String wordlyTalkingMain(Model model) {
        // 기본 카테고리 (자유 게시판)로 리다이렉트
        return "redirect:/board/wordly-talking/free";
    }

    // 워들리 토킹 - 카테고리 별 (페이징 추가)
    @GetMapping("/wordly-talking/{category}")
    public String wordlyTalkingCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            Model model,
            HttpServletRequest request) {

        model.addAttribute("currentMenu", "board");
        model.addAttribute("category", category);
        model.addAttribute("currentBoardType", "wordly-talking");

        // 카테고리에 따른 게시판 정보 가져오기
        BoardInfoBean boardInfoBean = boardService.getBoardInfoByType("wordly-" + category);
        model.addAttribute("boardInfo", boardInfoBean);

        // 로그인한 사용자 키 (로그인하지 않았으면 0)
        int userKey = loginUserBean.isLogin() ? loginUserBean.getUser_key() : 0;

        // 페이징 객체 생성 (현재 페이지, 한 페이지당 게시글 수, 페이지 그룹 사이즈)
        PageBean pageBean = new PageBean(page, 10, 5, 0);

        // 게시글 목록 가져오기 (페이징 적용)
        List<BoardBean> boardList = boardService.getBoardListWithPaging("wordly-" + category, userKey, pageBean);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pageBean", pageBean);

        return "board/wordly-talking/index";
    }

    // 게시글 상세 페이지 (워들리 토킹)
    @GetMapping("/wordly-talking/detail/{postId}")
    public String wordlyTalkingDetail(@PathVariable int postId, Model model) {
        model.addAttribute("currentMenu", "board");
        model.addAttribute("currentBoardType", "wordly-talking");

        // 로그인한 사용자 키 (로그인하지 않았으면 0)
        int userKey = loginUserBean.isLogin() ? loginUserBean.getUser_key() : 0;

        // 게시글 상세 정보 가져오기
        BoardBean boardBean = boardService.getBoardDetail(postId, userKey);
        model.addAttribute("boardBean", boardBean);

        // 댓글 목록 가져오기
        List<BoardCommentBean> commentList = boardService.getBoardCommentList(postId);
        model.addAttribute("commentList", commentList);

        return "board/wordly-talking/detail";
    }

    /**
     * 워들리 토킹 게시글 작성 페이지
     */
    @GetMapping("/wordly-talking/write")
    public String wordlyTalkingWriteForm(@RequestParam(required = false) String category, Model model) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        model.addAttribute("currentMenu", "board");
        model.addAttribute("category", category);

        // 게시판 정보 목록 가져오기 (워들리 토킹 관련 게시판만)
        List<BoardInfoBean> boardInfoList = boardService.getBoardInfoList();
        model.addAttribute("boardInfoList", boardInfoList);

        // 새 게시글 객체
        BoardBean boardBean = new BoardBean();
        model.addAttribute("boardBean", boardBean);

        return "board/wordly-talking/write";
    }

    /**
     * 게시글 작성 처리
     */
    @PostMapping("/wordly-talking/write_pro")
    public String wordlyTalkingWritePro(@ModelAttribute("boardBean") BoardBean boardBean) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        // 사용자 키 설정
        boardBean.setUser_key(loginUserBean.getUser_key());

        // 게시글 작성
        boardService.writeBoard(boardBean);

        // 게시판 목록 페이지로 리다이렉트
        String category = "";
        BoardInfoBean boardInfoBean = boardService.getBoardInfoByKey(boardBean.getBoard_info_key());
        if(boardInfoBean != null) {
            // 예: wordly-free에서 free만 추출
            category = boardInfoBean.getBoard_info_type().replace("wordly-", "");
        }

        return "redirect:/board/wordly-talking/" + category;
    }

    /**
     * 게시글 수정 페이지
     */
    @GetMapping("/wordly-talking/modify/{postId}")
    public String wordlyTalkingModifyForm(@PathVariable int postId, Model model) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        model.addAttribute("currentMenu", "board");

        // 게시글 정보 가져오기
        int userKey = loginUserBean.getUser_key();
        BoardBean boardBean = boardService.getBoardDetail(postId, userKey);

        // 작성자 확인
        if(boardBean.getUser_key() != userKey) {
            return "redirect:/board/wordly-talking";
        }

        model.addAttribute("boardBean", boardBean);

        return "board/wordly-talking/modify";
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("/wordly-talking/modify_pro")
    public String wordlyTalkingModifyPro(@ModelAttribute("boardBean") BoardBean boardBean) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        // 사용자 키 설정
        boardBean.setUser_key(loginUserBean.getUser_key());

        // 게시글 수정
        boardService.updateBoard(boardBean);

        return "redirect:/board/wordly-talking/detail/" + boardBean.getBoard_key();
    }

    /**
     * 게시글 삭제 처리
     */
    @GetMapping("/wordly-talking/delete/{postId}")
    public String wordlyTalkingDelete(@PathVariable int postId) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        // 게시글 정보 가져오기
        int userKey = loginUserBean.getUser_key();
        BoardBean boardBean = boardService.getBoardDetail(postId, userKey);

        // 작성자 확인
        if(boardBean.getUser_key() != userKey) {
            return "redirect:/board/wordly-talking";
        }

        // 게시글 삭제
        boardBean.setUser_key(userKey);
        boardService.deleteBoard(boardBean);

        // 게시판 카테고리 추출
        String category = boardBean.getBoard_info_type().replace("wordly-", "");

        return "redirect:/board/wordly-talking/" + category;
    }

    /**
     * 워들리 토킹 검색 결과 페이지 (페이징 추가)
     */
    @GetMapping("/wordly-talking/search")
    public String wordlyTalkingSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "free") String category,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        model.addAttribute("currentMenu", "board");
        model.addAttribute("searchQuery", q);
        model.addAttribute("category", category);
        model.addAttribute("currentBoardType", "wordly-talking");

        // 로그인한 사용자 키 (로그인하지 않았으면 0)
        int userKey = loginUserBean.isLogin() ? loginUserBean.getUser_key() : 0;

        // 페이징 객체 생성
        PageBean pageBean = new PageBean(page, 10, 5, 0);

        // 검색 결과 가져오기 (페이징 적용)
        List<BoardBean> searchResults = boardService.searchBoardWithPaging(q, "wordly-" + category, userKey, pageBean);

        model.addAttribute("boardList", searchResults);
        model.addAttribute("pageBean", pageBean);

        return "board/wordly-talking/search";
    }

    /**
     * 댓글 작성 AJAX 처리
     */
    @PostMapping("/api/comment/write")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> writeComment(@RequestBody BoardCommentBean commentBean) {

        Map<String, Object> result = new HashMap<>();

        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        // 사용자 키 설정
        commentBean.setComment_user_key(loginUserBean.getUser_key());

        try {
            // 댓글 작성
            boardService.writeComment(commentBean);

            // 댓글 목록 다시 가져오기
            List<BoardCommentBean> commentList = boardService.getBoardCommentList(commentBean.getComment_board_key());

            result.put("success", true);
            result.put("commentList", commentList);

            System.out.println(result.get("commentList"));
            System.out.println(result.get("success"));

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "댓글 작성 중 오류가 발생했습니다.");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 댓글 삭제 AJAX 처리
     */
    @PostMapping("/api/comment/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteComment(@RequestBody BoardCommentBean commentBean) {
        Map<String, Object> result = new HashMap<>();

        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        // 사용자 키 설정
        commentBean.setComment_user_key(loginUserBean.getUser_key());

        try {
            // 댓글 삭제
            boardService.deleteComment(commentBean);

            // 댓글 목록 다시 가져오기
            List<BoardCommentBean> commentList = boardService.getBoardCommentList(commentBean.getComment_board_key());

            result.put("success", true);
            result.put("commentList", commentList);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "댓글 삭제 중 오류가 발생했습니다.");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 좋아요 토글 AJAX 처리
     */
    @PostMapping("/api/like/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestParam int boardKey) {
        Map<String, Object> result = new HashMap<>();

        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        try {
            // 좋아요 토글
            boolean isLiked = boardService.toggleLike(boardKey, loginUserBean.getUser_key());

            // 좋아요 수 가져오기
            int likeCount = boardService.getBoardDetail(boardKey, loginUserBean.getUser_key()).getLike_count();

            result.put("success", true);
            result.put("isLiked", isLiked);
            result.put("likeCount", likeCount);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "좋아요 처리 중 오류가 발생했습니다.");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 대댓글 작성 AJAX 처리
     */
    @PostMapping("/api/comment/reply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> writeReply(@RequestBody BoardCommentBean commentBean) {
        Map<String, Object> result = new HashMap<>();

        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        // 사용자 키 설정
        commentBean.setComment_user_key(loginUserBean.getUser_key());

        try {
            // 대댓글 작성
            boardService.writeReply(commentBean);

            // 댓글 목록 다시 가져오기
            List<BoardCommentBean> commentList = boardService.getBoardCommentList(commentBean.getComment_board_key());

            result.put("success", true);
            result.put("commentList", commentList);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "대댓글 작성 중 오류가 발생했습니다.");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 커넥트 게시판 메인 페이지
     */
    @GetMapping("/connect")
    public String connectMain(Model model) {
        model.addAttribute("currentMenu", "board");
        return "redirect:/board/connect/tutor";
    }

    /**
     * 커넥트 카테고리별 페이지 (페이징 추가)
     */
    @GetMapping("/connect/{category}")
    public String connectCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        model.addAttribute("currentMenu", "board");
        model.addAttribute("category", category);
        model.addAttribute("currentBoardType", "connect");

        // 카테고리에 따른 게시판 정보 가져오기
        BoardInfoBean boardInfoBean = boardService.getBoardInfoByType("connect-" + category);
        model.addAttribute("boardInfo", boardInfoBean);

        // 로그인한 사용자 키 (로그인하지 않았으면 0)
        int userKey = loginUserBean.isLogin() ? loginUserBean.getUser_key() : 0;

        // 페이징 객체 생성
        PageBean pageBean = new PageBean(page, 10, 5, 0);

        // 게시글 목록 가져오기 (페이징 적용)
        List<BoardBean> boardList = boardService.getBoardListWithPaging("connect-" + category, userKey, pageBean);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pageBean", pageBean);

        return "board/connect/index";
    }

    /**
     * 커넥트 게시글 상세 페이지
     */
    @GetMapping("/connect/detail/{postId}")
    public String connectDetail(@PathVariable int postId, Model model) {
        model.addAttribute("currentMenu", "board");

        // 로그인한 사용자 키 (로그인하지 않았으면 0)
        int userKey = loginUserBean.isLogin() ? loginUserBean.getUser_key() : 0;

        // 게시글 상세 정보 가져오기
        BoardBean boardBean = boardService.getBoardDetail(postId, userKey);
        model.addAttribute("boardBean", boardBean);

        // 댓글 목록 가져오기
        List<BoardCommentBean> commentList = boardService.getBoardCommentList(postId);
        model.addAttribute("commentList", commentList);

        return "board/connect/detail";
    }

    /**
     * 커넥트 게시글 작성 페이지
     */
    @GetMapping("/connect/write")
    public String connectWriteForm(@RequestParam(required = false) String category, Model model) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        model.addAttribute("currentMenu", "board");
        model.addAttribute("category", category);

        // 게시판 정보 목록 가져오기 (커넥트 관련 게시판만)
        List<BoardInfoBean> boardInfoList = boardService.getBoardInfoList();
        model.addAttribute("boardInfoList", boardInfoList);

        // 새 게시글 객체
        BoardBean boardBean = new BoardBean();
        model.addAttribute("boardBean", boardBean);

        return "board/connect/write";
    }

    /**
     * 커넥트 게시글 작성 처리
     */
    @PostMapping("/connect/write_pro")
    public String connectWritePro(@ModelAttribute("boardBean") BoardBean boardBean) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        // 로깅 추가 - 디버깅용
        System.out.println("Connect Write Pro - 게시판 키: " + boardBean.getBoard_info_key());
        System.out.println("Connect Write Pro - 제목: " + boardBean.getBoard_title());
        System.out.println("Connect Write Pro - 내용 길이: " + (boardBean.getBoard_text() != null ? boardBean.getBoard_text().length() : 0));

        // 사용자 키 설정
        boardBean.setUser_key(loginUserBean.getUser_key());

        // 게시글 작성
        boardService.writeBoard(boardBean);

        // 게시판 정보 가져오기
        BoardInfoBean boardInfoBean = boardService.getBoardInfoByKey(boardBean.getBoard_info_key());

        // 카테고리 결정
        String category = "tutor"; // 기본값 설정

        if(boardInfoBean != null) {
            // connect- 접두사 제거
            if(boardInfoBean.getBoard_info_type().startsWith("connect-")) {
                category = boardInfoBean.getBoard_info_type().replace("connect-", "");
            }
        }

        System.out.println("Connect Write Pro - 리다이렉트 경로: /board/connect/" + category);

        return "redirect:/board/connect/" + category;
    }

    /**
     * 커넥트 게시글 수정 페이지
     */
    @GetMapping("/connect/modify/{postId}")
    public String connectModifyForm(@PathVariable int postId, Model model) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        model.addAttribute("currentMenu", "board");

        // 게시글 정보 가져오기
        int userKey = loginUserBean.getUser_key();
        BoardBean boardBean = boardService.getBoardDetail(postId, userKey);

        // 작성자 확인
        if(boardBean.getUser_key() != userKey) {
            return "redirect:/board/connect";
        }

        model.addAttribute("boardBean", boardBean);

        return "board/connect/modify";
    }

    /**
     * 커넥트 게시글 수정 처리
     */
    @PostMapping("/connect/modify_pro")
    public String connectModifyPro(@ModelAttribute("boardBean") BoardBean boardBean) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        // 사용자 키 설정
        boardBean.setUser_key(loginUserBean.getUser_key());

        // 게시글 수정
        boardService.updateBoard(boardBean);

        return "redirect:/board/connect/detail/" + boardBean.getBoard_key();
    }

    /**
     * 커넥트 게시글 삭제 처리
     */
    @GetMapping("/connect/delete/{postId}")
    public String connectDelete(@PathVariable int postId) {
        // 로그인 체크
        if(!loginUserBean.isLogin()) {
            return "redirect:/user/login";
        }

        // 게시글 정보 가져오기
        int userKey = loginUserBean.getUser_key();
        BoardBean boardBean = boardService.getBoardDetail(postId, userKey);

        // 작성자 확인
        if(boardBean.getUser_key() != userKey) {
            return "redirect:/board/connect";
        }

        // 게시글 삭제
        boardBean.setUser_key(userKey);
        boardService.deleteBoard(boardBean);

        // 게시판 카테고리 추출
        String category = boardBean.getBoard_info_type().replace("connect-", "");

        return "redirect:/board/connect/" + category;
    }

    /**
     * 커넥트 검색 결과 페이지 (페이징 추가)
     */
    @GetMapping("/connect/search")
    public String connectSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "tutor") String category,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        model.addAttribute("currentMenu", "board");
        model.addAttribute("searchQuery", q);
        model.addAttribute("category", category);
        model.addAttribute("currentBoardType", "connect");

        // 로그인한 사용자 키 (로그인하지 않았으면 0)
        int userKey = loginUserBean.isLogin() ? loginUserBean.getUser_key() : 0;

        // 페이징 객체 생성
        PageBean pageBean = new PageBean(page, 10, 5, 0);

        // 검색 결과 가져오기 (페이징 적용)
        List<BoardBean> searchResults = boardService.searchBoardWithPaging(q, "connect-" + category, userKey, pageBean);

        model.addAttribute("boardList", searchResults);
        model.addAttribute("pageBean", pageBean);

        return "board/connect/search";
    }

    /**
     * 테스트용 게시글 대량 생성 API
     * 사용 예: /board/api/generate-test-posts?type=wordly-free&count=100&userKey=1
     */
    @PostMapping("/api/generate-test-posts")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateTestPosts(
            @RequestParam String type,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "3") int userKey) {

        Map<String, Object> result = new HashMap<>();

        try {
            // board_info_key 가져오기
            BoardInfoBean boardInfoBean = boardService.getBoardInfoByType(type);
            if(boardInfoBean == null) {
                result.put("success", false);
                result.put("message", "존재하지 않는 게시판 타입입니다: " + type);
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }

            // 테스트 게시글 생성
            List<BoardBean> generatedPosts = new ArrayList<>();
            for(int i = 1; i <= count; i++) {
                BoardBean post = new BoardBean();
                post.setBoard_info_key(boardInfoBean.getBoard_info_key());
                post.setUser_key(userKey);
                post.setBoard_title("테스트 게시글 #" + i + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                post.setBoard_text("이 게시글은 페이징 테스트를 위해 자동 생성된 게시글입니다.\n\n"
                        + "게시판 타입: " + type + "\n"
                        + "생성 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n"
                        + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam auctor, nunc eget ultricies ultrices, "
                        + "nisl nisl aliquam nisl, eget aliquam nisl nisl sit amet nisl. Nullam auctor, nunc eget ultricies ultrices, "
                        + "nisl nisl aliquam nisl, eget aliquam nisl nisl sit amet nisl.");

                boardService.writeBoard(post);
                generatedPosts.add(post);
            }

            result.put("success", true);
            result.put("message", count + "개의 테스트 게시글이 생성되었습니다.");
            result.put("type", type);
            result.put("boardInfoKey", boardInfoBean.getBoard_info_key());
            result.put("count", count);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "게시글 생성 중 오류 발생: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}