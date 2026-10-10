package com.ironlogic.modules.program.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模板动作处方段持久化对象。
 *
 * <p>该对象直接映射新增的 segment 子表，用于表达“一个模板动作下的多段处方”。
 * 顶层 SessionExerciseTemplate 仍然保留，避免推翻现有模板结构。
 */
@TableName("session_exercise_prescription_segments")
public class SessionExercisePrescriptionSegmentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("session_exercise_template_id")
    private Long sessionExerciseTemplateId;

    @TableField("segment_order_no")
    private Integer segmentOrderNo;

    private String label;

    private Integer sets;

    private Integer reps;

    @TableField("rest_seconds")
    private Integer restSeconds;

    private BigDecimal rpe;

    private Integer rir;

    @TableField("load_prescription_type")
    private String loadPrescriptionType;

    @TableField("target_weight")
    private BigDecimal targetWeight;

    @TableField("percentage_value")
    private BigDecimal percentageValue;

    @TableField("relative_adjustment_type")
    private String relativeAdjustmentType;

    @TableField("relative_adjustment_value")
    private BigDecimal relativeAdjustmentValue;

    private String notes;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionExerciseTemplateId() { return sessionExerciseTemplateId; }
    public void setSessionExerciseTemplateId(Long sessionExerciseTemplateId) { this.sessionExerciseTemplateId = sessionExerciseTemplateId; }
    public Integer getSegmentOrderNo() { return segmentOrderNo; }
    public void setSegmentOrderNo(Integer segmentOrderNo) { this.segmentOrderNo = segmentOrderNo; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public Integer getRestSeconds() { return restSeconds; }
    public void setRestSeconds(Integer restSeconds) { this.restSeconds = restSeconds; }
    public BigDecimal getRpe() { return rpe; }
    public void setRpe(BigDecimal rpe) { this.rpe = rpe; }
    public Integer getRir() { return rir; }
    public void setRir(Integer rir) { this.rir = rir; }
    public String getLoadPrescriptionType() { return loadPrescriptionType; }
    public void setLoadPrescriptionType(String loadPrescriptionType) { this.loadPrescriptionType = loadPrescriptionType; }
    public BigDecimal getTargetWeight() { return targetWeight; }
    public void setTargetWeight(BigDecimal targetWeight) { this.targetWeight = targetWeight; }
    public BigDecimal getPercentageValue() { return percentageValue; }
    public void setPercentageValue(BigDecimal percentageValue) { this.percentageValue = percentageValue; }
    public String getRelativeAdjustmentType() { return relativeAdjustmentType; }
    public void setRelativeAdjustmentType(String relativeAdjustmentType) { this.relativeAdjustmentType = relativeAdjustmentType; }
    public BigDecimal getRelativeAdjustmentValue() { return relativeAdjustmentValue; }
    public void setRelativeAdjustmentValue(BigDecimal relativeAdjustmentValue) { this.relativeAdjustmentValue = relativeAdjustmentValue; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
