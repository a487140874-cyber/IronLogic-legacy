import '../../domain/model/session_exercise_prescription_segment.dart';

/// 模板动作处方段 DTO。
class SessionExercisePrescriptionSegmentDto {
  /// 创建 DTO。
  const SessionExercisePrescriptionSegmentDto({
    required this.segmentOrderNo,
    required this.sets,
    this.id,
    this.sessionExerciseTemplateId,
    this.label,
    this.reps,
    this.restSeconds,
    this.rpe,
    this.rir,
    this.loadPrescriptionType,
    this.targetWeight,
    this.percentageValue,
    this.relativeAdjustmentType,
    this.relativeAdjustmentValue,
    this.notes,
    this.createdAt,
    this.updatedAt,
  });

  /// 从 JSON 构建 DTO。
  factory SessionExercisePrescriptionSegmentDto.fromJson(
    Map<String, dynamic> json,
  ) {
    return SessionExercisePrescriptionSegmentDto(
      id: (json['id'] as num?)?.toInt(),
      sessionExerciseTemplateId:
          (json['sessionExerciseTemplateId'] as num?)?.toInt(),
      segmentOrderNo: (json['segmentOrderNo'] as num?)?.toInt() ?? 0,
      label: json['label']?.toString(),
      sets: (json['sets'] as num?)?.toInt() ?? 0,
      reps: (json['reps'] as num?)?.toInt(),
      restSeconds: (json['restSeconds'] as num?)?.toInt(),
      rpe: (json['rpe'] as num?)?.toDouble(),
      rir: (json['rir'] as num?)?.toInt(),
      loadPrescriptionType: json['loadPrescriptionType']?.toString(),
      targetWeight: (json['targetWeight'] as num?)?.toDouble(),
      percentageValue: (json['percentageValue'] as num?)?.toDouble(),
      relativeAdjustmentType: json['relativeAdjustmentType']?.toString(),
      relativeAdjustmentValue:
          (json['relativeAdjustmentValue'] as num?)?.toDouble(),
      notes: json['notes']?.toString(),
      createdAt: DateTime.tryParse(json['createdAt']?.toString() ?? ''),
      updatedAt: DateTime.tryParse(json['updatedAt']?.toString() ?? ''),
    );
  }

  final int? id;
  final int? sessionExerciseTemplateId;
  final int segmentOrderNo;
  final String? label;
  final int sets;
  final int? reps;
  final int? restSeconds;
  final double? rpe;
  final int? rir;
  final String? loadPrescriptionType;
  final double? targetWeight;
  final double? percentageValue;
  final String? relativeAdjustmentType;
  final double? relativeAdjustmentValue;
  final String? notes;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  /// 转成领域模型。
  SessionExercisePrescriptionSegment toDomain() {
    return SessionExercisePrescriptionSegment(
      id: id,
      sessionExerciseTemplateId: sessionExerciseTemplateId,
      segmentOrderNo: segmentOrderNo,
      label: label,
      sets: sets,
      reps: reps,
      restSeconds: restSeconds,
      rpe: rpe,
      rir: rir,
      loadPrescriptionType: loadPrescriptionType,
      targetWeight: targetWeight,
      percentageValue: percentageValue,
      relativeAdjustmentType: relativeAdjustmentType,
      relativeAdjustmentValue: relativeAdjustmentValue,
      notes: notes,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
