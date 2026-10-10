package com.ironlogic.modules.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 复制 ProgramBlock 的请求 DTO。
 *
 * <p>Block 复制属于深拷贝操作，不只是复制 Block 自己，还要把下面的 SessionTemplate、
 * SessionExerciseTemplate 以及其处方段一起复制出来，所以这里需要显式指定新名称和新顺序号。
 *
 * @param newName 复制后 Block 名称
 * @param newSequenceNo 复制后 Block 顺序号
 * @param copyNotes 预留复制说明
 * @param sequenceAdjustment 对子 SessionTemplate 顺序号做整体偏移
 */
public record CopyProgramBlockRequest(
        /** 新 Block 名称。 */
        @NotBlank
        String newName,

        /** 新 Block 在 Program 内的顺序号。 */
        @NotNull
        Integer newSequenceNo,

        /** 预留复制说明，当前 MVP 只接收不做复杂落库。 */
        String copyNotes,

        /** 子模板 sequenceNo 的整体偏移量，例如把 day1/day2/day3 调成 day4/day5/day6。 */
        Integer sequenceAdjustment
) {
}
