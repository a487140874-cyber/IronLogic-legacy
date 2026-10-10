package com.ironlogic.modules.program.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating a SessionExerciseTemplate.
 *
 * @param exerciseId updated exercise id
 * @param orderNo updated order inside one session template
 * @param targetSets updated planned set count
 * @param targetReps updated planned reps
 * @param targetWeight updated planned weight
 * @param targetWeightUnit updated weight unit
 * @param restSeconds updated rest seconds
 * @param intensityMode updated intensity mode
 * @param progressionRuleId updated progression rule id
 * @param prescriptionJson updated prescription json
 * @param prescriptionSegments updated multi-segment prescription definition
 */
public record UpdateSessionExerciseTemplateRequest(
        /** Updated referenced exercise id. */
        @NotNull
        Long exerciseId,

        /** Updated unique order inside one session template. */
        @NotNull
        Integer orderNo,

        /** Updated planned set count. */
        @NotNull
        Integer targetSets,

        /** Updated planned reps. */
        Integer targetReps,

        /** Updated planned weight. */
        BigDecimal targetWeight,

        /** Updated weight unit. */
        @Size(max = 8)
        String targetWeightUnit,

        /** Updated rest seconds. */
        Integer restSeconds,

        /** Updated intensity mode. */
        @Size(max = 32)
        String intensityMode,

        /** Updated progression rule id. */
        Long progressionRuleId,

        /** Updated prescription json. */
        String prescriptionJson,

        /** 更新后的多段处方列表。 */
        List<@Valid SessionExercisePrescriptionSegmentRequest> prescriptionSegments
) {
}
