import 'session_exercise_prescription_segment_input.dart';

/// 创建 SessionExerciseTemplate 的输入模型。
///
/// 该类属于 `program/domain`，承接模板动作创建页的表单输入。
class CreateSessionExerciseTemplateInput {
  /// 创建模板动作输入对象。
  const CreateSessionExerciseTemplateInput({
    required this.exerciseId,
    required this.orderNo,
    required this.targetSets,
    this.targetReps,
    this.targetWeight,
    this.targetWeightUnit,
    this.restSeconds,
    this.intensityMode,
    this.prescriptionJson = '{}',
    this.prescriptionSegments = const <SessionExercisePrescriptionSegmentInput>[],
  });

  /// Exercise id。
  final int exerciseId;

  /// 顺序号。
  final int orderNo;

  /// 目标组数。
  final int targetSets;

  /// 目标次数。
  final int? targetReps;

  /// 目标重量。
  final double? targetWeight;

  /// 重量单位。
  final String? targetWeightUnit;

  /// 休息秒数。
  final int? restSeconds;

  /// 强度模式。
  final String? intensityMode;

  /// 处方 JSON。
  final String prescriptionJson;

  /// 结构化多段处方。
  final List<SessionExercisePrescriptionSegmentInput> prescriptionSegments;
}
