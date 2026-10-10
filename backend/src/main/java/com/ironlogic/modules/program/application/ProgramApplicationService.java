package com.ironlogic.modules.program.application;

import com.ironlogic.modules.program.dto.CreateProgramBlockRequest;
import com.ironlogic.modules.program.dto.CreateProgramRequest;
import com.ironlogic.modules.program.dto.CreateSessionExerciseTemplateRequest;
import com.ironlogic.modules.program.dto.CreateSessionTemplateRequest;
import com.ironlogic.modules.program.dto.CopyProgramBlockRequest;
import com.ironlogic.modules.program.dto.CopySessionTemplateRequest;
import com.ironlogic.modules.program.dto.ProgramBlockResponse;
import com.ironlogic.modules.program.dto.ProgramResponse;
import com.ironlogic.modules.program.dto.SessionExerciseTemplateResponse;
import com.ironlogic.modules.program.dto.SessionTemplateResponse;
import com.ironlogic.modules.program.dto.UpdateProgramBlockRequest;
import com.ironlogic.modules.program.dto.UpdateProgramRequest;
import com.ironlogic.modules.program.dto.UpdateSessionExerciseTemplateRequest;
import com.ironlogic.modules.program.dto.UpdateSessionTemplateRequest;
import java.util.List;

/**
 * Program 模块的应用服务接口。
 *
 * <p>在 MVP 中，Program、ProgramBlock、SessionTemplate、SessionExerciseTemplate
 * 组成一条完整的模板层级，因此这层统一承接模板定义流程，把父子关系校验和顺序规则
 * 从 Controller 中抽离出来。
 */
public interface ProgramApplicationService {

    /** 为当前用户创建 Program。 */
    ProgramResponse createProgram(Long userId, CreateProgramRequest request);

    /** 列出当前用户的 Program。 */
    List<ProgramResponse> listPrograms(Long userId);

    /** 查询一个属于当前用户的 Program。 */
    ProgramResponse getProgram(Long userId, Long programId);

    /** 更新一个属于当前用户的 Program。 */
    ProgramResponse updateProgram(Long userId, Long programId, UpdateProgramRequest request);

    /** 在指定 Program 下创建 ProgramBlock。 */
    ProgramBlockResponse createProgramBlock(Long userId, Long programId, CreateProgramBlockRequest request);

    /** 列出指定 Program 下的 ProgramBlock。 */
    List<ProgramBlockResponse> listProgramBlocks(Long userId, Long programId);

    /** 更新一个属于当前用户 Program 层级的 ProgramBlock。 */
    ProgramBlockResponse updateProgramBlock(Long userId, Long blockId, UpdateProgramBlockRequest request);

    /** 深拷贝一个 ProgramBlock 到同一个 Program 下。 */
    ProgramBlockResponse copyProgramBlock(Long userId, Long blockId, CopyProgramBlockRequest request);

    /** 在指定 ProgramBlock 下创建 SessionTemplate。 */
    SessionTemplateResponse createSessionTemplate(Long userId, Long blockId, CreateSessionTemplateRequest request);

    /** 列出指定 ProgramBlock 下的 SessionTemplate。 */
    List<SessionTemplateResponse> listSessionTemplates(Long userId, Long blockId);

    /** 更新一个属于当前用户 Program 层级的 SessionTemplate。 */
    SessionTemplateResponse updateSessionTemplate(Long userId, Long templateId, UpdateSessionTemplateRequest request);

    /** 复制一个 SessionTemplate 到同一个 Block 下。 */
    SessionTemplateResponse copySessionTemplate(Long userId, Long templateId, CopySessionTemplateRequest request);

    /** 在指定 SessionTemplate 下创建 SessionExerciseTemplate。 */
    SessionExerciseTemplateResponse createSessionExerciseTemplate(
            Long userId,
            Long sessionTemplateId,
            CreateSessionExerciseTemplateRequest request
    );

    /** 列出指定 SessionTemplate 下的 SessionExerciseTemplate。 */
    List<SessionExerciseTemplateResponse> listSessionExerciseTemplates(Long userId, Long sessionTemplateId);

    /** 更新一个属于当前用户 Program 层级的 SessionExerciseTemplate。 */
    SessionExerciseTemplateResponse updateSessionExerciseTemplate(
            Long userId,
            Long sessionExerciseTemplateId,
            UpdateSessionExerciseTemplateRequest request
    );
}
