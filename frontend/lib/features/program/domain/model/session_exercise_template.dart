import 'session_exercise_prescription_segment.dart';

/// SessionExerciseTemplate 领域模型。
///
/// 该类属于 `program/domain`，表示训练模板中的一个动作处方。
/// 它仍然是模板定义，而不是某次实际训练的组记录。
class SessionExerciseTemplate {
  /// 创建 SessionExerciseTemplate 模型。
  const SessionExerciseTemplate({
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
    this.prescriptionSegments = const <SessionExercisePrescriptionSegment>[],
    this.createdAt,
    this.updatedAt,
  });

  /// 模板动作 id。
  final int id;

  /// 所属 SessionTemplate id。
  final int sessionTemplateId;

  /// 引用的 Exercise id。
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

  /// 进阶规则 id。
  final int? progressionRuleId;

  /// 预留处方 JSON。
  final String? prescriptionJson;

  /// 结构化处方段。
  ///
  /// 旧数据即使没有落库 segment，后端也会返回一个兼容用默认段，
  /// 这样前端展示层可以统一按“多段处方”来渲染。
  final List<SessionExercisePrescriptionSegment> prescriptionSegments;

  /// 创建时间。
  final DateTime? createdAt;

  /// 更新时间。
  final DateTime? updatedAt;
}
