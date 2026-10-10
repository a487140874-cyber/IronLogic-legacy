import '../../domain/model/copy_program_block_input.dart';
import '../../domain/model/copy_session_template_input.dart';
import '../../domain/model/create_program_input.dart';
import '../../domain/model/create_program_block_input.dart';
import '../../domain/model/create_session_exercise_template_input.dart';
import '../../domain/model/create_session_template_input.dart';
import '../../domain/model/program.dart';
import '../../domain/model/program_block.dart';
import '../../domain/model/session_exercise_template.dart';
import '../../domain/model/session_template.dart';
import '../../domain/repository/program_repository.dart';
import '../api/program_api.dart';
import '../dto/copy_program_block_request_dto.dart';
import '../dto/copy_session_template_request_dto.dart';
import '../dto/create_program_block_request_dto.dart';
import '../dto/create_program_request_dto.dart';
import '../dto/create_session_exercise_template_request_dto.dart';
import '../dto/create_session_template_request_dto.dart';
import '../dto/program_block_dto.dart';
import '../dto/program_dto.dart';
import '../dto/session_exercise_template_dto.dart';
import '../dto/session_template_dto.dart';

/// Program 仓储实现。
///
/// 该类属于 `program/data/repository`，负责承接 Program API 并转换成领域对象。
class ProgramRepositoryImpl implements ProgramRepository {
  /// 创建仓储实现。
  const ProgramRepositoryImpl(this._api);

  final ProgramApi _api;

  @override
  Future<List<Program>> listPrograms() async {
    final List<ProgramDto> items = await _api.listPrograms();
    return items.map((ProgramDto item) => item.toDomain()).toList();
  }

  @override
  Future<Program> getProgram(int id) async {
    final ProgramDto dto = await _api.getProgram(id);
    return dto.toDomain();
  }

  @override
  Future<Program> createProgram(CreateProgramInput input) async {
    final CreateProgramRequestDto request =
        CreateProgramRequestDto.fromInput(input);
    final ProgramDto dto = await _api.createProgram(request);
    return dto.toDomain();
  }

  @override
  Future<List<ProgramBlock>> listProgramBlocks(int programId) async {
    final List<ProgramBlockDto> items = await _api.listProgramBlocks(programId);
    return items.map((ProgramBlockDto item) => item.toDomain()).toList();
  }

  @override
  Future<ProgramBlock> createProgramBlock(
    int programId,
    CreateProgramBlockInput input,
  ) async {
    final CreateProgramBlockRequestDto request =
        CreateProgramBlockRequestDto.fromInput(input);
    final ProgramBlockDto dto = await _api.createProgramBlock(programId, request);
    return dto.toDomain();
  }

  @override
  Future<ProgramBlock> copyProgramBlock(
    int blockId,
    CopyProgramBlockInput input,
  ) async {
    final CopyProgramBlockRequestDto request =
        CopyProgramBlockRequestDto.fromInput(input);
    final ProgramBlockDto dto = await _api.copyProgramBlock(blockId, request);
    return dto.toDomain();
  }

  @override
  Future<List<SessionTemplate>> listSessionTemplates(int blockId) async {
    final List<SessionTemplateDto> items = await _api.listSessionTemplates(
      blockId,
    );
    return items.map((SessionTemplateDto item) => item.toDomain()).toList();
  }

  @override
  Future<SessionTemplate> createSessionTemplate(
    int blockId,
    CreateSessionTemplateInput input,
  ) async {
    final CreateSessionTemplateRequestDto request =
        CreateSessionTemplateRequestDto.fromInput(input);
    final SessionTemplateDto dto = await _api.createSessionTemplate(
      blockId,
      request,
    );
    return dto.toDomain();
  }

  @override
  Future<SessionTemplate> copySessionTemplate(
    int templateId,
    CopySessionTemplateInput input,
  ) async {
    final CopySessionTemplateRequestDto request =
        CopySessionTemplateRequestDto.fromInput(input);
    final SessionTemplateDto dto = await _api.copySessionTemplate(
      templateId,
      request,
    );
    return dto.toDomain();
  }

  @override
  Future<List<SessionExerciseTemplate>> listSessionExerciseTemplates(
    int templateId,
  ) async {
    final List<SessionExerciseTemplateDto> items =
        await _api.listSessionExerciseTemplates(templateId);
    return items
        .map((SessionExerciseTemplateDto item) => item.toDomain())
        .toList();
  }

  @override
  Future<SessionExerciseTemplate> createSessionExerciseTemplate(
    int templateId,
    CreateSessionExerciseTemplateInput input,
  ) async {
    final CreateSessionExerciseTemplateRequestDto request =
        CreateSessionExerciseTemplateRequestDto.fromInput(input);
    final SessionExerciseTemplateDto dto =
        await _api.createSessionExerciseTemplate(templateId, request);
    return dto.toDomain();
  }
}
