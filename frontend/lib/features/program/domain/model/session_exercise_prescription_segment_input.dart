/// 模板动作处方段输入模型。
///
/// 该对象承接前端表单输入，再由 data 层转换成后端请求 DTO。
class SessionExercisePrescriptionSegmentInput {
  /// 创建输入对象。
  const SessionExercisePrescriptionSegmentInput({
    required this.segmentOrderNo,
    required this.sets,
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
  });

  /// 段顺序号。
  final int segmentOrderNo;

  /// 标签。
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
}
