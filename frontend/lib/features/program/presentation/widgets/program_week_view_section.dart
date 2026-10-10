import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router.dart';
import '../../../workout/application/workout_providers.dart';
import '../../application/program_providers.dart';
import '../../domain/model/program_block.dart';
import '../../domain/model/session_template.dart';
import '../../domain/model/copy_program_block_input.dart';
import '../../domain/model/copy_session_template_input.dart';
import '../widgets/program_copy_dialogs.dart';

/// Program 周视图区块。
///
/// 该组件把原来的结构树重新组织成“周 / 天”视角：
/// - Block 视作一周或一个训练阶段卡片
/// - SessionTemplate 视作这一周里的训练日
/// 这样用户看到的更像训练计划表，而不是数据库层级。
class ProgramWeekViewSection extends ConsumerWidget {
  /// 创建周视图区块。
  const ProgramWeekViewSection({
    required this.programId,
    required this.weeks,
    required this.exerciseNameOf,
    super.key,
  });

  /// 当前 Program id。
  final int programId;

  /// 周视图数据。
  final List<ProgramWeekBlockData> weeks;

  /// 根据 exerciseId 查名称。
  final String Function(int exerciseId) exerciseNameOf;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (weeks.isEmpty) {
      return const Card(
        child: Padding(
          padding: EdgeInsets.all(16),
          child: Text('当前 Program 还没有任何周结构，请先创建 Block。'),
        ),
      );
    }

    return Column(
      children: weeks
          .map(
            (ProgramWeekBlockData week) => Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Row(
                      children: <Widget>[
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: <Widget>[
                              Text(
                                week.block.name,
                                style: Theme.of(context).textTheme.titleLarge,
                              ),
                              const SizedBox(height: 4),
                              Text(
                                '类型：${week.block.blockType} · 顺序：${week.block.sequenceNo}',
                              ),
                              Text(
                                '周期：${week.block.durationMode ?? '未设置'} ${week.block.durationValue ?? ''}',
                              ),
                            ],
                          ),
                        ),
                        IconButton(
                          tooltip: '复制当前 Block',
                          onPressed: () => _copyBlock(context, ref, week.block),
                          icon: const Icon(Icons.copy),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    if (week.days.isEmpty)
                      const Text('当前周还没有训练日模板。')
                    else
                      ...week.days.map(
                        (ProgramWeekDayData day) => Container(
                          margin: const EdgeInsets.only(bottom: 12),
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Theme.of(context).colorScheme.surfaceContainerHighest,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: <Widget>[
                              Row(
                                children: <Widget>[
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: <Widget>[
                                        Text(
                                          day.template.name,
                                          style: Theme.of(context).textTheme.titleMedium,
                                        ),
                                        const SizedBox(height: 4),
                                        Text(
                                          'Day 顺序：${day.template.sequenceNo} · 动作数：${day.exercises.length}',
                                        ),
                                        Text(
                                          day.exercises.isEmpty
                                              ? '当前没有模板动作'
                                              : '主项：${exerciseNameOf(day.exercises.first.exerciseId)}',
                                        ),
                                      ],
                                    ),
                                  ),
                                  IconButton(
                                    tooltip: '复制训练日',
                                    onPressed: () => _copySessionTemplate(
                                      context,
                                      ref,
                                      day.template,
                                    ),
                                    icon: const Icon(Icons.copy_outlined),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Wrap(
                                spacing: 8,
                                runSpacing: 8,
                                children: <Widget>[
                                  OutlinedButton(
                                    onPressed: () => context.go(
                                      AppRoutes.sessionTemplateDetail(day.template.id),
                                      extra: day.template,
                                    ),
                                    child: const Text('查看 / 编辑模板'),
                                  ),
                                  OutlinedButton(
                                    onPressed: () => context.go(
                                      AppRoutes.sessionTemplateExerciseCreate(
                                        day.template.id,
                                      ),
                                    ),
                                    child: const Text('添加模板动作'),
                                  ),
                                  FilledButton.tonal(
                                    onPressed: () => _startWorkout(
                                      context,
                                      ref,
                                      day.template.id,
                                    ),
                                    child: const Text('从模板开始训练'),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                  ],
                ),
              ),
            ),
          )
          .toList(),
    );
  }

  Future<void> _copyBlock(
    BuildContext context,
    WidgetRef ref,
    ProgramBlock block,
  ) async {
    final CopyProgramBlockInput? input =
        await ProgramCopyDialogs.showCopyBlockDialog(
      context,
      initialName: block.name,
      initialSequenceNo: block.sequenceNo + 1,
    );
    if (input == null || !context.mounted) {
      return;
    }

    final ProgramBlockCopyController controller = ref.read(
      programBlockCopyControllerProvider(block.id).notifier,
    );
    await controller.submit(input);
    final AsyncValue<void> state = ref.read(programBlockCopyControllerProvider(block.id));
    if (!context.mounted) {
      return;
    }
    state.whenOrNull(
      data: (_) {
        ref.invalidate(programBlocksProvider(programId));
        ref.invalidate(programWeekViewProvider(programId));
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Block 复制成功')),
        );
      },
      error: (Object error, StackTrace stackTrace) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(error.toString())),
        );
      },
    );
  }

  Future<void> _copySessionTemplate(
    BuildContext context,
    WidgetRef ref,
    SessionTemplate template,
  ) async {
    final CopySessionTemplateInput? input =
        await ProgramCopyDialogs.showCopySessionTemplateDialog(
      context,
      initialName: template.name,
      initialSequenceNo: template.sequenceNo + 1,
    );
    if (input == null || !context.mounted) {
      return;
    }

    final SessionTemplateCopyController controller = ref.read(
      sessionTemplateCopyControllerProvider(template.id).notifier,
    );
    await controller.submit(input);
    final AsyncValue<void> state = ref.read(
      sessionTemplateCopyControllerProvider(template.id),
    );
    if (!context.mounted) {
      return;
    }
    state.whenOrNull(
      data: (_) {
        ref.invalidate(programWeekViewProvider(programId));
        ref.invalidate(sessionTemplatesProvider(template.blockId));
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('SessionTemplate 复制成功')),
        );
      },
      error: (Object error, StackTrace stackTrace) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(error.toString())),
        );
      },
    );
  }

  Future<void> _startWorkout(
    BuildContext context,
    WidgetRef ref,
    int templateId,
  ) async {
    final StartWorkoutController controller = ref.read(
      startWorkoutControllerProvider(templateId).notifier,
    );
    try {
      final detail = await controller.start();
      if (!context.mounted) {
        return;
      }
      context.go(AppRoutes.workoutDetail(detail.id));
    } catch (error) {
      if (!context.mounted) {
        return;
      }
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(error.toString())),
      );
    }
  }
}
