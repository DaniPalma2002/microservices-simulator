package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.coordination.execution;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionFactory;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.service.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate.UserDto;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaSyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

import static pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.exception.ErrorMessage.USER_MISSING_NAME;

public class UpdateStudentNameFunctionalitySagas extends WorkflowFunctionality {
    
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

        SagaSyncStep updateStudentNameStep = new SagaSyncStep("updateStudentNameStep", () -> {
            courseExecutionService.updateExecutionStudentName(executionAggregateId, userAggregateId, userDto.getName(), unitOfWork);
        });
    
        workflow.addStep(updateStudentNameStep);
    }
}

