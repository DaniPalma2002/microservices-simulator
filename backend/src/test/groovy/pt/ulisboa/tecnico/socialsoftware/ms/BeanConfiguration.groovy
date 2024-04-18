package pt.ulisboa.tecnico.socialsoftware.ms

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventApplicationService
import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventService

import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalQuizAnswerFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.answer.events.handling.QuizAnswerEventHandling
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.eventProcessing.QuizAnswerEventProcessing
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.answer.service.QuizAnswerService
import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.AggregateIdGeneratorService

import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.CausalUnitOfWorkService
import pt.ulisboa.tecnico.socialsoftware.ms.causal.version.VersionService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CourseCustomRepositoryTCC   
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.QuizAnswerCustomRepositoryTCC   
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.TournamentCustomRepositoryTCC
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CourseExecutionCustomRepositoryTCC;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.user.aggregate.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.quiz.aggregate.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.course.aggregate.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionRepository;

import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.course.service.CourseService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalCourseExecutionFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.events.handling.CourseExecutionEventHandling
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.eventProcessing.CourseExecutionEventProcessing
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.service.CourseExecutionService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalQuestionFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.events.handling.QuestionEventHandling
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.eventProcessing.QuestionEventProcessing
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.service.QuestionService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalQuizFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.quiz.events.handling.QuizEventHandling
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.eventProcessing.QuizEventProcessing
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.quiz.service.QuizService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalTopicFunctionalities

import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.service.TopicService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalTournamentFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.tournament.events.handling.TournamentEventHandling
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.eventProcessing.TournamentEventProcessing
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.tournament.service.TournamentService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.functionalities.CausalUserFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.user.service.UserService

@TestConfiguration
@PropertySource("classpath:application-test.properties")
class BeanConfiguration {
    @Bean
    AggregateIdGeneratorService aggregateIdGeneratorService() {
        return new AggregateIdGeneratorService();
    }

    @Bean
    VersionService versionService() {
        return new VersionService();
    }

    @Bean
    EventApplicationService eventApplicationService() {
        return new EventApplicationService();
    }

    @Bean
    EventService eventService() {
        return new EventService();
    }

    @Bean
    CausalUnitOfWorkService unitOfWorkService() {
        return new CausalUnitOfWorkService();
    }

    @Bean
    CausalCourseExecutionFunctionalities courseExecutionFunctionalities() {
        return new CausalCourseExecutionFunctionalities()
    }

    @Bean
    CourseExecutionEventProcessing courseExecutionEventProcessing(CausalUnitOfWorkService unitOfWorkService) {
        return new CourseExecutionEventProcessing(unitOfWorkService)
    }

    @Bean
    CausalUserFunctionalities userFunctionalities() {
        return new CausalUserFunctionalities()
    }

    @Bean
    CausalTopicFunctionalities topicFunctionalities() {
        return new CausalTopicFunctionalities()
    }

    @Bean
    CausalQuestionFunctionalities questionFunctionalities() {
        return new CausalQuestionFunctionalities()
    }

    @Bean
    QuestionEventProcessing questionEventProcessing(CausalUnitOfWorkService unitOfWorkService) {
        return new QuestionEventProcessing(unitOfWorkService);
    }

    @Bean
    CausalQuizFunctionalities quizFunctionalities() {
        return new CausalQuizFunctionalities()
    }

    @Bean
    QuizEventProcessing quizEventProcessing(CausalUnitOfWorkService unitOfWorkService) {
        return new QuizEventProcessing(unitOfWorkService);
    }

    @Bean
    CausalQuizAnswerFunctionalities answerFunctionalities() {
        return new CausalQuizAnswerFunctionalities()
    }

    @Bean
    QuizAnswerEventProcessing answerEventProcessing(CausalUnitOfWorkService unitOfWorkService) {
        return new QuizAnswerEventProcessing(unitOfWorkService)
    }

    @Bean
    CausalTournamentFunctionalities tournamentFunctionalities() {
        return new CausalTournamentFunctionalities()
    }

    @Bean
    TournamentEventProcessing tournamentEventProcessing(CausalUnitOfWorkService unitOfWorkService) {
        return new TournamentEventProcessing(unitOfWorkService)
    }

    @Bean
    CourseCustomRepositoryTCC courseCustomRepositoryTCC(){
        return new CourseCustomRepositoryTCC()
    }

    @Bean
    CourseExecutionCustomRepositoryTCC courseExecutionCustomRepositoryTCC(){
        return new CourseExecutionCustomRepositoryTCC()
    }

    @Bean
    TournamentCustomRepositoryTCC tournamentCustomRepositoryTCC(){
        return new TournamentCustomRepositoryTCC()
    }

    @Bean
    QuizAnswerCustomRepositoryTCC quizAnswerCustomRepositoryTCC(){
        return new QuizAnswerCustomRepositoryTCC()
    }

    @Bean
    CourseService courseService(CausalUnitOfWorkService unitOfWorkService, CourseCustomRepositoryTCC courseRepository) {
        return new CourseService(unitOfWorkService, courseRepository)
    }

    @Bean
    QuizAnswerService answerService(CausalUnitOfWorkService unitOfWorkService, QuizAnswerCustomRepositoryTCC quizAnswerRepository) {
        return new QuizAnswerService(unitOfWorkService, quizAnswerRepository)
    }

    @Bean
    TournamentService tournamentService(CausalUnitOfWorkService unitOfWorkService, TournamentCustomRepositoryTCC tournamentRepository) {
        return new TournamentService(unitOfWorkService, tournamentRepository)
    }

    @Bean
    CourseExecutionService courseExecutionService(CausalUnitOfWorkService unitOfWorkService, CourseExecutionRepository courseExecutionRepository, CourseExecutionCustomRepositoryTCC courseExecutionCustomRepository) {
        return new CourseExecutionService(unitOfWorkService, courseExecutionRepository, courseExecutionCustomRepository)
    }

    @Bean
    UserService userService(CausalUnitOfWorkService unitOfWorkService, UserRepository userRepository) {
        return new UserService(unitOfWorkService, userRepository)
    }

    @Bean
    TopicService topicService(CausalUnitOfWorkService unitOfWorkService, TopicRepository topicRepository) {
        return new TopicService(unitOfWorkService, topicRepository)
    }

    @Bean
    QuestionService questionService(CausalUnitOfWorkService unitOfWorkService, QuestionRepository questionRepository) {
        return new QuestionService(unitOfWorkService, questionRepository)
    }

    @Bean
    QuizService quizService(CausalUnitOfWorkService unitOfWorkService, QuizRepository quizRepository) {
        return new QuizService(unitOfWorkService, quizRepository)
    }

    @Bean
    CourseExecutionEventHandling courseExecutionEventDetection() {
        return new CourseExecutionEventHandling()
    }

    @Bean
    QuestionEventHandling questionEventDetection() {
        return new QuestionEventHandling()
    }

    @Bean
    QuizEventHandling quizEventDetection() {
        return new QuizEventHandling()
    }

    @Bean
    QuizAnswerEventHandling answerEventDetection() {
        return new QuizAnswerEventHandling()
    }

    @Bean
    TournamentEventHandling tournamentEventDetection() {
        return new TournamentEventHandling()
    }
}