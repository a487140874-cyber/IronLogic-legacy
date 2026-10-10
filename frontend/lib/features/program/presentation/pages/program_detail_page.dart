import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router.dart';
import '../../../../core/utils/date_utils.dart';
import '../../../../shared/widgets/error_view.dart';
import '../../../../shared/widgets/ironlogic_scaffold.dart';
import '../../../../shared/widgets/loading_view.dart';
import '../../../exercise/application/exercise_providers.dart';
import '../../../exercise/domain/model/exercise.dart';
import '../../application/program_providers.dart';
import '../../domain/model/program.dart';
import '../../domain/model/program_block.dart';
import '../widgets/block_list_section.dart';
import '../widgets/program_week_view_section.dart';

/// Program 详情页。
///
/// 本轮在原有结构视图基础上新增“周视图”，让用户既能保留工程结构感知，
/// 又能在更接近训练计划表的周 / 天视角下快速复制和启动训练。
class ProgramDetailPage extends ConsumerStatefulWidget {
  /// 创建 Program 详情页。
  const ProgramDetailPage({
    required this.programId,
    super.key,
  });

  /// Program id。
  final int programId;

  @override
  ConsumerState<ProgramDetailPage> createState() => _ProgramDetailPageState();
}

class _ProgramDetailPageState extends ConsumerState<ProgramDetailPage> {
  bool _isWeekView = true;

  @override
  Widget build(BuildContext context) {
    final AsyncValue<Program> programAsync = ref.watch(
      programDetailProvider(widget.programId),
    );
    final AsyncValue<List<ProgramBlock>> blocksAsync = ref.watch(
      programBlocksProvider(widget.programId),
    );
    final AsyncValue<List<ProgramWeekBlockData>> weekViewAsync = ref.watch(
      programWeekViewProvider(widget.programId),
    );
    final AsyncValue<List<Exercise>> exercisesAsync = ref.watch(
      exerciseListProvider,
    );

    return IronLogicScaffold(
      title: 'Program 详情',
      actions: <Widget>[
        IconButton(
          tooltip: '刷新',
          onPressed: () {
            ref.invalidate(programDetailProvider(widget.programId));
            ref.invalidate(programBlocksProvider(widget.programId));
            ref.invalidate(programWeekViewProvider(widget.programId));
            ref.invalidate(exerciseListProvider);
          },
          icon: const Icon(Icons.refresh),
        ),
      ],
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => context.go(AppRoutes.programBlockCreate(widget.programId)),
        icon: const Icon(Icons.add),
        label: const Text('创建 Block'),
      ),
      body: programAsync.when(
        loading: () => const LoadingView(message: '正在加载 Program 详情...'),
        error: (Object error, StackTrace stackTrace) {
          return ErrorView(
            message: error.toString(),
            onRetry: () {
              ref.invalidate(programDetailProvider(widget.programId));
              ref.invalidate(programBlocksProvider(widget.programId));
              ref.invalidate(programWeekViewProvider(widget.programId));
            },
          );
        },
        data: (Program program) {
          return ListView(
            padding: const EdgeInsets.all(16),
            children: <Widget>[
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(
                        program.name,
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      const SizedBox(height: 8),
                      Text('目标类型：${program.goalType}'),
                      Text('状态：${program.status}'),
                      Text('开始：${AppDateUtils.formatDateLabel(program.startDate)}'),
                      Text('结束：${AppDateUtils.formatDateLabel(program.endDate)}'),
                      const SizedBox(height: 12),
                      Text(
                        program.description?.isNotEmpty == true
                            ? program.description!
                            : '暂无描述',
                      ),
                      const SizedBox(height: 16),
                      SegmentedButton<bool>(
                        segments: const <ButtonSegment<bool>>[
                          ButtonSegment<bool>(
                            value: true,
                            label: Text('周视图'),
                            icon: Icon(Icons.calendar_view_week),
                          ),
                          ButtonSegment<bool>(
                            value: false,
                            label: Text('结构视图'),
                            icon: Icon(Icons.account_tree_outlined),
                          ),
                        ],
                        selected: <bool>{_isWeekView},
                        onSelectionChanged: (Set<bool> selected) {
                          setState(() => _isWeekView = selected.first);
                        },
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Text(
                _isWeekView ? '周 / 天视图' : 'Block 结构视图',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 12),
              if (_isWeekView)
                exercisesAsync.when(
                  loading: () => const LoadingView(message: '正在加载动作目录...'),
                  error: (Object error, StackTrace stackTrace) => ErrorView(
                    message: error.toString(),
                    onRetry: () => ref.invalidate(exerciseListProvider),
                  ),
                  data: (List<Exercise> exercises) {
                    final Map<int, String> exerciseNames = <int, String>{
                      for (final Exercise item in exercises) item.id: item.name,
                    };
                    return weekViewAsync.when(
                      loading: () => const LoadingView(message: '正在加载周视图...'),
                      error: (Object error, StackTrace stackTrace) => ErrorView(
                        message: error.toString(),
                        onRetry: () =>
                            ref.invalidate(programWeekViewProvider(widget.programId)),
                      ),
                      data: (List<ProgramWeekBlockData> weeks) {
                        return ProgramWeekViewSection(
                          programId: widget.programId,
                          weeks: weeks,
                          exerciseNameOf: (int exerciseId) =>
                              exerciseNames[exerciseId] ?? 'Exercise #$exerciseId',
                        );
                      },
                    );
                  },
                )
              else
                blocksAsync.when(
                  loading: () => const LoadingView(message: '正在加载 Block 列表...'),
                  error: (Object error, StackTrace stackTrace) {
                    return ErrorView(
                      message: error.toString(),
                      onRetry: () =>
                          ref.invalidate(programBlocksProvider(widget.programId)),
                    );
                  },
                  data: (List<ProgramBlock> blocks) {
                    return BlockListSection(
                      programId: widget.programId,
                      blocks: blocks,
                    );
                  },
                ),
            ],
          );
        },
      ),
    );
  }
}
