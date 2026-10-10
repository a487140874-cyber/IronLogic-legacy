package com.ironlogic.modules.program.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ironlogic.common.exception.BusinessException;
import com.ironlogic.modules.exercise.domain.model.Exercise;
import com.ironlogic.modules.exercise.domain.repository.ExerciseRepository;
import com.ironlogic.modules.program.domain.model.Program;
import com.ironlogic.modules.program.domain.model.ProgramBlock;
import com.ironlogic.modules.program.domain.model.SessionExercisePrescriptionSegment;
import com.ironlogic.modules.program.domain.model.SessionExerciseTemplate;
import com.ironlogic.modules.program.domain.model.SessionTemplate;
import com.ironlogic.modules.program.domain.repository.ProgramBlockRepository;
import com.ironlogic.modules.program.domain.repository.ProgramRepository;
import com.ironlogic.modules.program.domain.repository.SessionExercisePrescriptionSegmentRepository;
import com.ironlogic.modules.program.domain.repository.SessionExerciseTemplateRepository;
import com.ironlogic.modules.program.domain.repository.SessionTemplateRepository;
import com.ironlogic.modules.program.dto.CopyProgramBlockRequest;
import com.ironlogic.modules.program.dto.CreateProgramRequest;
import com.ironlogic.modules.program.dto.CreateSessionExerciseTemplateRequest;
import com.ironlogic.modules.program.dto.ProgramBlockResponse;
import com.ironlogic.modules.program.dto.ProgramResponse;
import com.ironlogic.modules.program.dto.SessionExercisePrescriptionSegmentRequest;
import com.ironlogic.modules.program.dto.SessionExerciseTemplateResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link ProgramApplicationServiceImpl} 的聚焦单元测试。
 *
 * <p>这些测试覆盖本轮最关键的两类能力：
 * 1. Program 模板复制
 * 2. 模板动作多段处方
 */
@ExtendWith(MockitoExtension.class)
class ProgramApplicationServiceImplTest {

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ProgramBlockRepository programBlockRepository;

    @Mock
    private SessionTemplateRepository sessionTemplateRepository;

    @Mock
    private SessionExerciseTemplateRepository sessionExerciseTemplateRepository;

    @Mock
    private SessionExercisePrescriptionSegmentRepository segmentRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ProgramApplicationServiceImpl programApplicationService;

    /** 验证创建 Program 时会正确绑定到当前用户。 */
    @Test
    void shouldCreateProgramForCurrentUser() {
        CreateProgramRequest request = new CreateProgramRequest(
                "Push Pull Legs",
                "hypertrophy",
                "draft",
                "Three day rotation",
                LocalDate.of(2026, 3, 20),
                LocalDate.of(2026, 6, 20)
        );

        when(programRepository.save(any(Program.class))).thenAnswer(invocation -> {
            Program program = invocation.getArgument(0);
            return new Program(
                    100L,
                    program.userId(),
                    program.name(),
                    program.goalType(),
                    program.status(),
                    program.description(),
                    program.startDate(),
                    program.endDate(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        });

        ProgramResponse response = programApplicationService.createProgram(1L, request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Push Pull Legs");

        ArgumentCaptor<Program> captor = ArgumentCaptor.forClass(Program.class);
        verify(programRepository).save(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(1L);
        assertThat(captor.getValue().id()).isNull();
    }

    /** 验证创建模板动作时可以同时写入多个处方段。 */
    @Test
    void shouldCreateSessionExerciseTemplateWithPrescriptionSegments() {
        SessionTemplate template = new SessionTemplate(
                20L, 10L, "Day 1", 1, "SEQUENCE", null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        ProgramBlock block = new ProgramBlock(
                10L, 1L, "Week 1", "ACCUMULATION", 1, "WEEKS", 1, false, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        Program program = new Program(
                1L, 1L, "PPL", "HYPERTROPHY", "ACTIVE", null, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(sessionTemplateRepository.findById(20L)).thenReturn(Optional.of(template));
        when(programBlockRepository.findById(10L)).thenReturn(Optional.of(block));
        when(programRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(program));
        when(sessionExerciseTemplateRepository.existsBySessionTemplateIdAndOrderNo(20L, 1)).thenReturn(false);
        when(exerciseRepository.findVisibleById(1L, 99L)).thenReturn(Optional.of(new Exercise(
                99L, null, "Bench Press", "strength", "chest", null, "barbell",
                "horizontal_push", false, null, LocalDateTime.now(), LocalDateTime.now()
        )));
        when(sessionExerciseTemplateRepository.save(any(SessionExerciseTemplate.class))).thenAnswer(invocation -> {
            SessionExerciseTemplate templateExercise = invocation.getArgument(0);
            return new SessionExerciseTemplate(
                    300L,
                    templateExercise.sessionTemplateId(),
                    templateExercise.exerciseId(),
                    templateExercise.orderNo(),
                    templateExercise.targetSets(),
                    templateExercise.targetReps(),
                    templateExercise.targetWeight(),
                    templateExercise.targetWeightUnit(),
                    templateExercise.restSeconds(),
                    templateExercise.intensityMode(),
                    templateExercise.progressionRuleId(),
                    templateExercise.prescriptionJson(),
                    templateExercise.createdAt(),
                    templateExercise.updatedAt()
            );
        });
        when(segmentRepository.findBySessionExerciseTemplateIds(List.of(300L))).thenReturn(List.of(
                new SessionExercisePrescriptionSegment(
                        1L, 300L, 1, "顶组", 1, 5, 180, new BigDecimal("8.0"), null,
                        null, null, new BigDecimal("75.0"), null, null, null,
                        LocalDateTime.now(), LocalDateTime.now()
                ),
                new SessionExercisePrescriptionSegment(
                        2L, 300L, 2, "回退组", 3, 5, 150, null, null,
                        null, null, null, null, null, null,
                        LocalDateTime.now(), LocalDateTime.now()
                )
        ));

        SessionExerciseTemplateResponse response = programApplicationService.createSessionExerciseTemplate(
                1L,
                20L,
                new CreateSessionExerciseTemplateRequest(
                        99L,
                        1,
                        3,
                        5,
                        null,
                        "kg",
                        120,
                        "ABSOLUTE_WEIGHT",
                        null,
                        "{}",
                        List.of(
                                new SessionExercisePrescriptionSegmentRequest(
                                        1, "顶组", 1, 5, 180, new BigDecimal("8.0"), null,
                                        "PERCENT_1RM", null, new BigDecimal("75.0"), null, null, null
                                ),
                                new SessionExercisePrescriptionSegmentRequest(
                                        2, "回退组", 3, 5, 150, null, null,
                                        "RELATIVE_TO_PREVIOUS_SEGMENT", null, null,
                                        "PERCENT_DELTA", new BigDecimal("-10.0"), null
                                )
                        )
                )
        );

        assertThat(response.id()).isEqualTo(300L);
        assertThat(response.prescriptionSegments()).hasSize(2);

        ArgumentCaptor<List<SessionExercisePrescriptionSegment>> segmentsCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(segmentRepository).saveAll(segmentsCaptor.capture());
        assertThat(segmentsCaptor.getValue()).hasSize(2);
    }

    /** 验证复制 Block 时会深拷贝到模板和模板动作层。 */
    @Test
    void shouldCopyProgramBlockDeeply() {
        ProgramBlock sourceBlock = new ProgramBlock(
                10L, 1L, "Week 1", "ACCUMULATION", 1, "WEEKS", 1, false, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        SessionTemplate sourceTemplate = new SessionTemplate(
                20L, 10L, "Day 1", 1, "SEQUENCE", null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        SessionExerciseTemplate sourceExercise = new SessionExerciseTemplate(
                30L, 20L, 99L, 1, 3, 5, null, "kg", 120, "ABSOLUTE_WEIGHT", null, "{}",
                LocalDateTime.now(), LocalDateTime.now()
        );
        Program program = new Program(
                1L, 1L, "PPL", "HYPERTROPHY", "ACTIVE", null, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(programBlockRepository.findById(10L)).thenReturn(Optional.of(sourceBlock));
        when(programRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(program));
        when(programBlockRepository.existsByProgramIdAndSequenceNo(1L, 2)).thenReturn(false);
        when(programBlockRepository.save(any(ProgramBlock.class))).thenReturn(new ProgramBlock(
                100L, 1L, "Week 2", "ACCUMULATION", 2, "WEEKS", 1, false, null,
                LocalDateTime.now(), LocalDateTime.now()
        ));
        when(sessionTemplateRepository.findByBlockId(10L)).thenReturn(List.of(sourceTemplate));
        when(sessionTemplateRepository.existsByBlockIdAndSequenceNo(100L, 1)).thenReturn(false);
        when(sessionTemplateRepository.save(any(SessionTemplate.class))).thenReturn(new SessionTemplate(
                200L, 100L, "Day 1", 1, "SEQUENCE", null, null,
                LocalDateTime.now(), LocalDateTime.now()
        ));
        when(sessionExerciseTemplateRepository.findBySessionTemplateId(20L)).thenReturn(List.of(sourceExercise));
        when(segmentRepository.findBySessionExerciseTemplateIds(List.of(30L))).thenReturn(List.of());
        when(sessionExerciseTemplateRepository.save(any(SessionExerciseTemplate.class))).thenReturn(new SessionExerciseTemplate(
                300L, 200L, 99L, 1, 3, 5, null, "kg", 120, "ABSOLUTE_WEIGHT", null, "{}",
                LocalDateTime.now(), LocalDateTime.now()
        ));

        ProgramBlockResponse response = programApplicationService.copyProgramBlock(
                1L,
                10L,
                new CopyProgramBlockRequest("Week 2", 2, null, 0)
        );

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Week 2");
        verify(sessionTemplateRepository).save(any(SessionTemplate.class));
        verify(sessionExerciseTemplateRepository).save(any(SessionExerciseTemplate.class));
    }

    /** 验证复制 Block 时 sequenceNo 冲突会直接报错。 */
    @Test
    void shouldRejectBlockCopyWhenSequenceNoConflicts() {
        ProgramBlock sourceBlock = new ProgramBlock(
                10L, 1L, "Week 1", "ACCUMULATION", 1, "WEEKS", 1, false, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        Program program = new Program(
                1L, 1L, "PPL", "HYPERTROPHY", "ACTIVE", null, null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(programBlockRepository.findById(10L)).thenReturn(Optional.of(sourceBlock));
        when(programRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(program));
        when(programBlockRepository.existsByProgramIdAndSequenceNo(1L, 2)).thenReturn(true);

        assertThatThrownBy(() -> programApplicationService.copyProgramBlock(
                1L,
                10L,
                new CopyProgramBlockRequest("Week 2", 2, null, null)
        )).isInstanceOf(BusinessException.class)
                .hasMessage("Block sequenceNo already exists in this program");
    }
}
