package com.blockpage.commentservice.adaptor.web;

import com.blockpage.commentservice.adaptor.infrastructure.value.ReportType;
import com.blockpage.commentservice.adaptor.web.view.ApiResponseView;
import com.blockpage.commentservice.adaptor.web.view.CommentView;
import com.blockpage.commentservice.adaptor.web.view.ReportView;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/comments")
public class CommentController {

    @GetMapping()
    public ResponseEntity<ApiResponseView> comments(@RequestParam Long episodeId) {
        List<CommentView> commentViewList = new ArrayList<>();
        commentViewList.add(new CommentView(1L, 1L, "김태근", null, null, "명세 똑바로 하세요1", 120, 2, 3, false, false, true));
        commentViewList.add(new CommentView(1L, 2L, "백고은", null, null, "맨날 침대 다이부 하고 싶다~", 1, 225, 0, false, false, false));
        commentViewList.add(new CommentView(1L, 1L, "김태근", null, null, "굿", 12, 2, 0, false, false, false));
        commentViewList.add(new CommentView(1L, 1L, "김태근", null, null, "낫배드", 15, 2, 0, false, false, false));
        commentViewList.add(new CommentView(1L, 1L, "김태근", null, null, "배드", 1, 2, 0, false, false, false));

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseView<>(commentViewList));
    }

    @GetMapping("/report")
    public ResponseEntity<ApiResponseView> reportList(@RequestParam Long memberId) {
        List<ReportView> reportViewList = new ArrayList<>();
        reportViewList.add(new ReportView(1L, "부적절한사람1", "노잼", Date.valueOf("2023-05-06"), ReportType.ABUSE));
        reportViewList.add(new ReportView(2L, "부적절한사람2", "진짜 노잼", Date.valueOf("2023-05-06"), ReportType.ABUSE));
        reportViewList.add(new ReportView(3L, "부적절한사람3", "매우 노잼", Date.valueOf("2023-05-06"), ReportType.ABUSE));

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseView<>(reportViewList));
    }
}
