import '../model/copy_program_block_input.dart';
import '../model/copy_session_template_input.dart';
import '../model/create_program_input.dart';
import '../model/create_program_block_input.dart';
import '../model/create_session_exercise_template_input.dart';
import '../model/create_session_template_input.dart';
import '../model/program.dart';
import '../model/program_block.dart';
import '../model/session_exercise_template.dart';
import '../model/session_template.dart';

/// Program 仓储抽象。
///
/// 该接口属于 `program/domain`，统一定义 Program 及其模板结构的查询与创建能力。
abstract class ProgramRepository {
  /// 查询当前用户 Program 列表。
  Future<List<Program>> listPrograms();

  /// 查询单个 Program 详情。
  Future<Program> getProgram(int id);

  /// 创建 Program。
  Future<Program> createProgram(CreateProgramInput input);

  /// 查询指定 Program 下的 Block 列表。
  Future<List<ProgramBlock>> listProgramBlocks(int programId);

  /// 在指定 Program 下创建 Block。
  Future<ProgramBlock> createProgramBlock(
    int programId,
    CreateProgramBlockInput input,
  );

  /// 复制一个 Block。
  Future<ProgramBlock> copyProgramBlock(int blockId, CopyProgramBlockInput input);

  /// 查询指定 Block 下的 SessionTemplate 列表。
  Future<List<SessionTemplate>> listSessionTemplates(int blockId);

  /// 在指定 Block 下创建 SessionTemplate。
  Future<SessionTemplate> createSessionTemplate(
    int blockId,
    CreateSessionTemplateInput input,
  );

  /// 复制一个 SessionTemplate。
  Future<SessionTemplate> copySessionTemplate(
    int templateId,
    CopySessionTemplateInput input,
  );

  /// 查询指定 SessionTemplate 下的模板动作列表。
  Future<List<SessionExerciseTemplate>> listSessionExerciseTemplates(
    int templateId,
  );

  /// 在指定 SessionTemplate 下创建模板动作。
  Future<SessionExerciseTemplate> createSessionExerciseTemplate(
    int templateId,
    CreateSessionExerciseTemplateInput input,
  );
}
