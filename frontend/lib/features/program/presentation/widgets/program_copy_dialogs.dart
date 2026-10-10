import 'package:flutter/material.dart';

import '../../domain/model/copy_program_block_input.dart';
import '../../domain/model/copy_session_template_input.dart';

/// Program 模块复制弹窗工具。
///
/// 这里把复制 Block / SessionTemplate 的简单表单抽成独立工具函数，
/// 目的是复用最小交互，而不是现在就引入复杂的表单状态管理。
abstract final class ProgramCopyDialogs {
  /// 弹出复制 Block 表单。
  static Future<CopyProgramBlockInput?> showCopyBlockDialog(
    BuildContext context, {
    required String initialName,
    required int initialSequenceNo,
  }) async {
    final TextEditingController nameController = TextEditingController(
      text: '$initialName Copy',
    );
    final TextEditingController sequenceController = TextEditingController(
      text: '$initialSequenceNo',
    );
    final TextEditingController adjustmentController = TextEditingController(
      text: '0',
    );

    final CopyProgramBlockInput? result = await showDialog<CopyProgramBlockInput>(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: const Text('复制 Block'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              TextField(
                controller: nameController,
                decoration: const InputDecoration(labelText: '新名称'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: sequenceController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: '新顺序号'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: adjustmentController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(
                  labelText: '子模板顺序整体偏移',
                ),
              ),
            ],
          ),
          actions: <Widget>[
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('取消'),
            ),
            FilledButton(
              onPressed: () {
                final int? sequenceNo = int.tryParse(
                  sequenceController.text.trim(),
                );
                if (nameController.text.trim().isEmpty || sequenceNo == null) {
                  return;
                }
                Navigator.of(dialogContext).pop(
                  CopyProgramBlockInput(
                    newName: nameController.text.trim(),
                    newSequenceNo: sequenceNo,
                    sequenceAdjustment:
                        int.tryParse(adjustmentController.text.trim()),
                  ),
                );
              },
              child: const Text('确认复制'),
            ),
          ],
        );
      },
    );

    nameController.dispose();
    sequenceController.dispose();
    adjustmentController.dispose();
    return result;
  }

  /// 弹出复制 SessionTemplate 表单。
  static Future<CopySessionTemplateInput?> showCopySessionTemplateDialog(
    BuildContext context, {
    required String initialName,
    required int initialSequenceNo,
  }) async {
    final TextEditingController nameController = TextEditingController(
      text: '$initialName Copy',
    );
    final TextEditingController sequenceController = TextEditingController(
      text: '$initialSequenceNo',
    );

    final CopySessionTemplateInput? result =
        await showDialog<CopySessionTemplateInput>(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: const Text('复制 SessionTemplate'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              TextField(
                controller: nameController,
                decoration: const InputDecoration(labelText: '新名称'),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: sequenceController,
                keyboardType: TextInputType.number,
                decoration: const InputDecoration(labelText: '新顺序号'),
              ),
            ],
          ),
          actions: <Widget>[
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('取消'),
            ),
            FilledButton(
              onPressed: () {
                final int? sequenceNo = int.tryParse(
                  sequenceController.text.trim(),
                );
                if (nameController.text.trim().isEmpty || sequenceNo == null) {
                  return;
                }
                Navigator.of(dialogContext).pop(
                  CopySessionTemplateInput(
                    newName: nameController.text.trim(),
                    newSequenceNo: sequenceNo,
                  ),
                );
              },
              child: const Text('确认复制'),
            ),
          ],
        );
      },
    );

    nameController.dispose();
    sequenceController.dispose();
    return result;
  }
}
