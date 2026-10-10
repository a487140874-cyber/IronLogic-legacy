package com.ironlogic.modules.program.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating a SessionExerciseTemplate.
 *
 * @param exerciseId referenced exercise id
 * @param orderNo order inside one session template
 * @param targetSets planned set count
 * @param targetReps optional planned reps
 * @param targetWeight optional planned weight
 * @param targetWeightUnit optional weight unit
 * @param restSeconds optional rest seconds
 * @param intensityMode optional intensity mode
 * @param progressionRuleId optional future progression rule id
 * @param prescriptionJson optional prescription json
 * @param prescriptionSegments optional multi-segment prescription definition
 */
public record CreateSessionExerciseTemplateRequest(
        /** Referenced exercise id; must exist and be visible to current user. */
        @NotNull
        Long exerciseId,

        /** Order number must be unique inside one session template. */
        @NotNull
        Integer orderNo,

        /** Planned set count. */
        @NotNull
        Integer targetSets,

        /** Optional planned reps. */
        Integer targetReps,

        /** Optional planned weight. */
        BigDecimal targetWeight,

        /** Optional weight unit, defaults are handled by client or future service logic. */
        @Size(max = 8)
        String targetWeightUnit,

        /** Optional planned rest seconds. */
        Integer restSeconds,

        /** Optional intensity mode string. */
        @Size(max = 32)
        String intensityMode,

        /** Optional future progression rule id. */
        Long progressionRuleId,

        /** Optional prescription json. */
        String prescriptionJson,

        /** 可选多段处方。前端未传时，仍兼容旧的单段字段模式。 */
        List<@Valid SessionExercisePrescriptionSegmentRequest> prescriptionSegments
) {
}
