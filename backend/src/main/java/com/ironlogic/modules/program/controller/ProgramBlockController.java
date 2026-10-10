package com.ironlogic.modules.program.controller;

import com.ironlogic.common.api.ApiResponse;
import com.ironlogic.modules.program.application.ProgramApplicationService;
import com.ironlogic.modules.program.dto.CopyProgramBlockRequest;
import com.ironlogic.modules.program.dto.CreateProgramBlockRequest;
import com.ironlogic.modules.program.dto.ProgramBlockResponse;
import com.ironlogic.modules.program.dto.UpdateProgramBlockRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProgramBlock 的 HTTP Controller。
 */
@RestController
public class ProgramBlockController {

    private final ProgramApplicationService programApplicationService;

    public ProgramBlockController(ProgramApplicationService programApplicationService) {
        this.programApplicationService = programApplicationService;
    }

    /** 列出指定 Program 下的 ProgramBlock。 */
    @GetMapping("/api/programs/{programId}/blocks")
    public ApiResponse<List<ProgramBlockResponse>> listProgramBlocks(@PathVariable Long programId) {
        return ApiResponse.success(programApplicationService.listProgramBlocks(currentUserId(), programId));
    }

    /** 在指定 Program 下创建 ProgramBlock。 */
    @PostMapping("/api/programs/{programId}/blocks")
    public ApiResponse<ProgramBlockResponse> createProgramBlock(
            @PathVariable Long programId,
            @Valid @RequestBody CreateProgramBlockRequest request
    ) {
        return ApiResponse.success(programApplicationService.createProgramBlock(currentUserId(), programId, request));
    }

    /** 更新一个属于当前用户 Program 层级的 ProgramBlock。 */
    @PutMapping("/api/blocks/{id}")
    public ApiResponse<ProgramBlockResponse> updateProgramBlock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProgramBlockRequest request
    ) {
        return ApiResponse.success(programApplicationService.updateProgramBlock(currentUserId(), id, request));
    }

    /** 深拷贝一个 Block 到同一个 Program 下。 */
    @PostMapping("/api/blocks/{id}/copy")
    public ApiResponse<ProgramBlockResponse> copyProgramBlock(
            @PathVariable Long id,
            @Valid @RequestBody CopyProgramBlockRequest request
    ) {
        return ApiResponse.success(programApplicationService.copyProgramBlock(currentUserId(), id, request));
    }

    private Long currentUserId() {
        // TODO: 等 auth 模块提供真实登录上下文后，替换这里的固定用户 id。
        return 1L;
    }
}
