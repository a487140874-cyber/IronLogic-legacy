import '../../domain/model/create_session_exercise_template_input.dart';
import 'session_exercise_prescription_segment_request_dto.dart';

/// 创建 SessionExerciseTemplate 的请求 DTO。
///
/// 该类属于 `program/data`，负责把页面输入映射到模板动作创建接口。
class CreateSessionExerciseTemplateRequestDto {
  /// 创建请求 DTO。
  const CreateSessionExerciseTemplateRequestDto({
    required this.exerciseId,
    required this.orderNo,
    required this.targetSets,
    this.targetReps,
    this.targetWeight,
    this.targetWeightUnit,
    this.restSeconds,
    this.intensityMode,
    required this.prescriptionJson,
    required this.prescriptionSegments,
  });

  /// 从领域输入构建请求 DTO。
  factory CreateSessionExerciseTemplateRequestDto.fromInput(
    CreateSessionExerciseTemplateInput input,
  ) {
    return CreateSessionExerciseTemplateRequestDto(
      exerciseId: input.exerciseId,
      orderNo: input.orderNo,
      targetSets: input.targetSets,
      targetReps: input.targetReps,
      targetWeight: input.targetWeight,
      targetWeightUnit: input.targetWeightUnit,
      restSeconds: input.restSeconds,
      intensityMode: input.intensityMode,
      prescriptionJson: input.prescriptionJson,
      prescriptionSegments: input.prescriptionSegments
          .map(SessionExercisePrescriptionSegmentRequestDto.fromInput)
          .toList(),
    );
  }

  final int exerciseId;
  final int orderNo;
  final int targetSets;
  final int? targetReps;
  final double? targetWeight;
  final String? targetWeightUnit;
  final int? restSeconds;
  final String? intensityMode;
  final String prescriptionJson;
  final List<SessionExercisePrescriptionSegmentRequestDto> prescriptionSegments;

  /// 转为 JSON。
  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'exerciseId': exerciseId,
      'orderNo': orderNo,
      'targetSets': targetSets,
      'targetReps': targetReps,
      'targetWeight': targetWeight,
      'targetWeightUnit': targetWeightUnit,
      'restSeconds': restSeconds,
      'intensityMode': intensityMode,
      'progressionRuleId': null,
      'prescriptionJson': prescriptionJson,
      'prescriptionSegments': prescriptionSegments
          .map((SessionExercisePrescriptionSegmentRequestDto item) => item.toJson())
          .toList(),
    };
  }
}
