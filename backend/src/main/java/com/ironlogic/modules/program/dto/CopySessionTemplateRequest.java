package com.ironlogic.modules.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 复制 SessionTemplate 的请求 DTO。
 *
 * @param newName 复制后模板名称
 * @param newSequenceNo 复制后模板顺序号
 */
public record CopySessionTemplateRequest(
        /** 新模板名称。 */
        @NotBlank
        String newName,

        /** 新模板顺序号。 */
        @NotNull
        Integer newSequenceNo
) {
}
