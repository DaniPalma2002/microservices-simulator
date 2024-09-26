package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.sagas.coordination.workflows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.course.service.CourseService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.Question;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionCourse;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.service.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.service.TopicService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaSyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

public class CreateQuestionFunctionalitySagas extends WorkflowFunctionality {
    private QuestionCourse course;
    private List<TopicDto> topics;
    private QuestionDto createdQuestion;

    

    private final QuestionService questionService;
    private final TopicService topicService;
    private final CourseService courseService;
    private final SagaUnitOfWorkService unitOfWorkService;

    public CreateQuestionFunctionalitySagas(QuestionService questionService, TopicService topicService, CourseService courseService, SagaUnitOfWorkService unitOfWorkService,  
                            Integer courseAggregateId, QuestionDto questionDto, SagaUnitOfWork unitOfWork) {
        this.questionService = questionService;
        this.topicService = topicService;
        this.courseService = courseService;
        this.unitOfWorkService = unitOfWorkService;
        this.buildWorkflow(courseAggregateId, questionDto, unitOfWork);
    }

    public void buildWorkflow(Integer courseAggregateId, QuestionDto questionDto, SagaUnitOfWork unitOfWork) {
        this.workflow = new SagaWorkflow(this, unitOfWorkService, unitOfWork);

        SagaSyncStep validateQuestionTopicsStep = new SagaSyncStep("validateQuestionTopicsStep", () -> {
            for (TopicDto topicDto : questionDto.getTopicDto()) {
                if (!topicDto.getCourseId().equals(courseAggregateId)) {
                    throw new TutorException(ErrorMessage.QUESTION_TOPIC_INVALID_COURSE, topicDto.getAggregateId(), courseAggregateId);
                }
            }
        });
    
        SagaSyncStep getCourseStep = new SagaSyncStep("getCourseStep", () -> {
            QuestionCourse course = new QuestionCourse(courseService.getCourseById(courseAggregateId, unitOfWork));
            this.setCourse(course);
        });
    
        SagaSyncStep getTopicsStep = new SagaSyncStep("getTopicsStep", () -> {
            List<TopicDto> topics = questionDto.getTopicDto().stream()
                    .map(topicDto -> topicService.getTopicById(topicDto.getAggregateId(), unitOfWork))
                    .collect(Collectors.toList());
            this.setTopics(topics);
        });
    
        SagaSyncStep createQuestionStep = new SagaSyncStep("createQuestionStep", () -> {
            QuestionDto createdQuestion = questionService.createQuestion(this.getCourse(), questionDto, this.getTopics(), unitOfWork);
            this.setCreatedQuestion(createdQuestion);
        }, new ArrayList<>(Arrays.asList(validateQuestionTopicsStep, getCourseStep, getTopicsStep)));
    
        createQuestionStep.registerCompensation(() -> {
            Question question = (Question) unitOfWorkService.aggregateLoadAndRegisterRead(this.getCreatedQuestion().getAggregateId(), unitOfWork);
            question.remove();
            unitOfWork.registerChanged(question);
        }, unitOfWork);
    
        workflow.addStep(validateQuestionTopicsStep);
        workflow.addStep(getCourseStep);
        workflow.addStep(getTopicsStep);
        workflow.addStep(createQuestionStep);
    }

    @Override
    public void handleEvents() {

    }    

    public QuestionCourse getCourse() {
        return course;
    }

    public void setCourse(QuestionCourse course) {
        this.course = course;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public QuestionDto getCreatedQuestion() {
        return createdQuestion;
    }

    public void setCreatedQuestion(QuestionDto createdQuestion) {
        this.createdQuestion = createdQuestion;
    }
}