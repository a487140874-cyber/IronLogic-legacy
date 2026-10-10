/// 复制 Block 的输入模型。
class CopyProgramBlockInput {
  /// 创建复制输入。
  const CopyProgramBlockInput({
    required this.newName,
    required this.newSequenceNo,
    this.copyNotes,
    this.sequenceAdjustment,
  });

  final String newName;
  final int newSequenceNo;
  final String? copyNotes;
  final int? sequenceAdjustment;
}
