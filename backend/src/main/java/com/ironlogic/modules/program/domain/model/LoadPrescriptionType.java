package com.ironlogic.modules.program.domain.model;

/**
 * 模板动作处方段的负荷表达方式。
 *
 * <p>本轮不重写原有 {@link SessionExerciseTemplate}，而是在它下面补一层 segment，
 * 让一个动作槽位可以拆成顶组、回退组等多个段。这个枚举只负责说明“这一段的重量
 * 应该如何理解”，真正的推荐计算依然留给 progression 模块后续处理。
 */
public enum LoadPrescriptionType {
    ABSOLUTE_WEIGHT,
    PERCENT_1RM,
    RELATIVE_TO_PREVIOUS_SEGMENT,
    RPE_ONLY
}
