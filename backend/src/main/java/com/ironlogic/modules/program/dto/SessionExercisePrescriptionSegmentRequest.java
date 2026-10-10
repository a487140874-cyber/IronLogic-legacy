package com.ironlogic.modules.program.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 模板动作处方段请求 DTO。
 *
 * <p>请求层单独拆出 segment，是为了让一个模板动作可以携带多段处方，而不破坏
 * 现有 SessionExerciseTemplate 顶层结构。
 *
 * @param segmentOrderNo 处方段顺序号
 * @param label 处方段标签
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
 * @param notes 处方段备注
 */
public record SessionExercisePrescriptionSegmentRequest(
        /** 同一模板动作下必须唯一，用于表达顶组/回退组的自然顺序。 */
        @NotNull
        Integer segmentOrderNo,

        /** 展示给训练者的标签，例如顶组、回退组。 */
        @Size(max = 64)
        String label,

        /** 当前处方段计划组数。 */
        @NotNull
        Integer sets,

        /** 当前处方段计划次数。 */
        Integer reps,

        /** 当前处方段休息秒数。 */
        Integer restSeconds,

        /** 当前处方段目标 RPE。 */
        BigDecimal rpe,

        /** 当前处方段目标 RIR。 */
        Integer rir,

        /** 负荷表达方式。 */
        @Size(max = 64)
        String loadPrescriptionType,

        /** 绝对重量模式下的目标重量。 */
        BigDecimal targetWeight,

        /** 百分比模式下的百分比数值。 */
        BigDecimal percentageValue,

        /** 相对调整类型。 */
        @Size(max = 64)
        String relativeAdjustmentType,

        /** 相对调整数值。 */
        BigDecimal relativeAdjustmentValue,

        /** 当前段备注。 */
        String notes
) {
}
