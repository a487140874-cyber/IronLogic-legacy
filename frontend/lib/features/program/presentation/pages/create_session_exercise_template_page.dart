import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../app/router.dart';
import '../../../../shared/widgets/error_view.dart';
import '../../../../shared/widgets/form_section.dart';
import '../../../../shared/widgets/ironlogic_scaffold.dart';
import '../../../../shared/widgets/loading_view.dart';
import '../../../exercise/application/exercise_providers.dart';
import '../../../exercise/domain/model/exercise.dart';
import '../../application/program_providers.dart';
import '../../domain/model/create_session_exercise_template_input.dart';
import '../../domain/model/session_exercise_prescription_segment_input.dart';

/// 创建 SessionExerciseTemplate 页。
///
/// 本轮把“动作槽位”和“处方段”分开建模：
/// - 页面顶层只负责选择动作和顺序号
/// - 处方细节放到一个或多个 segment 卡片中
/// 这样既保留了原来的 SessionExerciseTemplate 结构，又能表达顶组/回退组。
class CreateSessionExerciseTemplatePage extends ConsumerStatefulWidget {
  /// 创建页面。
  const CreateSessionExerciseTemplatePage({
    required this.templateId,
    super.key,
  });

  /// 所属模板 id。
  final int templateId;

  @override
  ConsumerState<CreateSessionExerciseTemplatePage> createState() =>
      _CreateSessionExerciseTemplatePageState();
}

class _CreateSessionExerciseTemplatePageState
    extends ConsumerState<CreateSessionExerciseTemplatePage> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController _orderNoController = TextEditingController(text: '1');
  final TextEditingController _targetWeightUnitController =
      TextEditingController(text: 'kg');

  int? _selectedExerciseId;
  late final List<_PrescriptionSegmentDraft> _segments;

  @override
  void initState() {
    super.initState();
    _segments = <_PrescriptionSegmentDraft>[
      _PrescriptionSegmentDraft(
        segmentOrderNo: 1,
        label: '主组',
        sets: '3',
        reps: '5',
        restSeconds: '120',
        loadPrescriptionType: 'ABSOLUTE_WEIGHT',
      ),
    ];
  }

  @override
  void dispose() {
    _orderNoController.dispose();
    _targetWeightUnitController.dispose();
    for (final _PrescriptionSegmentDraft draft in _segments) {
      draft.dispose();
    }
    super.dispose();
  }

  void _addSegment() {
    setState(() {
      _segments.add(
        _PrescriptionSegmentDraft(
          segmentOrderNo: _segments.length + 1,
          label: '段 ${_segments.length + 1}',
        ),
      );
    });
  }

  void _removeSegment(int index) {
    if (_segments.length == 1) {
      return;
    }
    setState(() {
      final _PrescriptionSegmentDraft removed = _segments.removeAt(index);
      removed.dispose();
      for (int i = 0; i < _segments.length; i++) {
        _segments[i].segmentOrderNoController.text = '${i + 1}';
      }
    });
  }

  /// 提交模板动作创建请求。
  Future<void> _submit() async {
    if (!_formKey.currentState!.validate() || _selectedExerciseId == null) {
      if (_selectedExerciseId == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('请选择动作')),
        );
      }
      return;
    }

    final List<SessionExercisePrescriptionSegmentInput> segmentInputs = _segments
        .map((draft) => draft.toInput())
        .toList();
    final SessionExercisePrescriptionSegmentInput primarySegment =
        segmentInputs.first;

    final SessionExerciseTemplateCreateController controller = ref.read(
      sessionExerciseTemplateCreateControllerProvider(widget.templateId).notifier,
    );
    await controller.submit(
      CreateSessionExerciseTemplateInput(
        exerciseId: _selectedExerciseId!,
        orderNo: int.parse(_orderNoController.text.trim()),
        targetSets: primarySegment.sets,
        targetReps: primarySegment.reps,
        targetWeight: primarySegment.targetWeight,
        targetWeightUnit: _targetWeightUnitController.text.trim().isEmpty
            ? null
            : _targetWeightUnitController.text.trim(),
        restSeconds: primarySegment.restSeconds,
        intensityMode: primarySegment.loadPrescriptionType,
        prescriptionSegments: segmentInputs,
      ),
    );

    final AsyncValue<void> state = ref.read(
      sessionExerciseTemplateCreateControllerProvider(widget.templateId),
    );
    if (!mounted) {
      return;
    }

    state.whenOrNull(
      data: (_) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('模板动作添加成功')),
        );
        context.go(AppRoutes.sessionTemplateDetail(widget.templateId));
      },
      error: (Object error, StackTrace stackTrace) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(error.toString())),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final AsyncValue<List<Exercise>> exercisesAsync = ref.watch(exerciseListProvider);
    final AsyncValue<void> submitState = ref.watch(
      sessionExerciseTemplateCreateControllerProvider(widget.templateId),
    );
    final bool isSubmitting = submitState.isLoading;

    return IronLogicScaffold(
      title: '添加模板动作',
      body: exercisesAsync.when(
        loading: () => const LoadingView(message: '正在加载动作列表...'),
        error: (Object error, StackTrace stackTrace) {
          return ErrorView(
            message: error.toString(),
            onRetry: () => ref.invalidate(exerciseListProvider),
          );
        },
        data: (List<Exercise> exercises) {
          return ListView(
            padding: const EdgeInsets.all(16),
            children: <Widget>[
              const Text(
                '本轮把一个模板动作拆成“动作槽位 + 多个处方段”。如果你只填一个段，就等价于旧的单段模板动作。',
              ),
              const SizedBox(height: 16),
              FormSection(
                child: Form(
                  key: _formKey,
                  child: Column(
                    children: <Widget>[
                      DropdownButtonFormField<int>(
                        value: _selectedExerciseId,
                        decoration: const InputDecoration(labelText: '动作'),
                        items: exercises
                            .map(
                              (Exercise exercise) => DropdownMenuItem<int>(
                                value: exercise.id,
                                child: Text(exercise.name),
                              ),
                            )
                            .toList(),
                        onChanged: (int? value) {
                          setState(() => _selectedExerciseId = value);
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _orderNoController,
                        keyboardType: TextInputType.number,
                        decoration: const InputDecoration(labelText: '动作顺序号'),
                        validator: (String? value) =>
                            int.tryParse(value ?? '') == null ? '请输入有效顺序号' : null,
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _targetWeightUnitController,
                        decoration: const InputDecoration(labelText: '重量单位'),
                      ),
                      const SizedBox(height: 24),
                      Row(
                        children: <Widget>[
                          Text(
                            '处方段',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          const Spacer(),
                          FilledButton.tonalIcon(
                            onPressed: _addSegment,
                            icon: const Icon(Icons.add),
                            label: const Text('新增处方段'),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      ..._segments.asMap().entries.map(
                        (MapEntry<int, _PrescriptionSegmentDraft> entry) {
                          return _PrescriptionSegmentCard(
                            draft: entry.value,
                            canRemove: _segments.length > 1,
                            onRemove: () => _removeSegment(entry.key),
                          );
                        },
                      ),
                      const SizedBox(height: 24),
                      SizedBox(
                        width: double.infinity,
                        child: FilledButton(
                          onPressed: isSubmitting ? null : _submit,
                          child: Text(isSubmitting ? '提交中...' : '提交创建'),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}

class _PrescriptionSegmentCard extends StatelessWidget {
  const _PrescriptionSegmentCard({
    required this.draft,
    required this.canRemove,
    required this.onRemove,
  });

  final _PrescriptionSegmentDraft draft;
  final bool canRemove;
  final VoidCallback onRemove;

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: <Widget>[
            Row(
              children: <Widget>[
                Expanded(
                  child: Text(
                    '处方段 ${draft.segmentOrderNoController.text}',
                    style: Theme.of(context).textTheme.titleSmall,
                  ),
                ),
                if (canRemove)
                  IconButton(
                    tooltip: '删除处方段',
                    onPressed: onRemove,
                    icon: const Icon(Icons.delete_outline),
                  ),
              ],
            ),
            TextFormField(
              controller: draft.labelController,
              decoration: const InputDecoration(labelText: '标签'),
            ),
            const SizedBox(height: 12),
            Row(
              children: <Widget>[
                Expanded(
                  child: TextFormField(
                    controller: draft.setsController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: '组数'),
                    validator: (String? value) =>
                        int.tryParse(value ?? '') == null ? '请输入有效组数' : null,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: TextFormField(
                    controller: draft.repsController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: '次数'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: draft.loadPrescriptionTypeController.text.isEmpty
                  ? null
                  : draft.loadPrescriptionTypeController.text,
              decoration: const InputDecoration(labelText: '负荷类型'),
              items: const <DropdownMenuItem<String>>[
                DropdownMenuItem(
                  value: 'ABSOLUTE_WEIGHT',
                  child: Text('ABSOLUTE_WEIGHT'),
                ),
                DropdownMenuItem(
                  value: 'PERCENT_1RM',
                  child: Text('PERCENT_1RM'),
                ),
                DropdownMenuItem(
                  value: 'RELATIVE_TO_PREVIOUS_SEGMENT',
                  child: Text('RELATIVE_TO_PREVIOUS_SEGMENT'),
                ),
                DropdownMenuItem(value: 'RPE_ONLY', child: Text('RPE_ONLY')),
              ],
              onChanged: (String? value) {
                draft.loadPrescriptionTypeController.text = value ?? '';
              },
            ),
            const SizedBox(height: 12),
            Row(
              children: <Widget>[
                Expanded(
                  child: TextFormField(
                    controller: draft.targetWeightController,
                    keyboardType: const TextInputType.numberWithOptions(
                      decimal: true,
                    ),
                    decoration: const InputDecoration(labelText: '绝对重量'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: TextFormField(
                    controller: draft.percentageValueController,
                    keyboardType: const TextInputType.numberWithOptions(
                      decimal: true,
                    ),
                    decoration: const InputDecoration(labelText: '百分比'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: <Widget>[
                Expanded(
                  child: DropdownButtonFormField<String>(
                    value: draft.relativeAdjustmentTypeController.text.isEmpty
                        ? null
                        : draft.relativeAdjustmentTypeController.text,
                    decoration: const InputDecoration(labelText: '相对调整类型'),
                    items: const <DropdownMenuItem<String>>[
                      DropdownMenuItem(
                        value: 'PERCENT_DELTA',
                        child: Text('PERCENT_DELTA'),
                      ),
                      DropdownMenuItem(
                        value: 'ABSOLUTE_DELTA',
                        child: Text('ABSOLUTE_DELTA'),
                      ),
                    ],
                    onChanged: (String? value) {
                      draft.relativeAdjustmentTypeController.text = value ?? '';
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: TextFormField(
                    controller: draft.relativeAdjustmentValueController,
                    keyboardType: const TextInputType.numberWithOptions(
                      decimal: true,
                    ),
                    decoration: const InputDecoration(labelText: '相对调整值'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: <Widget>[
                Expanded(
                  child: TextFormField(
                    controller: draft.restSecondsController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: '休息秒数'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: TextFormField(
                    controller: draft.rpeController,
                    keyboardType: const TextInputType.numberWithOptions(
                      decimal: true,
                    ),
                    decoration: const InputDecoration(labelText: 'RPE'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: draft.notesController,
              decoration: const InputDecoration(labelText: '备注'),
            ),
          ],
        ),
      ),
    );
  }
}

class _PrescriptionSegmentDraft {
  _PrescriptionSegmentDraft({
    required int segmentOrderNo,
    String label = '',
    String sets = '',
    String reps = '',
    String restSeconds = '',
    String loadPrescriptionType = '',
  })  : segmentOrderNoController = TextEditingController(
          text: '$segmentOrderNo',
        ),
        labelController = TextEditingController(text: label),
        setsController = TextEditingController(text: sets),
        repsController = TextEditingController(text: reps),
        restSecondsController = TextEditingController(text: restSeconds),
        rpeController = TextEditingController(),
        targetWeightController = TextEditingController(),
        percentageValueController = TextEditingController(),
        relativeAdjustmentTypeController = TextEditingController(),
        relativeAdjustmentValueController = TextEditingController(),
        notesController = TextEditingController(),
        loadPrescriptionTypeController = TextEditingController(
          text: loadPrescriptionType,
        );

  final TextEditingController segmentOrderNoController;
  final TextEditingController labelController;
  final TextEditingController setsController;
  final TextEditingController repsController;
  final TextEditingController restSecondsController;
  final TextEditingController rpeController;
  final TextEditingController targetWeightController;
  final TextEditingController percentageValueController;
  final TextEditingController relativeAdjustmentTypeController;
  final TextEditingController relativeAdjustmentValueController;
  final TextEditingController notesController;
  final TextEditingController loadPrescriptionTypeController;

  SessionExercisePrescriptionSegmentInput toInput() {
    return SessionExercisePrescriptionSegmentInput(
      segmentOrderNo: int.parse(segmentOrderNoController.text.trim()),
      label: labelController.text.trim().isEmpty ? null : labelController.text.trim(),
      sets: int.parse(setsController.text.trim()),
      reps: int.tryParse(repsController.text.trim()),
      restSeconds: int.tryParse(restSecondsController.text.trim()),
      rpe: double.tryParse(rpeController.text.trim()),
      loadPrescriptionType: loadPrescriptionTypeController.text.trim().isEmpty
          ? null
          : loadPrescriptionTypeController.text.trim(),
      targetWeight: double.tryParse(targetWeightController.text.trim()),
      percentageValue: double.tryParse(percentageValueController.text.trim()),
      relativeAdjustmentType:
          relativeAdjustmentTypeController.text.trim().isEmpty
              ? null
              : relativeAdjustmentTypeController.text.trim(),
      relativeAdjustmentValue: double.tryParse(
        relativeAdjustmentValueController.text.trim(),
      ),
      notes: notesController.text.trim().isEmpty ? null : notesController.text.trim(),
    );
  }

  void dispose() {
    segmentOrderNoController.dispose();
    labelController.dispose();
    setsController.dispose();
    repsController.dispose();
    restSecondsController.dispose();
    rpeController.dispose();
    targetWeightController.dispose();
    percentageValueController.dispose();
    relativeAdjustmentTypeController.dispose();
    relativeAdjustmentValueController.dispose();
    notesController.dispose();
    loadPrescriptionTypeController.dispose();
  }
}
