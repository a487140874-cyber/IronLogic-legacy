import '../../domain/model/copy_session_template_input.dart';

/// SessionTemplate 复制请求 DTO。
class CopySessionTemplateRequestDto {
  /// 创建 DTO。
  const CopySessionTemplateRequestDto({
    required this.newName,
    required this.newSequenceNo,
  });

  /// 从输入模型构建 DTO。
  factory CopySessionTemplateRequestDto.fromInput(
    CopySessionTemplateInput input,
  ) {
    return CopySessionTemplateRequestDto(
      newName: input.newName,
      newSequenceNo: input.newSequenceNo,
    );
  }

  final String newName;
  final int newSequenceNo;

  /// 转 JSON。
  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'newName': newName,
      'newSequenceNo': newSequenceNo,
    };
  }
}
