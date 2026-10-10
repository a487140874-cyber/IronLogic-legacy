package com.ironlogic.modules.program.application;

import com.ironlogic.common.exception.BusinessException;
import com.ironlogic.common.exception.NotFoundException;
import com.ironlogic.modules.exercise.domain.repository.ExerciseRepository;
import com.ironlogic.modules.program.domain.model.LoadPrescriptionType;
import com.ironlogic.modules.program.domain.model.Program;
import com.ironlogic.modules.program.domain.model.ProgramBlock;
import com.ironlogic.modules.program.domain.model.RelativeAdjustmentType;
import com.ironlogic.modules.program.domain.model.SessionExercisePrescriptionSegment;
import com.ironlogic.modules.program.domain.model.SessionExerciseTemplate;
import com.ironlogic.modules.program.domain.model.SessionTemplate;
import com.ironlogic.modules.program.domain.repository.ProgramBlockRepository;
import com.ironlogic.modules.program.domain.repository.ProgramRepository;
import com.ironlogic.modules.program.domain.repository.SessionExercisePrescriptionSegmentRepository;
import com.ironlogic.modules.program.domain.repository.SessionExerciseTemplateRepository;
import com.ironlogic.modules.program.domain.repository.SessionTemplateRepository;
import com.ironlogic.modules.program.dto.CopyProgramBlockRequest;
import com.ironlogic.modules.program.dto.CopySessionTemplateRequest;
import com.ironlogic.modules.program.dto.CreateProgramBlockRequest;
import com.ironlogic.modules.program.dto.CreateProgramRequest;
import com.ironlogic.modules.program.dto.CreateSessionExerciseTemplateRequest;
import com.ironlogic.modules.program.dto.CreateSessionTemplateRequest;
import com.ironlogic.modules.program.dto.ProgramBlockResponse;
import com.ironlogic.modules.program.dto.ProgramResponse;
import com.ironlogic.modules.program.dto.SessionExercisePrescriptionSegmentRequest;
import com.ironlogic.modules.program.dto.SessionExercisePrescriptionSegmentResponse;
import com.ironlogic.modules.program.dto.SessionExerciseTemplateResponse;
import com.ironlogic.modules.program.dto.SessionTemplateResponse;
import com.ironlogic.modules.program.dto.UpdateProgramBlockRequest;
import com.ironlogic.modules.program.dto.UpdateProgramRequest;
import com.ironlogic.modules.program.dto.UpdateSessionExerciseTemplateRequest;
import com.ironlogic.modules.program.dto.UpdateSessionTemplateRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Program 模块应用服务的默认实现。
 *
 * <p>这个类负责整个模板层级的编排：沿着父子链路校验归属关系，在正确作用域内校验
 * sequence/order 唯一性，并在创建模板动作前确认引用的 Exercise 已存在且当前用户可见。
 *
 * <p>本轮新增的两个重点能力都在这里落地：
 * 1. Block / SessionTemplate 的复制流程
 * 2. SessionExerciseTemplate 下的多段处方 segment
 *
 * <p>实现策略保持增量演进，不推翻已有 SessionExerciseTemplate。也就是说：
 * - 顶层模板动作对象继续保留原有字段，保障 workout / progression 旧链路仍能工作
 * - 新增的 segment 作为子对象存在
 * - 读取旧数据时，如果还没有 segment 记录，就从旧字段合成一个“虚拟默认段”
 */
@Service
public class ProgramApplicationServiceImpl implements ProgramApplicationService {

    private static final String DEFAULT_SEGMENT_LABEL = "默认段";

    private final ProgramRepository programRepository;
    private final ProgramBlockRepository programBlockRepository;
    private final SessionTemplateRepository sessionTemplateRepository;
    private final SessionExerciseTemplateRepository sessionExerciseTemplateRepository;
    private final SessionExercisePrescriptionSegmentRepository segmentRepository;
    private final ExerciseRepository exerciseRepository;

    public ProgramApplicationServiceImpl(
            ProgramRepository programRepository,
            ProgramBlockRepository programBlockRepository,
            SessionTemplateRepository sessionTemplateRepository,
            SessionExerciseTemplateRepository sessionExerciseTemplateRepository,
            SessionExercisePrescriptionSegmentRepository segmentRepository,
            ExerciseRepository exerciseRepository
    ) {
        this.programRepository = programRepository;
        this.programBlockRepository = programBlockRepository;
        this.sessionTemplateRepository = sessionTemplateRepository;
        this.sessionExerciseTemplateRepository = sessionExerciseTemplateRepository;
        this.segmentRepository = segmentRepository;
        this.exerciseRepository = exerciseRepository;
    }

    /** 为当前用户创建 Program。 */
    @Override
    @Transactional
    public ProgramResponse createProgram(Long userId, CreateProgramRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Program program = new Program(
                null,
                userId,
                normalize(request.name()),
                normalize(request.goalType()),
                normalize(request.status()),
                normalize(request.description()),
                request.startDate(),
                request.endDate(),
                now,
                now
        );
        return toProgramResponse(programRepository.save(program));
    }

    /** 列出当前用户的 Program。 */
    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> listPrograms(Long userId) {
        return programRepository.findByUserId(userId).stream()
                .map(this::toProgramResponse)
                .toList();
    }

    /** 查询一个属于当前用户的 Program。 */
    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getProgram(Long userId, Long programId) {
        return toProgramResponse(requireOwnedProgram(userId, programId));
    }

    /** 更新一个属于当前用户的 Program。 */
    @Override
    @Transactional
    public ProgramResponse updateProgram(Long userId, Long programId, UpdateProgramRequest request) {
        Program existing = requireOwnedProgram(userId, programId);
        Program updated = new Program(
                existing.id(),
                existing.userId(),
                normalize(request.name()),
                normalize(request.goalType()),
                normalize(request.status()),
                normalize(request.description()),
                request.startDate(),
                request.endDate(),
                existing.createdAt(),
                LocalDateTime.now()
        );
        return toProgramResponse(programRepository.update(updated));
    }

    /** 在指定 Program 下创建 ProgramBlock。 */
    @Override
    @Transactional
    public ProgramBlockResponse createProgramBlock(Long userId, Long programId, CreateProgramBlockRequest request) {
        requireOwnedProgram(userId, programId);

        if (programBlockRepository.existsByProgramIdAndSequenceNo(programId, request.sequenceNo())) {
            throw new BusinessException("Block sequenceNo already exists in this program");
        }

        LocalDateTime now = LocalDateTime.now();
        ProgramBlock block = new ProgramBlock(
                null,
                programId,
                normalize(request.name()),
                normalize(request.blockType()),
                request.sequenceNo(),
                normalize(request.durationMode()),
                request.durationValue(),
                defaultFalse(request.deloadEnabled()),
                normalize(request.metadataJson()),
                now,
                now
        );
        return toProgramBlockResponse(programBlockRepository.save(block));
    }

    /** 列出指定 Program 下的 ProgramBlock。 */
    @Override
    @Transactional(readOnly = true)
    public List<ProgramBlockResponse> listProgramBlocks(Long userId, Long programId) {
        requireOwnedProgram(userId, programId);
        return programBlockRepository.findByProgramId(programId).stream()
                .map(this::toProgramBlockResponse)
                .toList();
    }

    /** 更新一个属于当前用户 Program 层级的 ProgramBlock。 */
    @Override
    @Transactional
    public ProgramBlockResponse updateProgramBlock(Long userId, Long blockId, UpdateProgramBlockRequest request) {
        ProgramBlock existing = requireOwnedBlock(userId, blockId);

        if (programBlockRepository.existsByProgramIdAndSequenceNoAndIdNot(
                existing.programId(),
                request.sequenceNo(),
                existing.id()
        )) {
            throw new BusinessException("Block sequenceNo already exists in this program");
        }

        ProgramBlock updated = new ProgramBlock(
                existing.id(),
                existing.programId(),
                normalize(request.name()),
                normalize(request.blockType()),
                request.sequenceNo(),
                normalize(request.durationMode()),
                request.durationValue(),
                defaultFalse(request.deloadEnabled()),
                normalize(request.metadataJson()),
                existing.createdAt(),
                LocalDateTime.now()
        );
        return toProgramBlockResponse(programBlockRepository.update(updated));
    }

    /** 深拷贝一个 Block 到同一个 Program 下。 */
    @Override
    @Transactional
    public ProgramBlockResponse copyProgramBlock(Long userId, Long blockId, CopyProgramBlockRequest request) {
        ProgramBlock sourceBlock = requireOwnedBlock(userId, blockId);

        if (programBlockRepository.existsByProgramIdAndSequenceNo(sourceBlock.programId(), request.newSequenceNo())) {
            throw new BusinessException("Block sequenceNo already exists in this program");
        }

        LocalDateTime now = LocalDateTime.now();
        ProgramBlock copiedBlock = programBlockRepository.save(new ProgramBlock(
                null,
                sourceBlock.programId(),
                normalize(request.newName()),
                sourceBlock.blockType(),
                request.newSequenceNo(),
                sourceBlock.durationMode(),
                sourceBlock.durationValue(),
                defaultFalse(sourceBlock.deloadEnabled()),
                sourceBlock.metadataJson(),
                now,
                now
        ));

        int sequenceAdjustment = request.sequenceAdjustment() == null ? 0 : request.sequenceAdjustment();
        List<SessionTemplate> sourceTemplates = sessionTemplateRepository.findByBlockId(sourceBlock.id());
        for (SessionTemplate sourceTemplate : sourceTemplates) {
            // Block 复制必须深拷贝到底层模板动作和处方段，否则用户一改新周计划就会污染原周计划。
            copySessionTemplateTree(sourceTemplate, copiedBlock.id(), sourceTemplate.sequenceNo() + sequenceAdjustment);
        }

        return toProgramBlockResponse(copiedBlock);
    }

    /** 在指定 ProgramBlock 下创建 SessionTemplate。 */
    @Override
    @Transactional
    public SessionTemplateResponse createSessionTemplate(Long userId, Long blockId, CreateSessionTemplateRequest request) {
        requireOwnedBlock(userId, blockId);

        if (sessionTemplateRepository.existsByBlockIdAndSequenceNo(blockId, request.sequenceNo())) {
            throw new BusinessException("SessionTemplate sequenceNo already exists in this block");
        }

        LocalDateTime now = LocalDateTime.now();
        SessionTemplate template = new SessionTemplate(
                null,
                blockId,
                normalize(request.name()),
                request.sequenceNo(),
                normalize(request.triggerMode()),
                normalize(request.notes()),
                normalize(request.metadataJson()),
                now,
                now
        );
        return toSessionTemplateResponse(sessionTemplateRepository.save(template));
    }

    /** 列出指定 ProgramBlock 下的 SessionTemplate。 */
    @Override
    @Transactional(readOnly = true)
    public List<SessionTemplateResponse> listSessionTemplates(Long userId, Long blockId) {
        requireOwnedBlock(userId, blockId);
        return sessionTemplateRepository.findByBlockId(blockId).stream()
                .map(this::toSessionTemplateResponse)
                .toList();
    }

    /** 更新一个属于当前用户 Program 层级的 SessionTemplate。 */
    @Override
    @Transactional
    public SessionTemplateResponse updateSessionTemplate(Long userId, Long templateId, UpdateSessionTemplateRequest request) {
        SessionTemplate existing = requireOwnedSessionTemplate(userId, templateId);

        if (sessionTemplateRepository.existsByBlockIdAndSequenceNoAndIdNot(
                existing.blockId(),
                request.sequenceNo(),
                existing.id()
        )) {
            throw new BusinessException("SessionTemplate sequenceNo already exists in this block");
        }

        SessionTemplate updated = new SessionTemplate(
                existing.id(),
                existing.blockId(),
                normalize(request.name()),
                request.sequenceNo(),
                normalize(request.triggerMode()),
                normalize(request.notes()),
                normalize(request.metadataJson()),
                existing.createdAt(),
                LocalDateTime.now()
        );
        return toSessionTemplateResponse(sessionTemplateRepository.update(updated));
    }

    /** 复制一个 SessionTemplate 到同一个 Block 下。 */
    @Override
    @Transactional
    public SessionTemplateResponse copySessionTemplate(Long userId, Long templateId, CopySessionTemplateRequest request) {
        SessionTemplate sourceTemplate = requireOwnedSessionTemplate(userId, templateId);

        if (sessionTemplateRepository.existsByBlockIdAndSequenceNo(sourceTemplate.blockId(), request.newSequenceNo())) {
            throw new BusinessException("SessionTemplate sequenceNo already exists in this block");
        }

        SessionTemplate copiedTemplate = copySessionTemplateTree(
                sourceTemplate,
                sourceTemplate.blockId(),
                request.newSequenceNo(),
                normalize(request.newName())
        );
        return toSessionTemplateResponse(copiedTemplate);
    }

    /** 在指定 SessionTemplate 下创建 SessionExerciseTemplate。 */
    @Override
    @Transactional
    public SessionExerciseTemplateResponse createSessionExerciseTemplate(
            Long userId,
            Long sessionTemplateId,
            CreateSessionExerciseTemplateRequest request
    ) {
        requireOwnedSessionTemplate(userId, sessionTemplateId);

        if (sessionExerciseTemplateRepository.existsBySessionTemplateIdAndOrderNo(
                sessionTemplateId,
                request.orderNo()
        )) {
            throw new BusinessException("SessionExerciseTemplate orderNo already exists in this session template");
        }

        requireVisibleExercise(userId, request.exerciseId());
        validateDistinctSegmentOrderNo(request.prescriptionSegments());

        LocalDateTime now = LocalDateTime.now();
        SessionExerciseTemplate templateExercise = buildSessionExerciseTemplateForCreate(
                sessionTemplateId,
                request.exerciseId(),
                request.orderNo(),
                request.targetSets(),
                request.targetReps(),
                request.targetWeight(),
                request.targetWeightUnit(),
                request.restSeconds(),
                request.intensityMode(),
                request.progressionRuleId(),
                request.prescriptionJson(),
                request.prescriptionSegments(),
                now
        );
        SessionExerciseTemplate saved = sessionExerciseTemplateRepository.save(templateExercise);
        replaceSegments(saved, request.prescriptionSegments(), now);
        return toSessionExerciseTemplateResponse(saved, loadSegmentsMap(List.of(saved.id())));
    }

    /** 列出指定 SessionTemplate 下的 SessionExerciseTemplate。 */
    @Override
    @Transactional(readOnly = true)
    public List<SessionExerciseTemplateResponse> listSessionExerciseTemplates(Long userId, Long sessionTemplateId) {
        requireOwnedSessionTemplate(userId, sessionTemplateId);
        List<SessionExerciseTemplate> templateExercises =
                sessionExerciseTemplateRepository.findBySessionTemplateId(sessionTemplateId);
        Map<Long, List<SessionExercisePrescriptionSegment>> segmentsMap = loadSegmentsMap(
                templateExercises.stream().map(SessionExerciseTemplate::id).toList()
        );
        return templateExercises.stream()
                .map(item -> toSessionExerciseTemplateResponse(item, segmentsMap))
                .toList();
    }

    /** 更新一个属于当前用户 Program 层级的 SessionExerciseTemplate。 */
    @Override
    @Transactional
    public SessionExerciseTemplateResponse updateSessionExerciseTemplate(
            Long userId,
            Long sessionExerciseTemplateId,
            UpdateSessionExerciseTemplateRequest request
    ) {
        SessionExerciseTemplate existing = requireOwnedSessionExerciseTemplate(userId, sessionExerciseTemplateId);

        if (sessionExerciseTemplateRepository.existsBySessionTemplateIdAndOrderNoAndIdNot(
                existing.sessionTemplateId(),
                request.orderNo(),
                existing.id()
        )) {
            throw new BusinessException("SessionExerciseTemplate orderNo already exists in this session template");
        }

        requireVisibleExercise(userId, request.exerciseId());
        validateDistinctSegmentOrderNo(request.prescriptionSegments());

        LocalDateTime now = LocalDateTime.now();
        SessionExerciseTemplate updated = buildSessionExerciseTemplateForUpdate(
                existing,
                request.exerciseId(),
                request.orderNo(),
                request.targetSets(),
                request.targetReps(),
                request.targetWeight(),
                request.targetWeightUnit(),
                request.restSeconds(),
                request.intensityMode(),
                request.progressionRuleId(),
                request.prescriptionJson(),
                request.prescriptionSegments(),
                now
        );
        SessionExerciseTemplate saved = sessionExerciseTemplateRepository.update(updated);
        replaceSegments(saved, request.prescriptionSegments(), now);
        return toSessionExerciseTemplateResponse(saved, loadSegmentsMap(List.of(saved.id())));
    }

    private Program requireOwnedProgram(Long userId, Long programId) {
        return programRepository.findByIdAndUserId(programId, userId)
                .orElseThrow(() -> new NotFoundException("Program not found"));
    }

    private ProgramBlock requireOwnedBlock(Long userId, Long blockId) {
        ProgramBlock block = programBlockRepository.findById(blockId)
                .orElseThrow(() -> new NotFoundException("ProgramBlock not found"));
        requireOwnedProgram(userId, block.programId());
        return block;
    }

    private SessionTemplate requireOwnedSessionTemplate(Long userId, Long templateId) {
        SessionTemplate template = sessionTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("SessionTemplate not found"));
        requireOwnedBlock(userId, template.blockId());
        return template;
    }

    private SessionExerciseTemplate requireOwnedSessionExerciseTemplate(Long userId, Long templateExerciseId) {
        SessionExerciseTemplate templateExercise = sessionExerciseTemplateRepository.findById(templateExerciseId)
                .orElseThrow(() -> new NotFoundException("SessionExerciseTemplate not found"));
        requireOwnedSessionTemplate(userId, templateExercise.sessionTemplateId());
        return templateExercise;
    }

    private void requireVisibleExercise(Long userId, Long exerciseId) {
        if (exerciseRepository.findVisibleById(userId, exerciseId).isEmpty()) {
            throw new NotFoundException("Exercise not found");
        }
    }

    private SessionTemplate copySessionTemplateTree(
            SessionTemplate sourceTemplate,
            Long targetBlockId,
            Integer newSequenceNo
    ) {
        return copySessionTemplateTree(sourceTemplate, targetBlockId, newSequenceNo, sourceTemplate.name());
    }

    private SessionTemplate copySessionTemplateTree(
            SessionTemplate sourceTemplate,
            Long targetBlockId,
            Integer newSequenceNo,
            String newName
    ) {
        if (sessionTemplateRepository.existsByBlockIdAndSequenceNo(targetBlockId, newSequenceNo)) {
            throw new BusinessException("SessionTemplate sequenceNo already exists in this block");
        }

        LocalDateTime now = LocalDateTime.now();
        SessionTemplate copiedTemplate = sessionTemplateRepository.save(new SessionTemplate(
                null,
                targetBlockId,
                normalize(newName),
                newSequenceNo,
                sourceTemplate.triggerMode(),
                sourceTemplate.notes(),
                sourceTemplate.metadataJson(),
                now,
                now
        ));

        List<SessionExerciseTemplate> sourceExercises =
                sessionExerciseTemplateRepository.findBySessionTemplateId(sourceTemplate.id());
        Map<Long, List<SessionExercisePrescriptionSegment>> sourceSegmentsMap = loadSegmentsMap(
                sourceExercises.stream().map(SessionExerciseTemplate::id).toList()
        );

        for (SessionExerciseTemplate sourceExercise : sourceExercises) {
            // 复制模板动作时必须创建全新的记录，不能复用原 id。
            // 否则新模板和旧模板会指向同一条动作处方，任何更新都会互相污染。
            SessionExerciseTemplate copiedExercise = sessionExerciseTemplateRepository.save(new SessionExerciseTemplate(
                    null,
                    copiedTemplate.id(),
                    sourceExercise.exerciseId(),
                    sourceExercise.orderNo(),
                    sourceExercise.targetSets(),
                    sourceExercise.targetReps(),
                    sourceExercise.targetWeight(),
                    sourceExercise.targetWeightUnit(),
                    sourceExercise.restSeconds(),
                    sourceExercise.intensityMode(),
                    sourceExercise.progressionRuleId(),
                    sourceExercise.prescriptionJson(),
                    now,
                    now
            ));
            copySegments(sourceSegmentsMap.getOrDefault(sourceExercise.id(), List.of()), copiedExercise.id(), now);
        }

        return copiedTemplate;
    }

    private SessionExerciseTemplate buildSessionExerciseTemplateForCreate(
            Long sessionTemplateId,
            Long exerciseId,
            Integer orderNo,
            Integer requestTargetSets,
            Integer requestTargetReps,
            BigDecimal requestTargetWeight,
            String requestTargetWeightUnit,
            Integer requestRestSeconds,
            String requestIntensityMode,
            Long progressionRuleId,
            String prescriptionJson,
            List<SessionExercisePrescriptionSegmentRequest> segmentRequests,
            LocalDateTime now
    ) {
        SessionExercisePrescriptionSegmentRequest primarySegment = firstSegment(segmentRequests);
        return new SessionExerciseTemplate(
                null,
                sessionTemplateId,
                exerciseId,
                orderNo,
                primarySegment == null ? requestTargetSets : primarySegment.sets(),
                primarySegment == null ? requestTargetReps : primarySegment.reps(),
                primarySegment == null ? requestTargetWeight : primarySegment.targetWeight(),
                primarySegment == null ? normalize(requestTargetWeightUnit) : resolveTargetWeightUnit(requestTargetWeightUnit),
                primarySegment == null ? requestRestSeconds : primarySegment.restSeconds(),
                primarySegment == null
                        ? normalize(requestIntensityMode)
                        : normalize(primarySegment.loadPrescriptionType()),
                progressionRuleId,
                normalize(prescriptionJson),
                now,
                now
        );
    }

    private SessionExerciseTemplate buildSessionExerciseTemplateForUpdate(
            SessionExerciseTemplate existing,
            Long exerciseId,
            Integer orderNo,
            Integer requestTargetSets,
            Integer requestTargetReps,
            BigDecimal requestTargetWeight,
            String requestTargetWeightUnit,
            Integer requestRestSeconds,
            String requestIntensityMode,
            Long progressionRuleId,
            String prescriptionJson,
            List<SessionExercisePrescriptionSegmentRequest> segmentRequests,
            LocalDateTime now
    ) {
        SessionExercisePrescriptionSegmentRequest primarySegment = firstSegment(segmentRequests);
        return new SessionExerciseTemplate(
                existing.id(),
                existing.sessionTemplateId(),
                exerciseId,
                orderNo,
                primarySegment == null ? requestTargetSets : primarySegment.sets(),
                primarySegment == null ? requestTargetReps : primarySegment.reps(),
                primarySegment == null ? requestTargetWeight : primarySegment.targetWeight(),
                primarySegment == null ? normalize(requestTargetWeightUnit) : resolveTargetWeightUnit(requestTargetWeightUnit),
                primarySegment == null ? requestRestSeconds : primarySegment.restSeconds(),
                primarySegment == null
                        ? normalize(requestIntensityMode)
                        : normalize(primarySegment.loadPrescriptionType()),
                progressionRuleId,
                normalize(prescriptionJson),
                existing.createdAt(),
                now
        );
    }

    private void replaceSegments(
            SessionExerciseTemplate templateExercise,
            List<SessionExercisePrescriptionSegmentRequest> segmentRequests,
            LocalDateTime now
    ) {
        // 更新处方段时，MVP 采用“整批覆盖”策略：
        // 先删除旧段，再按最新请求重建。这样逻辑最稳定，也最适合当前仍在演进的模型。
        segmentRepository.deleteBySessionExerciseTemplateId(templateExercise.id());
        if (segmentRequests == null || segmentRequests.isEmpty()) {
            return;
        }
        List<SessionExercisePrescriptionSegment> segments = segmentRequests.stream()
                .map(request -> toSegmentDomain(templateExercise.id(), request, now))
                .toList();
        segmentRepository.saveAll(segments);
    }

    private void copySegments(
            List<SessionExercisePrescriptionSegment> sourceSegments,
            Long copiedTemplateExerciseId,
            LocalDateTime now
    ) {
        if (sourceSegments == null || sourceSegments.isEmpty()) {
            return;
        }
        List<SessionExercisePrescriptionSegment> copiedSegments = sourceSegments.stream()
                .map(segment -> new SessionExercisePrescriptionSegment(
                        null,
                        copiedTemplateExerciseId,
                        segment.segmentOrderNo(),
                        segment.label(),
                        segment.sets(),
                        segment.reps(),
                        segment.restSeconds(),
                        segment.rpe(),
                        segment.rir(),
                        segment.loadPrescriptionType(),
                        segment.targetWeight(),
                        segment.percentageValue(),
                        segment.relativeAdjustmentType(),
                        segment.relativeAdjustmentValue(),
                        segment.notes(),
                        now,
                        now
                ))
                .toList();
        segmentRepository.saveAll(copiedSegments);
    }

    private Map<Long, List<SessionExercisePrescriptionSegment>> loadSegmentsMap(List<Long> templateExerciseIds) {
        if (templateExerciseIds == null || templateExerciseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return segmentRepository.findBySessionExerciseTemplateIds(templateExerciseIds).stream()
                .collect(Collectors.groupingBy(
                        SessionExercisePrescriptionSegment::sessionExerciseTemplateId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private void validateDistinctSegmentOrderNo(List<SessionExercisePrescriptionSegmentRequest> segmentRequests) {
        if (segmentRequests == null || segmentRequests.isEmpty()) {
            return;
        }
        long distinctCount = segmentRequests.stream()
                .map(SessionExercisePrescriptionSegmentRequest::segmentOrderNo)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (distinctCount != segmentRequests.size()) {
            throw new BusinessException("Prescription segment orderNo must be unique inside one template exercise");
        }
    }

    private SessionExercisePrescriptionSegment toSegmentDomain(
            Long sessionExerciseTemplateId,
            SessionExercisePrescriptionSegmentRequest request,
            LocalDateTime now
    ) {
        return new SessionExercisePrescriptionSegment(
                null,
                sessionExerciseTemplateId,
                request.segmentOrderNo(),
                normalize(request.label()),
                request.sets(),
                request.reps(),
                request.restSeconds(),
                request.rpe(),
                request.rir(),
                parseLoadPrescriptionType(request.loadPrescriptionType()),
                request.targetWeight(),
                request.percentageValue(),
                parseRelativeAdjustmentType(request.relativeAdjustmentType()),
                request.relativeAdjustmentValue(),
                normalize(request.notes()),
                now,
                now
        );
    }

    private SessionExercisePrescriptionSegmentRequest firstSegment(
            List<SessionExercisePrescriptionSegmentRequest> segmentRequests
    ) {
        if (segmentRequests == null || segmentRequests.isEmpty()) {
            return null;
        }
        return segmentRequests.stream()
                .sorted(java.util.Comparator.comparing(SessionExercisePrescriptionSegmentRequest::segmentOrderNo))
                .findFirst()
                .orElse(null);
    }

    private LoadPrescriptionType parseLoadPrescriptionType(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : LoadPrescriptionType.valueOf(normalized);
    }

    private RelativeAdjustmentType parseRelativeAdjustmentType(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : RelativeAdjustmentType.valueOf(normalized);
    }

    private String resolveTargetWeightUnit(String requestTargetWeightUnit) {
        String normalized = normalize(requestTargetWeightUnit);
        return normalized == null ? "kg" : normalized;
    }

    private ProgramResponse toProgramResponse(Program program) {
        return new ProgramResponse(
                program.id(),
                program.userId(),
                program.name(),
                program.goalType(),
                program.status(),
                program.description(),
                program.startDate(),
                program.endDate(),
                program.createdAt(),
                program.updatedAt()
        );
    }

    private ProgramBlockResponse toProgramBlockResponse(ProgramBlock block) {
        return new ProgramBlockResponse(
                block.id(),
                block.programId(),
                block.name(),
                block.blockType(),
                block.sequenceNo(),
                block.durationMode(),
                block.durationValue(),
                block.deloadEnabled(),
                block.metadataJson(),
                block.createdAt(),
                block.updatedAt()
        );
    }

    private SessionTemplateResponse toSessionTemplateResponse(SessionTemplate template) {
        return new SessionTemplateResponse(
                template.id(),
                template.blockId(),
                template.name(),
                template.sequenceNo(),
                template.triggerMode(),
                template.notes(),
                template.metadataJson(),
                template.createdAt(),
                template.updatedAt()
        );
    }

    private SessionExerciseTemplateResponse toSessionExerciseTemplateResponse(
            SessionExerciseTemplate templateExercise,
            Map<Long, List<SessionExercisePrescriptionSegment>> segmentsMap
    ) {
        List<SessionExercisePrescriptionSegmentResponse> segmentResponses = toSegmentResponses(
                templateExercise,
                segmentsMap.getOrDefault(templateExercise.id(), List.of())
        );
        return new SessionExerciseTemplateResponse(
                templateExercise.id(),
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
                segmentResponses,
                templateExercise.createdAt(),
                templateExercise.updatedAt()
        );
    }

    private List<SessionExercisePrescriptionSegmentResponse> toSegmentResponses(
            SessionExerciseTemplate templateExercise,
            List<SessionExercisePrescriptionSegment> persistedSegments
    ) {
        if (persistedSegments != null && !persistedSegments.isEmpty()) {
            return persistedSegments.stream()
                    .map(this::toSegmentResponse)
                    .toList();
        }

        // 兼容旧数据：如果历史模板动作还没有 segment 记录，就从旧字段合成一个默认段返回。
        // 这样前端无需区分“新结构数据”和“旧结构数据”，读取链路可以统一展示。
        List<SessionExercisePrescriptionSegmentResponse> fallback = new ArrayList<>();
        fallback.add(new SessionExercisePrescriptionSegmentResponse(
                null,
                templateExercise.id(),
                1,
                DEFAULT_SEGMENT_LABEL,
                templateExercise.targetSets(),
                templateExercise.targetReps(),
                templateExercise.restSeconds(),
                null,
                null,
                resolveFallbackLoadType(templateExercise),
                templateExercise.targetWeight(),
                null,
                null,
                null,
                "旧数据兼容段",
                templateExercise.createdAt(),
                templateExercise.updatedAt()
        ));
        return fallback;
    }

    private SessionExercisePrescriptionSegmentResponse toSegmentResponse(
            SessionExercisePrescriptionSegment segment
    ) {
        return new SessionExercisePrescriptionSegmentResponse(
                segment.id(),
                segment.sessionExerciseTemplateId(),
                segment.segmentOrderNo(),
                segment.label(),
                segment.sets(),
                segment.reps(),
                segment.restSeconds(),
                segment.rpe(),
                segment.rir(),
                segment.loadPrescriptionType() == null ? null : segment.loadPrescriptionType().name(),
                segment.targetWeight(),
                segment.percentageValue(),
                segment.relativeAdjustmentType() == null ? null : segment.relativeAdjustmentType().name(),
                segment.relativeAdjustmentValue(),
                segment.notes(),
                segment.createdAt(),
                segment.updatedAt()
        );
    }

    private String resolveFallbackLoadType(SessionExerciseTemplate templateExercise) {
        return templateExercise.targetWeight() == null
                ? LoadPrescriptionType.RPE_ONLY.name()
                : LoadPrescriptionType.ABSOLUTE_WEIGHT.name();
    }

    private static Boolean defaultFalse(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
