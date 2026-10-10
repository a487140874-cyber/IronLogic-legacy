/// 复制 SessionTemplate 的输入模型。
class CopySessionTemplateInput {
  /// 创建复制输入。
  const CopySessionTemplateInput({
    required this.newName,
    required this.newSequenceNo,
  });

  final String newName;
  final int newSequenceNo;
}
