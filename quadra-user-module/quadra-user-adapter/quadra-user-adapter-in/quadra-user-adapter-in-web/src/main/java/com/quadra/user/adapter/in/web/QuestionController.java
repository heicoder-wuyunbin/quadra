package com.quadra.user.adapter.in.web;

import com.quadra.user.adapter.in.web.common.Result;
import com.quadra.user.adapter.in.web.context.UserContext;
import com.quadra.user.adapter.in.web.dto.QuestionRequest;
import com.quadra.user.application.port.in.QuestionUseCase;
import com.quadra.user.application.port.in.command.AddQuestionCommand;
import com.quadra.user.application.port.in.command.DisableQuestionCommand;
import com.quadra.user.application.port.in.command.UpdateQuestionCommand;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.in.dto.QuestionItemDTO;
import com.quadra.user.application.port.in.query.ListMyQuestionsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Question", description = "破冰问题接口")
@RestController
@RequestMapping("/v1/questions")
public class QuestionController {

    private final QuestionUseCase questionUseCase;
    private final ListMyQuestionsQuery listMyQuestionsQuery;

    public QuestionController(QuestionUseCase questionUseCase, ListMyQuestionsQuery listMyQuestionsQuery) {
        this.questionUseCase = questionUseCase;
        this.listMyQuestionsQuery = listMyQuestionsQuery;
    }

    @Operation(summary = "添加破冰问题", description = "每个用户最多添加3个")
    @PostMapping
    public Result<Long> addQuestion(@RequestBody QuestionRequest request) {
        Long currentUserId = UserContext.getUserId();
        Long questionId = questionUseCase.addQuestion(new AddQuestionCommand(currentUserId, request.question(), request.sortOrder()));
        return Result.success(questionId);
    }

    @Operation(summary = "分页查询我的破冰问题")
    @GetMapping
    public Result<PageResult<QuestionItemDTO>> listMyQuestions(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long currentUserId = UserContext.getUserId();
        PageResult<QuestionItemDTO> page = listMyQuestionsQuery.listQuestions(currentUserId, pageNo, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "更新破冰问题", description = "更新问题内容或排序")
    @PutMapping("/{id}")
    public Result<Void> updateQuestion(
            @Parameter(description = "问题ID") @PathVariable("id") Long id,
            @RequestBody QuestionRequest request) {
        Long currentUserId = UserContext.getUserId();
        questionUseCase.updateQuestion(new UpdateQuestionCommand(currentUserId, id, request.question(), request.sortOrder()));
        return Result.success();
    }

    @Operation(summary = "禁用/删除破冰问题", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public Result<Void> disableQuestion(
            @Parameter(description = "问题ID") @PathVariable("id") Long id) {
        Long currentUserId = UserContext.getUserId();
        questionUseCase.disableQuestion(new DisableQuestionCommand(currentUserId, id));
        return Result.success();
    }
}
