package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.coordination.workflows;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.SyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.user.service.UserService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.aggregates.SagaUser;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.GenericSagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.SagaAggregate.SagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

public class DeleteUserFunctionalitySagas extends WorkflowFunctionality {

    public enum State implements SagaState {
        DELETE_USER_READ_USER {
            @Override
            public String getStateName() {
                return "DELETE_USER_READ_USER";
            }
        }
    }

    private SagaUser user;

    

    private final UserService userService;
    private final SagaUnitOfWorkService unitOfWorkService;

    public DeleteUserFunctionalitySagas(UserService userService, SagaUnitOfWorkService unitOfWorkService,  
                            Integer userAggregateId, SagaUnitOfWork unitOfWork) {
        this.userService = userService;
        this.unitOfWorkService = unitOfWorkService;
        this.buildWorkflow(userAggregateId, unitOfWork);
    }

    public void buildWorkflow(Integer userAggregateId, SagaUnitOfWork unitOfWork) {
        this.workflow = new SagaWorkflow(this, unitOfWorkService, unitOfWork);

        SyncStep getUserStep = new SyncStep("getUserStep", () -> {
            SagaUser user = (SagaUser) unitOfWorkService.aggregateLoadAndRegisterRead(userAggregateId, unitOfWork);
            unitOfWorkService.registerSagaState(user, State.DELETE_USER_READ_USER, unitOfWork);
            this.setUser(user);
        });
    
        getUserStep.registerCompensation(() -> {
            SagaUser user = this.getUser();
            unitOfWorkService.registerSagaState(user, GenericSagaState.NOT_IN_SAGA, unitOfWork);
            unitOfWork.registerChanged(user);
        }, unitOfWork);
    
        SyncStep deleteUserStep = new SyncStep("deleteUserStep", () -> {
            userService.deleteUser(userAggregateId, unitOfWork);
        }, new ArrayList<>(Arrays.asList(getUserStep)));
    
        workflow.addStep(getUserStep);
        workflow.addStep(deleteUserStep);
    }

    @Override
    public void handleEvents() {

    }

    public SagaUser getUser() {
        return user;
    }

    public void setUser(SagaUser user) {
        this.user = user;
    }
}