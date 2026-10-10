package com.ironlogic.modules.program.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板动作下的一个处方段。
 *
 * <p>这一层是对现有 {@link SessionExerciseTemplate} 的增量增强，而不是替换。
 * SessionExerciseTemplate 仍然表示“某个动作槽位”，而本对象表示该槽位内部的一段
 * 具体处方，例如“顶组 1x5 @ 75%1RM”或“回退组 3x5 减 10%”。
 *
 * @param id 处方段 id
 * @param sessionExerciseTemplateId 所属模板动作 id
 * @param segmentOrderNo 在同一模板动作内的顺序
 * @param label 展示标签，例如顶组、回退组
 * @param sets 该段计划组数
 * @param reps 该段计划次数
 * @param restSeconds 该段休息秒数
 * @param rpe 该段目标 RPE
 * @param rir 该段目标 RIR
 * @param loadPrescriptionType 负荷表达方式
 * @param targetWeight 绝对重量
 * @param percentageValue 百分比数值
 * @param relativeAdjustmentType 相对调整类型
 * @param relativeAdjustmentValue 相对调整值
 * @param notes 该段备注
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record SessionExercisePrescriptionSegment(
        Long id,
        Long sessionExerciseTemplateId,
        Integer segmentOrderNo,
        String label,
        Integer sets,
        Integer reps,
        Integer restSeconds,
        BigDecimal rpe,
        Integer rir,
        LoadPrescriptionType loadPrescriptionType,
        BigDecimal targetWeight,
        BigDecimal percentageValue,
        RelativeAdjustmentType relativeAdjustmentType,
        BigDecimal relativeAdjustmentValue,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
