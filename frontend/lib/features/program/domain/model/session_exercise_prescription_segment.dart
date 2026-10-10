/// 模板动作处方段领域模型。
///
/// 该对象用于表达一个动作槽位下的单个处方段，例如“顶组 1x5”或“回退组 3x5 减 10%”。
/// 这样前端可以自然展示真实训练者熟悉的处方结构，而不是只能读一个扁平模板动作。
class SessionExercisePrescriptionSegment {
  /// 创建处方段模型。
  const SessionExercisePrescriptionSegment({
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

  /// 处方段 id。兼容旧数据时可能为空。
  final int? id;

  /// 所属模板动作 id。兼容旧数据时可能为空。
  final int? sessionExerciseTemplateId;

  /// 段顺序号。
  final int segmentOrderNo;

  /// 段标签，例如顶组、回退组。
  final String? label;

  /// 组数。
  final int sets;

  /// 次数。
  final int? reps;

  /// 休息秒数。
  final int? restSeconds;

  /// 目标 RPE。
  final double? rpe;

  /// 目标 RIR。
  final int? rir;

  /// 负荷表达方式。
  final String? loadPrescriptionType;

  /// 绝对重量。
  final double? targetWeight;

  /// 百分比数值。
  final double? percentageValue;

  /// 相对调整类型。
  final String? relativeAdjustmentType;

  /// 相对调整数值。
  final double? relativeAdjustmentValue;

  /// 备注。
  final String? notes;

  /// 创建时间。
  final DateTime? createdAt;

  /// 更新时间。
  final DateTime? updatedAt;
}
