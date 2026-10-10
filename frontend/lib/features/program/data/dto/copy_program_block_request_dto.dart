import '../../domain/model/copy_program_block_input.dart';

/// Block 复制请求 DTO。
class CopyProgramBlockRequestDto {
  /// 创建 DTO。
  const CopyProgramBlockRequestDto({
    required this.newName,
    required this.newSequenceNo,
    this.copyNotes,
    this.sequenceAdjustment,
  });

  /// 从输入模型构建 DTO。
  factory CopyProgramBlockRequestDto.fromInput(CopyProgramBlockInput input) {
    return CopyProgramBlockRequestDto(
      newName: input.newName,
      newSequenceNo: input.newSequenceNo,
      copyNotes: input.copyNotes,
      sequenceAdjustment: input.sequenceAdjustment,
    );
  }

  final String newName;
  final int newSequenceNo;
  final String? copyNotes;
  final int? sequenceAdjustment;

  /// 转 JSON。
  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'newName': newName,
      'newSequenceNo': newSequenceNo,
      'copyNotes': copyNotes,
      'sequenceAdjustment': sequenceAdjustment,
    };
  }
}
