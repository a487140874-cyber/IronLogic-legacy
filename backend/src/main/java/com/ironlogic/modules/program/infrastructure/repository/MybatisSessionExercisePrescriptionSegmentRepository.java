package com.ironlogic.modules.program.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ironlogic.modules.program.domain.model.LoadPrescriptionType;
import com.ironlogic.modules.program.domain.model.RelativeAdjustmentType;
import com.ironlogic.modules.program.domain.model.SessionExercisePrescriptionSegment;
import com.ironlogic.modules.program.domain.repository.SessionExercisePrescriptionSegmentRepository;
import com.ironlogic.modules.program.infrastructure.persistence.entity.SessionExercisePrescriptionSegmentEntity;
import com.ironlogic.modules.program.infrastructure.persistence.mapper.SessionExercisePrescriptionSegmentMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 模板动作处方段仓储的 MyBatis-Plus 实现。
 *
 * <p>MVP 先使用“整批删除 + 整批重建”的简单策略处理处方段，原因是 segment 仍处于
 * 结构稳定期，优先保证代码可读性和规则明确，而不是过早做复杂 diff。
 */
@Repository
public class MybatisSessionExercisePrescriptionSegmentRepository
        implements SessionExercisePrescriptionSegmentRepository {

    private final SessionExercisePrescriptionSegmentMapper segmentMapper;

    public MybatisSessionExercisePrescriptionSegmentRepository(
            SessionExercisePrescriptionSegmentMapper segmentMapper
    ) {
        this.segmentMapper = segmentMapper;
    }

    @Override
    public List<SessionExercisePrescriptionSegment> findBySessionExerciseTemplateId(Long sessionExerciseTemplateId) {
        LambdaQueryWrapper<SessionExercisePrescriptionSegmentEntity> wrapper =
                new LambdaQueryWrapper<SessionExercisePrescriptionSegmentEntity>()
                        .eq(SessionExercisePrescriptionSegmentEntity::getSessionExerciseTemplateId, sessionExerciseTemplateId)
                        .orderByAsc(SessionExercisePrescriptionSegmentEntity::getSegmentOrderNo)
                        .orderByAsc(SessionExercisePrescriptionSegmentEntity::getId);
        return segmentMapper.selectList(wrapper).stream()
                .map(MybatisSessionExercisePrescriptionSegmentRepository::toDomain)
                .toList();
    }

    @Override
    public List<SessionExercisePrescriptionSegment> findBySessionExerciseTemplateIds(List<Long> sessionExerciseTemplateIds) {
        if (sessionExerciseTemplateIds == null || sessionExerciseTemplateIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SessionExercisePrescriptionSegmentEntity> wrapper =
                new LambdaQueryWrapper<SessionExercisePrescriptionSegmentEntity>()
                        .in(SessionExercisePrescriptionSegmentEntity::getSessionExerciseTemplateId, sessionExerciseTemplateIds)
                        .orderByAsc(SessionExercisePrescriptionSegmentEntity::getSessionExerciseTemplateId)
                        .orderByAsc(SessionExercisePrescriptionSegmentEntity::getSegmentOrderNo)
                        .orderByAsc(SessionExercisePrescriptionSegmentEntity::getId);
        return segmentMapper.selectList(wrapper).stream()
                .map(MybatisSessionExercisePrescriptionSegmentRepository::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<SessionExercisePrescriptionSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            return;
        }
        for (SessionExercisePrescriptionSegment segment : segments) {
            segmentMapper.insert(toEntity(segment));
        }
    }

    @Override
    public void deleteBySessionExerciseTemplateId(Long sessionExerciseTemplateId) {
        LambdaQueryWrapper<SessionExercisePrescriptionSegmentEntity> wrapper =
                new LambdaQueryWrapper<SessionExercisePrescriptionSegmentEntity>()
                        .eq(SessionExercisePrescriptionSegmentEntity::getSessionExerciseTemplateId, sessionExerciseTemplateId);
        segmentMapper.delete(wrapper);
    }

    private static SessionExercisePrescriptionSegmentEntity toEntity(SessionExercisePrescriptionSegment segment) {
        SessionExercisePrescriptionSegmentEntity entity = new SessionExercisePrescriptionSegmentEntity();
        entity.setId(segment.id());
        entity.setSessionExerciseTemplateId(segment.sessionExerciseTemplateId());
        entity.setSegmentOrderNo(segment.segmentOrderNo());
        entity.setLabel(segment.label());
        entity.setSets(segment.sets());
        entity.setReps(segment.reps());
        entity.setRestSeconds(segment.restSeconds());
        entity.setRpe(segment.rpe());
        entity.setRir(segment.rir());
        entity.setLoadPrescriptionType(segment.loadPrescriptionType() == null ? null : segment.loadPrescriptionType().name());
        entity.setTargetWeight(segment.targetWeight());
        entity.setPercentageValue(segment.percentageValue());
        entity.setRelativeAdjustmentType(
                segment.relativeAdjustmentType() == null ? null : segment.relativeAdjustmentType().name()
        );
        entity.setRelativeAdjustmentValue(segment.relativeAdjustmentValue());
        entity.setNotes(segment.notes());
        entity.setCreatedAt(segment.createdAt());
        entity.setUpdatedAt(segment.updatedAt());
        return entity;
    }

    private static SessionExercisePrescriptionSegment toDomain(SessionExercisePrescriptionSegmentEntity entity) {
        return new SessionExercisePrescriptionSegment(
                entity.getId(),
                entity.getSessionExerciseTemplateId(),
                entity.getSegmentOrderNo(),
                entity.getLabel(),
                entity.getSets(),
                entity.getReps(),
                entity.getRestSeconds(),
                entity.getRpe(),
                entity.getRir(),
                entity.getLoadPrescriptionType() == null ? null : LoadPrescriptionType.valueOf(entity.getLoadPrescriptionType()),
                entity.getTargetWeight(),
                entity.getPercentageValue(),
                entity.getRelativeAdjustmentType() == null
                        ? null
                        : RelativeAdjustmentType.valueOf(entity.getRelativeAdjustmentType()),
                entity.getRelativeAdjustmentValue(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
