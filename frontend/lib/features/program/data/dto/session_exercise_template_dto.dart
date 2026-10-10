import '../../domain/model/session_exercise_template.dart';
import 'session_exercise_prescription_segment_dto.dart';

/// SessionExerciseTemplate DTO。
///
/// 该类属于 `program/data`，用于和后端 `SessionExerciseTemplateResponse` 对齐。
class SessionExerciseTemplateDto {
  /// 创建 SessionExerciseTemplate DTO。
  const SessionExerciseTemplateDto({
    required this.id,
    required this.sessionTemplateId,
    required this.exerciseId,
    required this.orderNo,
    required this.targetSets,
    this.targetReps,
    this.targetWeight,
    this.targetWeightUnit,
    this.restSeconds,
    this.intensityMode,
    this.progressionRuleId,
    this.prescriptionJson,
    this.prescriptionSegments = const <SessionExercisePrescriptionSegmentDto>[],
    this.createdAt,
    this.updatedAt,
  });

  /// 从 JSON 构建 DTO。
  factory SessionExerciseTemplateDto.fromJson(Map<String, dynamic> json) {
    return SessionExerciseTemplateDto(
      id: (json['id'] as num?)?.toInt() ?? 0,
      sessionTemplateId: (json['sessionTemplateId'] as num?)?.toInt() ?? 0,
      exerciseId: (json['exerciseId'] as num?)?.toInt() ?? 0,
      orderNo: (json['orderNo'] as num?)?.toInt() ?? 0,
      targetSets: (json['targetSets'] as num?)?.toInt() ?? 0,
      targetReps: (json['targetReps'] as num?)?.toInt(),
      targetWeight: (json['targetWeight'] as num?)?.toDouble(),
      targetWeightUnit: json['targetWeightUnit']?.toString(),
      restSeconds: (json['restSeconds'] as num?)?.toInt(),
      intensityMode: json['intensityMode']?.toString(),
      progressionRuleId: (json['progressionRuleId'] as num?)?.toInt(),
      prescriptionJson: json['prescriptionJson']?.toString(),
      prescriptionSegments:
          (json['prescriptionSegments'] as List<dynamic>? ?? <dynamic>[])
              .map(
                (dynamic item) => SessionExercisePrescriptionSegmentDto.fromJson(
                  item as Map<String, dynamic>,
                ),
              )
              .toList(),
      createdAt: DateTime.tryParse(json['createdAt']?.toString() ?? ''),
      updatedAt: DateTime.tryParse(json['updatedAt']?.toString() ?? ''),
    );
  }

  final int id;
  final int sessionTemplateId;
  final int exerciseId;
  final int orderNo;
  final int targetSets;
  final int? targetReps;
  final double? targetWeight;
  final String? targetWeightUnit;
  final int? restSeconds;
  final String? intensityMode;
  final int? progressionRuleId;
  final String? prescriptionJson;
  final List<SessionExercisePrescriptionSegmentDto> prescriptionSegments;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  /// 转为领域模型。
  SessionExerciseTemplate toDomain() {
    return SessionExerciseTemplate(
      id: id,
      sessionTemplateId: sessionTemplateId,
      exerciseId: exerciseId,
      orderNo: orderNo,
      targetSets: targetSets,
      targetReps: targetReps,
      targetWeight: targetWeight,
      targetWeightUnit: targetWeightUnit,
      restSeconds: restSeconds,
      intensityMode: intensityMode,
      progressionRuleId: progressionRuleId,
      prescriptionJson: prescriptionJson,
      prescriptionSegments: prescriptionSegments
          .map((SessionExercisePrescriptionSegmentDto item) => item.toDomain())
          .toList(),
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}
