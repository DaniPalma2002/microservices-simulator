package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.coordination.functionalities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.Question;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionCourse;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionTopic;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionFactory;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.Topic;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicFactory;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.service.TopicService;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.SyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.Aggregate.AggregateState;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.course.service.CourseService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.service.QuestionFunctionalitiesInterface;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.service.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Profile("sagas")
@Service
public class SagaQuestionFunctionalities implements QuestionFunctionalitiesInterface{
    @Autowired
    private SagaUnitOfWorkService unitOfWorkService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private QuestionFactory questionFactory;

    public QuestionDto findQuestionByAggregateId(Integer aggregateId) {
        String functionalityName = new Throwable().getStackTrace()[0].getMethodName();
        SagaUnitOfWork unitOfWork = unitOfWorkService.createUnitOfWork(functionalityName);

        FindQuestionByAggregateIdData data = new FindQuestionByAggregateIdData();
        SagaWorkflow workflow = new SagaWorkflow(data, unitOfWorkService, functionalityName);

        SyncStep findQuestionStep = new SyncStep(() -> {
            QuestionDto questionDto = questionService.getQuestionById(aggregateId, unitOfWork);
            data.setQuestionDto(questionDto);
        });

        workflow.addStep(findQuestionStep);
        workflow.execute();

        return data.getQuestionDto();
    }

    public List<QuestionDto> findQuestionsByCourseAggregateId(Integer courseAggregateId) {
        String functionalityName = new Throwable().getStackTrace()[0].getMethodName();
        SagaUnitOfWork unitOfWork = unitOfWorkService.createUnitOfWork(functionalityName);
    
        FindQuestionsByCourseData data = new FindQuestionsByCourseData();
        SagaWorkflow workflow = new SagaWorkflow(data, unitOfWorkService, functionalityName);
    
        SyncStep findQuestionsStep = new SyncStep(() -> {
            List<QuestionDto> questions = questionService.findQuestionsByCourseAggregateId(courseAggregateId, unitOfWork);
            data.setQuestions(questions);
        });
    
        workflow.addStep(findQuestionsStep);
        workflow.execute();
    
        return data.getQuestions();
    }

    public QuestionDto createQuestion(Integer courseAggregateId, QuestionDto questionDto) throws Exception {
        String functionalityName = new Throwable().getStackTrace()[0].getMethodName();
        SagaUnitOfWork unitOfWork = unitOfWorkService.createUnitOfWork(functionalityName);
    
        CreateQuestionData data = new CreateQuestionData();
        SagaWorkflow workflow = new SagaWorkflow(data, unitOfWorkService, functionalityName);
    
        SyncStep validateQuestionTopicsStep = new SyncStep(() -> {
            for (TopicDto topicDto : questionDto.getTopicDto()) {
                if (!topicDto.getCourseId().equals(courseAggregateId)) {
                    throw new TutorException(ErrorMessage.QUESTION_TOPIC_INVALID_COURSE, topicDto.getAggregateId(), courseAggregateId);
                }
            }
        });
    
        SyncStep getCourseStep = new SyncStep(() -> {
            QuestionCourse course = new QuestionCourse(courseService.getCourseById(courseAggregateId, unitOfWork));
            data.setCourse(course);
        });
    
        SyncStep getTopicsStep = new SyncStep(() -> {
            List<TopicDto> topics = questionDto.getTopicDto().stream()
                    .map(topicDto -> topicService.getTopicById(topicDto.getAggregateId(), unitOfWork))
                    .collect(Collectors.toList());
            data.setTopics(topics);
        });
    
        SyncStep createQuestionStep = new SyncStep(() -> {
            QuestionDto createdQuestion = questionService.createQuestion(data.getCourse(), questionDto, data.getTopics(), unitOfWork);
            data.setCreatedQuestion(createdQuestion);
        });
    
        createQuestionStep.registerCompensation(() -> {
            Question question = (Question) unitOfWorkService.aggregateLoadAndRegisterRead(data.getCreatedQuestion().getAggregateId(), unitOfWork);
            question.remove();
            question.setState(AggregateState.DELETED);
            unitOfWork.registerChanged(question);
        }, unitOfWork);
    
        workflow.addStep(validateQuestionTopicsStep);
        workflow.addStep(getCourseStep);
        workflow.addStep(getTopicsStep);
        workflow.addStep(createQuestionStep);
    
        workflow.execute();
    
        return data.getCreatedQuestion();
    }

    public void updateQuestion(QuestionDto questionDto) throws Exception {
        String functionalityName = new Throwable().getStackTrace()[0].getMethodName();
        SagaUnitOfWork unitOfWork = unitOfWorkService.createUnitOfWork(functionalityName);
    
        UpdateQuestionData data = new UpdateQuestionData();
        SagaWorkflow workflow = new SagaWorkflow(data, unitOfWorkService, functionalityName);
    
        SyncStep getOldQuestionStep = new SyncStep(() -> {
            Question oldQuestion = (Question) unitOfWorkService.aggregateLoadAndRegisterRead(questionDto.getAggregateId(), unitOfWork);
            oldQuestion.setState(AggregateState.IN_SAGA);
            data.setOldQuestion(oldQuestion);
        });
    
        getOldQuestionStep.registerCompensation(() -> {
            Question newQuestion = questionFactory.createQuestionFromExisting(data.getOldQuestion());
            unitOfWork.registerChanged(newQuestion);
            newQuestion.setState(AggregateState.ACTIVE);
        }, unitOfWork);
    
        SyncStep updateQuestionStep = new SyncStep(() -> {
            questionService.updateQuestion(questionDto, unitOfWork);
        });
    
        workflow.addStep(getOldQuestionStep);
        workflow.addStep(updateQuestionStep);
    
        workflow.execute();
    }

    public void removeQuestion(Integer questionAggregateId) throws Exception {
        String functionalityName = new Throwable().getStackTrace()[0].getMethodName();
        SagaUnitOfWork unitOfWork = unitOfWorkService.createUnitOfWork(functionalityName);
    
        RemoveQuestionData data = new RemoveQuestionData();
        SagaWorkflow workflow = new SagaWorkflow(data, unitOfWorkService, functionalityName);
    
        SyncStep getQuestionStep = new SyncStep(() -> {
            Question question = (Question) unitOfWorkService.aggregateLoadAndRegisterRead(questionAggregateId, unitOfWork);
            question.setState(AggregateState.IN_SAGA);
            data.setQuestion(question);
        });
    
        getQuestionStep.registerCompensation(() -> {
            Question question = data.getQuestion();
            question.setState(AggregateState.ACTIVE);
            unitOfWork.registerChanged(question);
        }, unitOfWork);
    
        SyncStep removeQuestionStep = new SyncStep(() -> {
            questionService.removeQuestion(questionAggregateId, unitOfWork);
        });
    
        workflow.addStep(getQuestionStep);
        workflow.addStep(removeQuestionStep);
    
        workflow.execute();
    }


    public void updateQuestionTopics(Integer courseAggregateId, List<Integer> topicIds) throws Exception {
        String functionalityName = new Throwable().getStackTrace()[0].getMethodName();
        SagaUnitOfWork unitOfWork = unitOfWorkService.createUnitOfWork(functionalityName);
    
        UpdateQuestionTopicsData data = new UpdateQuestionTopicsData();
        SagaWorkflow workflow = new SagaWorkflow(data, unitOfWorkService, functionalityName);
    
        SyncStep getTopicsStep = new SyncStep(() -> {
            Set<QuestionTopic> topics = topicIds.stream()
                            .map(id -> topicService.getTopicById(id, unitOfWork))
                            .map(QuestionTopic::new)
                            .collect(Collectors.toSet());
            topics.forEach(t -> t.setState(AggregateState.IN_SAGA));
            data.setTopics(topics);
        });
    
        SyncStep getOldQuestionStep = new SyncStep(() -> {
            Question oldQuestion = (Question) unitOfWorkService.aggregateLoadAndRegisterRead(courseAggregateId, unitOfWork);
            oldQuestion.setState(AggregateState.IN_SAGA);
            Set<QuestionTopic> oldTopics = oldQuestion.getQuestionTopics();
            oldTopics.forEach(t -> t.setState(AggregateState.IN_SAGA));
            data.setOldQuestion(oldQuestion);
            data.setOldTopics(oldTopics);
        });
    
        getOldQuestionStep.registerCompensation(() -> {
            Question newQuestion = questionFactory.createQuestionFromExisting(data.getOldQuestion());
            newQuestion.setQuestionTopics(data.getOldTopics());
            data.getTopics().forEach(t -> t.setState(AggregateState.ACTIVE));
            data.getOldTopics().forEach(t -> t.setState(AggregateState.ACTIVE));
            newQuestion.setState(AggregateState.ACTIVE);
            unitOfWork.registerChanged(newQuestion);
        }, unitOfWork);
    
        SyncStep updateQuestionTopicsStep = new SyncStep(() -> {
            questionService.updateQuestionTopics(courseAggregateId, data.getTopics(), unitOfWork);
        });
    
        workflow.addStep(getTopicsStep);
        workflow.addStep(getOldQuestionStep);
        workflow.addStep(updateQuestionTopicsStep);
    
        workflow.execute();
    }

}
