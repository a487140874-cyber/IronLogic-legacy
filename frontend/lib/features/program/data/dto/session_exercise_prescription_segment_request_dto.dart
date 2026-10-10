import '../../domain/model/session_exercise_prescription_segment_input.dart';

/// 模板动作处方段请求 DTO。
class SessionExercisePrescriptionSegmentRequestDto {
  /// 创建 DTO。
  const SessionExercisePrescriptionSegmentRequestDto({
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

  /// 从输入模型构建 DTO。
  factory SessionExercisePrescriptionSegmentRequestDto.fromInput(
    SessionExercisePrescriptionSegmentInput input,
  ) {
    return SessionExercisePrescriptionSegmentRequestDto(
      segmentOrderNo: input.segmentOrderNo,
      label: input.label,
      sets: input.sets,
      reps: input.reps,
      restSeconds: input.restSeconds,
      rpe: input.rpe,
      rir: input.rir,
      loadPrescriptionType: input.loadPrescriptionType,
      targetWeight: input.targetWeight,
      percentageValue: input.percentageValue,
      relativeAdjustmentType: input.relativeAdjustmentType,
      relativeAdjustmentValue: input.relativeAdjustmentValue,
      notes: input.notes,
    );
  }

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

  /// 转 JSON。
  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'segmentOrderNo': segmentOrderNo,
      'label': label,
      'sets': sets,
      'reps': reps,
      'restSeconds': restSeconds,
      'rpe': rpe,
      'rir': rir,
      'loadPrescriptionType': loadPrescriptionType,
      'targetWeight': targetWeight,
      'percentageValue': percentageValue,
      'relativeAdjustmentType': relativeAdjustmentType,
      'relativeAdjustmentValue': relativeAdjustmentValue,
      'notes': notes,
    };
  }
}
