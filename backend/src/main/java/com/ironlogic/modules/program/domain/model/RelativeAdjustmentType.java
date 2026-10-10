package com.ironlogic.modules.program.domain.model;

/**
 * 相对调整类型。
 *
 * <p>当负荷类型为 {@link LoadPrescriptionType#RELATIVE_TO_PREVIOUS_SEGMENT} 时，
 * 需要再说明相对前一段是“加/减固定重量”还是“加/减百分比”。
 */
public enum RelativeAdjustmentType {
    PERCENT_DELTA,
    ABSOLUTE_DELTA
}
