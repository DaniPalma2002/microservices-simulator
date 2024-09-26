package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.coordination.workflows;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.Topic;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicFactory;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.service.TopicService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.aggregates.SagaTopic;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.aggregates.states.TopicSagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.GenericSagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaSyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

public class UpdateTopicFunctionalitySagas extends WorkflowFunctionality {
    private Topic oldTopic;

    

    private final TopicService topicService;
    private final SagaUnitOfWorkService unitOfWorkService;

    public UpdateTopicFunctionalitySagas(TopicService topicService, SagaUnitOfWorkService unitOfWorkService,  
                            TopicDto topicDto, TopicFactory topicFactory, SagaUnitOfWork unitOfWork) {
        this.topicService = topicService;
        this.unitOfWorkService = unitOfWorkService;
        this.buildWorkflow(topicDto, topicFactory, unitOfWork);
    }

    public void buildWorkflow(TopicDto topicDto, TopicFactory topicFactory, SagaUnitOfWork unitOfWork) {
        this.workflow = new SagaWorkflow(this, unitOfWorkService, unitOfWork);

        SagaSyncStep getOldTopicStep = new SagaSyncStep("getOldTopicStep", () -> {
            SagaTopic oldTopic = (SagaTopic) unitOfWorkService.aggregateLoadAndRegisterRead(topicDto.getAggregateId(), unitOfWork);
            unitOfWorkService.registerSagaState(oldTopic, TopicSagaState.UPDATE_TOPIC_READ_TOPIC, unitOfWork);
            this.setOldTopic(oldTopic);
        });
    
        getOldTopicStep.registerCompensation(() -> {
            Topic newTopic = topicFactory.createTopicFromExisting(this.getOldTopic());
            unitOfWorkService.registerSagaState((SagaTopic) newTopic, GenericSagaState.NOT_IN_SAGA, unitOfWork);
            unitOfWork.registerChanged(newTopic);
        }, unitOfWork);
    
        SagaSyncStep updateTopicStep = new SagaSyncStep("updateTopicStep", () -> {
            topicService.updateTopic(topicDto, unitOfWork);
        }, new ArrayList<>(Arrays.asList(getOldTopicStep)));
    
        workflow.addStep(getOldTopicStep);
        workflow.addStep(updateTopicStep);
    }

    @Override
    public void handleEvents() {

    }

    

    public Topic getOldTopic() {
        return oldTopic;
    }

    public void setOldTopic(Topic oldTopic) {
        this.oldTopic = oldTopic;
    }
}