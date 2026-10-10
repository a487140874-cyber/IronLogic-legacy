package com.ironlogic.modules.program.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板动作处方段响应 DTO。
 *
 * @param id 处方段 id
 * @param sessionExerciseTemplateId 所属模板动作 id
 * @param segmentOrderNo 段顺序号
 * @param label 段标签
 * @param sets 计划组数
 * @param reps 计划次数
 * @param restSeconds 休息秒数
 * @param rpe 目标 RPE
 * @param rir 目标 RIR
 * @param loadPrescriptionType 负荷表达方式
 * @param targetWeight 绝对重量
 * @param percentageValue 百分比数值
 * @param relativeAdjustmentType 相对调整类型
 * @param relativeAdjustmentValue 相对调整值
 * @param notes 备注
 * @param createdAt 创建时间
 * @param updatedAt 更新时间
 */
public record SessionExercisePrescriptionSegmentResponse(
        Long id,
        Long sessionExerciseTemplateId,
        Integer segmentOrderNo,
        String label,
        Integer sets,
        Integer reps,
        Integer restSeconds,
        BigDecimal rpe,
        Integer rir,
        String loadPrescriptionType,
        BigDecimal targetWeight,
        BigDecimal percentageValue,
        String relativeAdjustmentType,
        BigDecimal relativeAdjustmentValue,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
