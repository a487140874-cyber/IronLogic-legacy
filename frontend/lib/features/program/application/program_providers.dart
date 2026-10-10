import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../domain/model/copy_program_block_input.dart';
import '../domain/model/copy_session_template_input.dart';
import '../data/api/program_api.dart';
import '../data/repository/program_repository_impl.dart';
import '../domain/model/create_program_input.dart';
import '../domain/model/create_program_block_input.dart';
import '../domain/model/create_session_exercise_template_input.dart';
import '../domain/model/create_session_template_input.dart';
import '../domain/model/program.dart';
import '../domain/model/program_block.dart';
import '../domain/model/session_exercise_template.dart';
import '../domain/model/session_template.dart';
import '../domain/repository/program_repository.dart';

/// Program 仓储 Provider。
final programRepositoryProvider = Provider<ProgramRepository>((ref) {
  return ProgramRepositoryImpl(ref.watch(programApiProvider));
});

/// Program 列表 Provider。
final programListProvider = FutureProvider<List<Program>>((ref) async {
  return ref.watch(programRepositoryProvider).listPrograms();
});

/// Program 详情 Provider。
///
/// Program 详情页使用单独 Provider，是因为详情和列表的刷新时机不同，
/// 避免列表刷新把详情页状态一起打断。
final programDetailProvider = FutureProvider.family<Program, int>((ref, programId) async {
  return ref.watch(programRepositoryProvider).getProgram(programId);
});

/// Program 下 Block 列表 Provider。
final programBlocksProvider =
    FutureProvider.family<List<ProgramBlock>, int>((ref, programId) async {
  return ref.watch(programRepositoryProvider).listProgramBlocks(programId);
});

/// Block 下 SessionTemplate 列表 Provider。
final sessionTemplatesProvider =
    FutureProvider.family<List<SessionTemplate>, int>((ref, blockId) async {
  return ref.watch(programRepositoryProvider).listSessionTemplates(blockId);
});

/// SessionTemplate 下模板动作列表 Provider。
final sessionTemplateExercisesProvider =
    FutureProvider.family<List<SessionExerciseTemplate>, int>((
  ref,
  templateId,
) async {
  return ref
      .watch(programRepositoryProvider)
      .listSessionExerciseTemplates(templateId);
});

/// 周视图里的单天数据。
class ProgramWeekDayData {
  /// 创建单天数据。
  const ProgramWeekDayData({
    required this.template,
    required this.exercises,
  });

  final SessionTemplate template;
  final List<SessionExerciseTemplate> exercises;
}

/// 周视图里的单周数据。
class ProgramWeekBlockData {
  /// 创建单周数据。
  const ProgramWeekBlockData({
    required this.block,
    required this.days,
  });

  final ProgramBlock block;
  final List<ProgramWeekDayData> days;
}

/// Program 周视图数据。
///
/// 这个 Provider 属于 application 层，因为它把 Block、SessionTemplate、模板动作三层
/// 结构重新编排成更接近训练者思维的“周 / 天视图”。
final programWeekViewProvider =
    FutureProvider.family<List<ProgramWeekBlockData>, int>((ref, programId) async {
  final ProgramRepository repository = ref.watch(programRepositoryProvider);
  final List<ProgramBlock> blocks = await repository.listProgramBlocks(programId);

  final List<ProgramWeekBlockData> weeks = <ProgramWeekBlockData>[];
  for (final ProgramBlock block in blocks) {
    final List<SessionTemplate> templates = await repository.listSessionTemplates(
      block.id,
    );
    final List<ProgramWeekDayData> days = <ProgramWeekDayData>[];
    for (final SessionTemplate template in templates) {
      final List<SessionExerciseTemplate> exercises =
          await repository.listSessionExerciseTemplates(template.id);
      days.add(ProgramWeekDayData(template: template, exercises: exercises));
    }
    weeks.add(ProgramWeekBlockData(block: block, days: days));
  }

  return weeks;
});

/// 创建 Program 的状态控制器。
///
/// 该控制器属于 `program/application`，负责处理创建提交与列表刷新。
class ProgramCreateController extends AutoDisposeAsyncNotifier<void> {
  @override
  FutureOr<void> build() {}

  /// 提交创建 Program 请求。
  Future<void> submit(CreateProgramInput input) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      await ref.read(programRepositoryProvider).createProgram(input);
      ref.invalidate(programListProvider);
    });
  }
}

/// 创建 Program 控制器 Provider。
final programCreateControllerProvider =
    AutoDisposeAsyncNotifierProvider<ProgramCreateController, void>(
  ProgramCreateController.new,
);

/// 创建 ProgramBlock 的状态控制器。
class ProgramBlockCreateController extends AutoDisposeFamilyAsyncNotifier<void, int> {
  @override
  FutureOr<void> build(int arg) {}

  /// 提交创建 Block 请求。
  Future<void> submit(CreateProgramBlockInput input) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final int programId = arg;
      await ref.read(programRepositoryProvider).createProgramBlock(programId, input);
      ref.invalidate(programBlocksProvider(programId));
      ref.invalidate(programDetailProvider(programId));
    });
  }
}

/// 创建 Block 控制器 Provider。
final programBlockCreateControllerProvider =
    AutoDisposeAsyncNotifierProviderFamily<ProgramBlockCreateController, void, int>(
  ProgramBlockCreateController.new,
);

/// 复制 Block 的状态控制器。
class ProgramBlockCopyController extends AutoDisposeFamilyAsyncNotifier<void, int> {
  @override
  FutureOr<void> build(int arg) {}

  /// 提交复制请求。
  Future<void> submit(CopyProgramBlockInput input) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final int blockId = arg;
      await ref.read(programRepositoryProvider).copyProgramBlock(blockId, input);
    });
  }
}

/// 复制 Block 控制器 Provider。
final programBlockCopyControllerProvider =
    AutoDisposeAsyncNotifierProviderFamily<ProgramBlockCopyController, void, int>(
  ProgramBlockCopyController.new,
);

/// 创建 SessionTemplate 的状态控制器。
class SessionTemplateCreateController
    extends AutoDisposeFamilyAsyncNotifier<void, int> {
  @override
  FutureOr<void> build(int arg) {}

  /// 提交创建 SessionTemplate 请求。
  Future<void> submit(CreateSessionTemplateInput input) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final int blockId = arg;
      await ref.read(programRepositoryProvider).createSessionTemplate(blockId, input);
      ref.invalidate(sessionTemplatesProvider(blockId));
    });
  }
}

/// 创建 SessionTemplate 控制器 Provider。
final sessionTemplateCreateControllerProvider =
    AutoDisposeAsyncNotifierProviderFamily<SessionTemplateCreateController, void, int>(
  SessionTemplateCreateController.new,
);

/// 复制 SessionTemplate 的状态控制器。
class SessionTemplateCopyController
    extends AutoDisposeFamilyAsyncNotifier<void, int> {
  @override
  FutureOr<void> build(int arg) {}

  /// 提交复制请求。
  Future<void> submit(CopySessionTemplateInput input) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final int templateId = arg;
      await ref.read(programRepositoryProvider).copySessionTemplate(
        templateId,
        input,
      );
    });
  }
}

/// 复制 SessionTemplate 控制器 Provider。
final sessionTemplateCopyControllerProvider =
    AutoDisposeAsyncNotifierProviderFamily<SessionTemplateCopyController, void, int>(
  SessionTemplateCopyController.new,
);

/// 创建 SessionExerciseTemplate 的状态控制器。
class SessionExerciseTemplateCreateController
    extends AutoDisposeFamilyAsyncNotifier<void, int> {
  @override
  FutureOr<void> build(int arg) {}

  /// 提交创建模板动作请求。
  Future<void> submit(CreateSessionExerciseTemplateInput input) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      final int templateId = arg;
      await ref
          .read(programRepositoryProvider)
          .createSessionExerciseTemplate(templateId, input);
      ref.invalidate(sessionTemplateExercisesProvider(templateId));
    });
  }
}

/// 创建模板动作控制器 Provider。
final sessionExerciseTemplateCreateControllerProvider =
    AutoDisposeAsyncNotifierProviderFamily<
      SessionExerciseTemplateCreateController,
      void,
      int
    >(
  SessionExerciseTemplateCreateController.new,
);
