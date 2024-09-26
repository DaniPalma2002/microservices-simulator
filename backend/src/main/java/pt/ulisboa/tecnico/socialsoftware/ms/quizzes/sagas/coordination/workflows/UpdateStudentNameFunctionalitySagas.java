package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.coordination.workflows;

import static pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.ErrorMessage.USER_MISSING_NAME;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.SyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionFactory;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.service.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.user.aggregate.UserDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.aggregates.SagaCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.aggregates.SagaUser;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.GenericSagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.SagaAggregate.SagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

public class UpdateStudentNameFunctionalitySagas extends WorkflowFunctionality {
    public enum State implements SagaState {
        UPDATE_STUDENT_NAME_READ_STUDENT {
            @Override
            public String getStateName() {
                return "UPDATE_STUDENT_NAME_READ_STUDENT";
            }
        },
        UPDATE_STUDENT_NAME_READ_COURSE {
            @Override
            public String getStateName() {
                return "UPDATE_STUDENT_NAME_READ_COURSE";
            }
        }
    }
    
    private SagaUser student;
    private CourseExecution oldCourseExecution;
        
    

    private final CourseExecutionService courseExecutionService;
    private final SagaUnitOfWorkService unitOfWorkService;
    private final CourseExecutionFactory courseExecutionFactory;

    public UpdateStudentNameFunctionalitySagas(CourseExecutionService courseExecutionService, CourseExecutionFactory courseExecutionFactory, SagaUnitOfWorkService unitOfWorkService, Integer executionAggregateId, Integer userAggregateId, UserDto userDto, SagaUnitOfWork unitOfWork) {
        this.courseExecutionService = courseExecutionService;
        this.courseExecutionFactory = courseExecutionFactory;
        this.unitOfWorkService = unitOfWorkService;
        this.buildWorkflow(executionAggregateId, userAggregateId, userDto, unitOfWork);
    }

    public void buildWorkflow(Integer executionAggregateId, Integer userAggregateId, UserDto userDto, SagaUnitOfWork unitOfWork) {
        this.workflow = new SagaWorkflow(this, unitOfWorkService, unitOfWork);

        if (userDto.getName() == null) {
            throw new TutorException(USER_MISSING_NAME);
        }
    
        SyncStep getStudentStep = new SyncStep("getStudentStep", () -> {
            SagaUser student = (SagaUser) unitOfWorkService.aggregateLoadAndRegisterRead(userAggregateId, unitOfWork);
            unitOfWorkService.registerSagaState(student, State.UPDATE_STUDENT_NAME_READ_STUDENT, unitOfWork);
            this.setStudent(student);
        });
    
        getStudentStep.registerCompensation(() -> {
            SagaUser student = this.getStudent();
            unitOfWorkService.registerSagaState(student, GenericSagaState.NOT_IN_SAGA, unitOfWork);
            unitOfWork.registerChanged(student);
        }, unitOfWork);
    
        SyncStep getOldCourseExecutionStep = new SyncStep("getOldCourseExecutionStep", () -> {
            SagaCourseExecution oldCourseExecution = (SagaCourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWork);
            unitOfWorkService.registerSagaState(oldCourseExecution, State.UPDATE_STUDENT_NAME_READ_COURSE, unitOfWork);
            this.setOldCourseExecution(oldCourseExecution);
        });
    
        getOldCourseExecutionStep.registerCompensation(() -> {
            CourseExecution newCourseExecution = courseExecutionFactory.createCourseExecutionFromExisting(this.getOldCourseExecution());
            unitOfWorkService.registerSagaState((SagaCourseExecution) newCourseExecution, GenericSagaState.NOT_IN_SAGA, unitOfWork);
            unitOfWork.registerChanged(newCourseExecution);
        }, unitOfWork);
    
        SyncStep updateStudentNameStep = new SyncStep("updateStudentNameStep", () -> {
            courseExecutionService.updateExecutionStudentName(executionAggregateId, userAggregateId, userDto.getName(), unitOfWork);
        }, new ArrayList<>(Arrays.asList(getStudentStep, getOldCourseExecutionStep)));
    
        workflow.addStep(getStudentStep);
        workflow.addStep(getOldCourseExecutionStep);
        workflow.addStep(updateStudentNameStep);
    }

    @Override
    public void handleEvents() {

    }

    public SagaUser getStudent() {
        return student;
    }

    public void setStudent(SagaUser student) {
        this.student = student;
    }

    public CourseExecution getOldCourseExecution() {
        return oldCourseExecution;
    }

    public void setOldCourseExecution(CourseExecution oldCourseExecution) {
        this.oldCourseExecution = oldCourseExecution;
    }
}

