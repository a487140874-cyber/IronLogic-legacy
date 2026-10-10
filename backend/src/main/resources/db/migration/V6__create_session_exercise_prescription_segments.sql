-- 为模板动作新增多段处方子表。
-- 设计目标是增量增强现有 SessionExerciseTemplate，而不是推翻原结构：
-- 1. SessionExerciseTemplate 继续表示“动作槽位”
-- 2. session_exercise_prescription_segments 表示该槽位下的多个处方段
-- 3. 旧数据可以没有 segment，读取时由应用层做兼容映射
-- 4. 同一模板动作下 segment_order_no 必须唯一，用于稳定表达顶组/回退组顺序

create table if not exists session_exercise_prescription_segments (
    id bigserial primary key,
    session_exercise_template_id bigint not null,
    segment_order_no integer not null,
    label varchar(64),
    sets integer not null,
    reps integer,
    rest_seconds integer,
    rpe numeric(4,1),
    rir integer,
    load_prescription_type varchar(64),
    target_weight numeric(10,2),
    percentage_value numeric(6,2),
    relative_adjustment_type varchar(64),
    relative_adjustment_value numeric(10,2),
    notes text,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    constraint uq_session_exercise_prescription_segments_order
        unique(session_exercise_template_id, segment_order_no),
    constraint fk_session_exercise_prescription_segments_template
        foreign key (session_exercise_template_id)
        references session_exercise_templates(id)
        on delete cascade
);

create index if not exists idx_session_exercise_prescription_segments_template_id
    on session_exercise_prescription_segments(session_exercise_template_id);
