package com.ironlogic.modules.program.controller;

import com.ironlogic.common.api.ApiResponse;
import com.ironlogic.modules.program.application.ProgramApplicationService;
import com.ironlogic.modules.program.dto.CopySessionTemplateRequest;
import com.ironlogic.modules.program.dto.CreateSessionTemplateRequest;
import com.ironlogic.modules.program.dto.SessionTemplateResponse;
import com.ironlogic.modules.program.dto.UpdateSessionTemplateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * SessionTemplate 的 HTTP Controller。
 */
@RestController
public class SessionTemplateController {

    private final ProgramApplicationService programApplicationService;

    public SessionTemplateController(ProgramApplicationService programApplicationService) {
        this.programApplicationService = programApplicationService;
    }

    /** 列出指定 ProgramBlock 下的 SessionTemplate。 */
    @GetMapping("/api/blocks/{blockId}/session-templates")
    public ApiResponse<List<SessionTemplateResponse>> listSessionTemplates(@PathVariable Long blockId) {
        return ApiResponse.success(programApplicationService.listSessionTemplates(currentUserId(), blockId));
    }

    /** 在指定 ProgramBlock 下创建 SessionTemplate。 */
    @PostMapping("/api/blocks/{blockId}/session-templates")
    public ApiResponse<SessionTemplateResponse> createSessionTemplate(
            @PathVariable Long blockId,
            @Valid @RequestBody CreateSessionTemplateRequest request
    ) {
        return ApiResponse.success(programApplicationService.createSessionTemplate(currentUserId(), blockId, request));
    }

    /** 更新一个属于当前用户 Program 层级的 SessionTemplate。 */
    @PutMapping("/api/session-templates/{id}")
    public ApiResponse<SessionTemplateResponse> updateSessionTemplate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSessionTemplateRequest request
    ) {
        return ApiResponse.success(programApplicationService.updateSessionTemplate(currentUserId(), id, request));
    }

    /** 复制一个属于当前用户 Program 层级的 SessionTemplate。 */
    @PostMapping("/api/session-templates/{id}/copy")
    public ApiResponse<SessionTemplateResponse> copySessionTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CopySessionTemplateRequest request
    ) {
        return ApiResponse.success(programApplicationService.copySessionTemplate(currentUserId(), id, request));
    }

    private Long currentUserId() {
        // TODO: 等 auth 模块提供真实登录上下文后，替换这里的固定用户 id。
        return 1L;
    }
}
