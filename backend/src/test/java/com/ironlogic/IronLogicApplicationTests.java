package com.ironlogic;

import com.ironlogic.modules.exercise.infrastructure.persistence.mapper.ExerciseMapper;
import com.ironlogic.modules.program.infrastructure.persistence.mapper.ProgramBlockMapper;
import com.ironlogic.modules.program.infrastructure.persistence.mapper.ProgramMapper;
import com.ironlogic.modules.program.infrastructure.persistence.mapper.SessionExercisePrescriptionSegmentMapper;
import com.ironlogic.modules.program.infrastructure.persistence.mapper.SessionExerciseTemplateMapper;
import com.ironlogic.modules.program.infrastructure.persistence.mapper.SessionTemplateMapper;
import com.ironlogic.modules.progression.infrastructure.persistence.mapper.ProgramProgressMapper;
import com.ironlogic.modules.workout.infrastructure.persistence.mapper.WorkoutExerciseMapper;
import com.ironlogic.modules.workout.infrastructure.persistence.mapper.WorkoutSessionMapper;
import com.ironlogic.modules.workout.infrastructure.persistence.mapper.WorkoutSetMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class IronLogicApplicationTests {

    @MockBean
    private ExerciseMapper exerciseMapper;

    @MockBean
    private ProgramMapper programMapper;

    @MockBean
    private ProgramBlockMapper programBlockMapper;

    @MockBean
    private SessionTemplateMapper sessionTemplateMapper;

    @MockBean
    private SessionExerciseTemplateMapper sessionExerciseTemplateMapper;

    @MockBean
    private SessionExercisePrescriptionSegmentMapper sessionExercisePrescriptionSegmentMapper;

    @MockBean
    private ProgramProgressMapper programProgressMapper;

    @MockBean
    private WorkoutSessionMapper workoutSessionMapper;

    @MockBean
    private WorkoutExerciseMapper workoutExerciseMapper;

    @MockBean
    private WorkoutSetMapper workoutSetMapper;

    @Test
    void contextLoads() {
    }
}
