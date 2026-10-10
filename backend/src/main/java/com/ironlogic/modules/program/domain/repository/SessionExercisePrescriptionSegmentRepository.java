package com.ironlogic.modules.program.domain.repository;

import com.ironlogic.modules.program.domain.model.SessionExercisePrescriptionSegment;
import java.util.List;

/**
 * 模板动作处方段的仓储边界。
 *
 * <p>本轮把 segment 当作 SessionExerciseTemplate 的子表处理，因此仓储职责保持简单：
 * 按父对象查询、整批保存、整批替换。这样最符合 MVP 对“先可用、再优化”的要求。
 */
public interface SessionExercisePrescriptionSegmentRepository {

    /**
     * 列出某个模板动作下的全部处方段。
     *
     * @param sessionExerciseTemplateId 模板动作 id
     * @return 按顺序返回的处方段列表
     */
    List<SessionExercisePrescriptionSegment> findBySessionExerciseTemplateId(Long sessionExerciseTemplateId);

    /**
     * 列出多个模板动作下的全部处方段。
     *
     * @param sessionExerciseTemplateIds 模板动作 id 列表
     * @return 所有命中的处方段
     */
    List<SessionExercisePrescriptionSegment> findBySessionExerciseTemplateIds(List<Long> sessionExerciseTemplateIds);

    /**
     * 保存一批处方段。
     *
     * @param segments 待保存的处方段
     */
    void saveAll(List<SessionExercisePrescriptionSegment> segments);

    /**
     * 删除某个模板动作下已有的全部处方段。
     *
     * @param sessionExerciseTemplateId 模板动作 id
     */
    void deleteBySessionExerciseTemplateId(Long sessionExerciseTemplateId);
}
