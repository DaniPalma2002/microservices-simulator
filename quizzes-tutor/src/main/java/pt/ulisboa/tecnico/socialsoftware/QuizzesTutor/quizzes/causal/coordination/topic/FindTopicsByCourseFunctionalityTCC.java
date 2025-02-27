package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.causal.coordination.topic;

import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.CausalUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.CausalUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.causal.workflow.CausalWorkflow;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.SyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.service.TopicService;

import java.util.List;

public class FindTopicsByCourseFunctionalityTCC extends WorkflowFunctionality {
    private List<TopicDto> topics;
    private final TopicService topicService;
    private final CausalUnitOfWorkService unitOfWorkService;

    public FindTopicsByCourseFunctionalityTCC(TopicService topicService, CausalUnitOfWorkService unitOfWorkService,  
                            Integer courseAggregateId, CausalUnitOfWork unitOfWork) {
        this.topicService = topicService;
        this.unitOfWorkService = unitOfWorkService;
        this.buildWorkflow(courseAggregateId, unitOfWork);
    }

    public void buildWorkflow(Integer courseAggregateId, CausalUnitOfWork unitOfWork) {
        this.workflow = new CausalWorkflow(this, unitOfWorkService, unitOfWork);

        SyncStep step = new SyncStep(() -> {
            this.topics = topicService.findTopicsByCourseId(courseAggregateId, unitOfWork);
        });
    
        workflow.addStep(step);
    }
    

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }
}