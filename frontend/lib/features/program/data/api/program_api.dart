import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/network/dio_client.dart';
import '../dto/copy_program_block_request_dto.dart';
import '../dto/copy_session_template_request_dto.dart';
import '../dto/create_program_block_request_dto.dart';
import '../dto/create_program_request_dto.dart';
import '../dto/create_session_exercise_template_request_dto.dart';
import '../dto/create_session_template_request_dto.dart';
import '../dto/program_dto.dart';
import '../dto/program_block_dto.dart';
import '../dto/session_exercise_template_dto.dart';
import '../dto/session_template_dto.dart';

/// Program 接口访问层。
///
/// 该类属于 `program/data/api`，只负责发起 HTTP 请求与解析原始响应体。
class ProgramApi {
  /// 创建 Program API。
  const ProgramApi(this._dio);

  final Dio _dio;

  /// 调用 `GET /api/programs` 获取 Program 列表。
  Future<List<ProgramDto>> listPrograms() async {
    final Response<dynamic> response = await _dio.get('/api/programs');
    return unwrapApiData<List<ProgramDto>>(response, (Object? data) {
      final List<dynamic> rawList = data as List<dynamic>? ?? <dynamic>[];
      return rawList
          .map((dynamic item) => ProgramDto.fromJson(item as Map<String, dynamic>))
          .toList();
    });
  }

  /// 调用 `GET /api/programs/{id}` 获取 Program 详情。
  Future<ProgramDto> getProgram(int id) async {
    final Response<dynamic> response = await _dio.get('/api/programs/$id');
    return unwrapApiData<ProgramDto>(response, (Object? data) {
      return ProgramDto.fromJson(data as Map<String, dynamic>);
    });
  }

  /// 调用 `POST /api/programs` 创建 Program。
  Future<ProgramDto> createProgram(CreateProgramRequestDto request) async {
    final Response<dynamic> response = await _dio.post(
      '/api/programs',
      data: request.toJson(),
    );
    return unwrapApiData<ProgramDto>(response, (Object? data) {
      return ProgramDto.fromJson(data as Map<String, dynamic>);
    });
  }

  /// 调用 `GET /api/programs/{programId}/blocks` 获取 Block 列表。
  Future<List<ProgramBlockDto>> listProgramBlocks(int programId) async {
    final Response<dynamic> response =
        await _dio.get('/api/programs/$programId/blocks');
    return unwrapApiData<List<ProgramBlockDto>>(response, (Object? data) {
      final List<dynamic> rawList = data as List<dynamic>? ?? <dynamic>[];
      return rawList
          .map(
            (dynamic item) =>
                ProgramBlockDto.fromJson(item as Map<String, dynamic>),
          )
          .toList();
    });
  }

  /// 调用 `POST /api/programs/{programId}/blocks` 创建 Block。
  Future<ProgramBlockDto> createProgramBlock(
    int programId,
    CreateProgramBlockRequestDto request,
  ) async {
    final Response<dynamic> response = await _dio.post(
      '/api/programs/$programId/blocks',
      data: request.toJson(),
    );
    return unwrapApiData<ProgramBlockDto>(response, (Object? data) {
      return ProgramBlockDto.fromJson(data as Map<String, dynamic>);
    });
  }

  /// 调用 `POST /api/blocks/{id}/copy` 复制 Block。
  Future<ProgramBlockDto> copyProgramBlock(
    int blockId,
    CopyProgramBlockRequestDto request,
  ) async {
    final Response<dynamic> response = await _dio.post(
      '/api/blocks/$blockId/copy',
      data: request.toJson(),
    );
    return unwrapApiData<ProgramBlockDto>(response, (Object? data) {
      return ProgramBlockDto.fromJson(data as Map<String, dynamic>);
    });
  }

  /// 调用 `GET /api/blocks/{blockId}/session-templates` 获取模板列表。
  Future<List<SessionTemplateDto>> listSessionTemplates(int blockId) async {
    final Response<dynamic> response =
        await _dio.get('/api/blocks/$blockId/session-templates');
    return unwrapApiData<List<SessionTemplateDto>>(response, (Object? data) {
      final List<dynamic> rawList = data as List<dynamic>? ?? <dynamic>[];
      return rawList
          .map(
            (dynamic item) =>
                SessionTemplateDto.fromJson(item as Map<String, dynamic>),
          )
          .toList();
    });
  }

  /// 调用 `POST /api/blocks/{blockId}/session-templates` 创建模板。
  Future<SessionTemplateDto> createSessionTemplate(
    int blockId,
    CreateSessionTemplateRequestDto request,
  ) async {
    final Response<dynamic> response = await _dio.post(
      '/api/blocks/$blockId/session-templates',
      data: request.toJson(),
    );
    return unwrapApiData<SessionTemplateDto>(response, (Object? data) {
      return SessionTemplateDto.fromJson(data as Map<String, dynamic>);
    });
  }

  /// 调用 `POST /api/session-templates/{id}/copy` 复制 SessionTemplate。
  Future<SessionTemplateDto> copySessionTemplate(
    int templateId,
    CopySessionTemplateRequestDto request,
  ) async {
    final Response<dynamic> response = await _dio.post(
      '/api/session-templates/$templateId/copy',
      data: request.toJson(),
    );
    return unwrapApiData<SessionTemplateDto>(response, (Object? data) {
      return SessionTemplateDto.fromJson(data as Map<String, dynamic>);
    });
  }

  /// 调用 `GET /api/session-templates/{templateId}/exercises` 获取模板动作列表。
  Future<List<SessionExerciseTemplateDto>> listSessionExerciseTemplates(
    int templateId,
  ) async {
    final Response<dynamic> response =
        await _dio.get('/api/session-templates/$templateId/exercises');
    return unwrapApiData<List<SessionExerciseTemplateDto>>(response, (
      Object? data,
    ) {
      final List<dynamic> rawList = data as List<dynamic>? ?? <dynamic>[];
      return rawList
          .map(
            (dynamic item) => SessionExerciseTemplateDto.fromJson(
              item as Map<String, dynamic>,
            ),
          )
          .toList();
    });
  }

  /// 调用 `POST /api/session-templates/{templateId}/exercises` 创建模板动作。
  Future<SessionExerciseTemplateDto> createSessionExerciseTemplate(
    int templateId,
    CreateSessionExerciseTemplateRequestDto request,
  ) async {
    final Response<dynamic> response = await _dio.post(
      '/api/session-templates/$templateId/exercises',
      data: request.toJson(),
    );
    return unwrapApiData<SessionExerciseTemplateDto>(response, (Object? data) {
      return SessionExerciseTemplateDto.fromJson(data as Map<String, dynamic>);
    });
  }
}

/// Program API Provider。
final programApiProvider = Provider<ProgramApi>((ref) {
  return ProgramApi(ref.watch(dioProvider));
});
