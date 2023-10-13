package pt.ulisboa.tecnico.socialsoftware.ms.functionality

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.ms.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.ms.SpockTest
import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.Aggregate
import pt.ulisboa.tecnico.socialsoftware.ms.causal.version.VersionService
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.TutorException
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.CourseExecutionFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.QuestionFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.OptionDto
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.question.aggregate.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.QuizFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.TopicFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.topic.aggregate.TopicDto
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.TournamentFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.tournament.aggregate.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.tournament.events.handling.TournamentEventHandling
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.UserFunctionalities
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.user.aggregate.UserDto
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.utils.DateHandler

@DataJpaTest
class TournamentFunctionalityTest extends SpockTest {
    public static final String UPDATED_NAME = "UpdatedName"

    @Autowired
    private CourseExecutionFunctionalities courseExecutionFunctionalities
    @Autowired
    private UserFunctionalities userFunctionalities
    @Autowired
    private TopicFunctionalities topicFunctionalities
    @Autowired
    private QuestionFunctionalities questionFunctionalities
    @Autowired
    private QuizFunctionalities quizFunctionalities
    @Autowired
    private TournamentFunctionalities tournamentFunctionalities

    @Autowired
    private VersionService versionService;

    @Autowired
    private TournamentEventHandling tournamentEventDetection

    private CourseExecutionDto courseExecutionDto
    private UserDto userCreatorDto, userDto
    private TopicDto topicDto1, topicDto2, topicDto3
    private QuestionDto questionDto1, questionDto2, questionDto3
    private TournamentDto tournamentDto

    def setup() {
        given: 'a course execution'
        courseExecutionDto = new CourseExecutionDto()
        courseExecutionDto.setName('BLCM')
        courseExecutionDto.setType('TECNICO')
        courseExecutionDto.setAcronym('TESTBLCM')
        courseExecutionDto.setAcademicTerm('2022/2023')
        courseExecutionDto.setEndDate(DateHandler.toISOString(TIME_4))
        courseExecutionDto = courseExecutionFunctionalities.createCourseExecution(courseExecutionDto)

        userCreatorDto = new UserDto()
        userCreatorDto.setName('Name' + 1)
        userCreatorDto.setUsername('Username' + 1)
        userCreatorDto.setRole('STUDENT')
        userCreatorDto = userFunctionalities.createUser(userCreatorDto)

        userFunctionalities.activateUser(userCreatorDto.getAggregateId())

        courseExecutionFunctionalities.addStudent(courseExecutionDto.getAggregateId(), userCreatorDto.getAggregateId())

        userDto = new UserDto()
        userDto.setName('Name' + 2)
        userDto.setUsername('Username' + 2)
        userDto.setRole('STUDENT')
        userDto = userFunctionalities.createUser(userDto)

        userFunctionalities.activateUser(userDto.aggregateId)

        courseExecutionFunctionalities.addStudent(courseExecutionDto.aggregateId, userDto.aggregateId)

        topicDto1 = new TopicDto()
        topicDto1.setName('Topic' + 1)
        topicDto1 = topicFunctionalities.createTopic(courseExecutionDto.getCourseAggregateId(), topicDto1)
        topicDto2 = new TopicDto()
        topicDto2.setName('Topic' + 2)
        topicDto2 = topicFunctionalities.createTopic(courseExecutionDto.getCourseAggregateId(), topicDto2)
        topicDto3 = new TopicDto()
        topicDto3.setName('Topic' + 3)
        topicDto3 = topicFunctionalities.createTopic(courseExecutionDto.getCourseAggregateId(), topicDto3)

        questionDto1 = new QuestionDto()
        questionDto1.setTitle('Title' + 1)
        questionDto1.setContent('Content' + 1)
        def set =  new HashSet<>(Arrays.asList(topicDto1));
        questionDto1.setTopicDto(set)
        def optionDto1 = new OptionDto()
        optionDto1.setSequence(1)
        optionDto1.setCorrect(true)
        optionDto1.setContent("Option" + 1)
        def optionDto2 = new OptionDto()
        optionDto2.setSequence(2)
        optionDto2.setCorrect(false)
        optionDto2.setContent("Option" + 2)
        questionDto1.setOptionDtos([optionDto1,optionDto2])
        questionDto1 = questionFunctionalities.createQuestion(courseExecutionDto.getCourseAggregateId(), questionDto1)

        questionDto2 = new QuestionDto()
        questionDto2.setTitle('Title' + 2)
        questionDto2.setContent('Content' + 2)
        set =  new HashSet<>(Arrays.asList(topicDto2));
        questionDto2.setTopicDto(set)
        questionDto2.setOptionDtos([optionDto1,optionDto2])
        questionDto2 = questionFunctionalities.createQuestion(courseExecutionDto.getCourseAggregateId(), questionDto2)

        questionDto3 = new QuestionDto()
        questionDto3.setTitle('Title' + 2)
        questionDto3.setContent('Content' + 2)
        set =  new HashSet<>(Arrays.asList(topicDto3));
        questionDto3.setTopicDto(set)
        questionDto3.setOptionDtos([optionDto1,optionDto2])
        questionDto3 = questionFunctionalities.createQuestion(courseExecutionDto.getCourseAggregateId(), questionDto3)

        tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(DateHandler.toISOString(TIME_1))
        tournamentDto.setEndTime(DateHandler.toISOString(TIME_3))
        tournamentDto.setNumberOfQuestions(2)
        tournamentDto = tournamentFunctionalities.createTournament(userCreatorDto.getAggregateId(), courseExecutionDto.getAggregateId(),
                [topicDto1.getAggregateId(),topicDto2.getAggregateId()], tournamentDto)
    }

    def cleanup() {

    }

    // update name in course execution and add student in tournament

    def 'sequential update name in course execution and then add student as tournament participant' () {
        given: 'student name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userDto.getAggregateId(), updateNameDto)

        when: 'student is added to tournament'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        then: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
        and: 'the name is updated in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.getParticipants().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
    }

    def 'sequential add student as tournament participant and then update name in course execution' () {
        given: 'student is added to tournament'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())
        and: 'student name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userDto.getAggregateId(), updateNameDto)

        when: 'update name event is processed'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent();

        then: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
        and: 'the name is updated in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.getParticipants().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
    }

    def 'concurrent add student as tournament participant and update name in course execution - add student finishes first' () {
        given: 'student is added to tournament'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()
        and: 'student name is updated and the commit does not require merge'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userDto.getAggregateId(), updateNameDto)

        when: 'update name event is processed such that the participant is updated in tournament'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent();

        then: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
        and: 'the name is updated in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.getParticipants().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
    }

    def 'concurrent add student as tournament participant and update name in course execution - update name finishes first' () {
        given: 'student name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userDto.getAggregateId(), updateNameDto)
        and: 'try to process update name event but there are no subscribers'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent();
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()
        and: 'student is added to tournament but uses the version without update'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        when: 'update name event is processed such that the participant is updated in tournament'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent();

        then: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
        and: 'the name is updated in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.getParticipants().find{it.aggregateId == userDto.aggregateId}.name == UPDATED_NAME
    }

    // update creator name in course execution and add creator in tournament

    def 'sequential update creator name in course execution and add creator as tournament participant: fails because when creator is added tournament have not process the event yet' () {
        given: 'creator name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userCreatorDto.getAggregateId(), updateNameDto)

        when: 'add creator as participant where the creator in tournament still has the old name'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userCreatorDto.getAggregateId())

        then: 'fails because course execution emitted the update event but it was not processed by the tournament'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_PERFORM_CAUSAL_READ_DUE_TO_EMITTED_EVENT_NOT_PROCESSED
        and: 'when event is finally processed it updates the creator name'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent()
        and: 'creator can be added as participant because tournament has processed all events it subscribes from course execution'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userCreatorDto.getAggregateId())
        and: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
        and: 'the creator is update in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.creator.name == UPDATED_NAME
        and: 'the creator is participant with updated name'
        tournamentDtoResult.getParticipants().size() == 1
        tournamentDtoResult.getParticipants().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
    }

    def 'sequential add creator as tournament participant and update creator name in course execution' () {
        given: 'add creator as participant'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userCreatorDto.getAggregateId())
        and: 'creator name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userCreatorDto.getAggregateId(), updateNameDto)

        when: 'when event is processed it updates the creator name'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent()

        then: 'the name is update and the creator is a participant'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
        and: 'the creator is update in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.creator.name == UPDATED_NAME
        and: 'the creator is participant with updated name'
        tournamentDtoResult.getParticipants().size() == 1
        tournamentDtoResult.getParticipants().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
    }

    def 'concurrent add creator as tournament participant and update name in course execution: update name finishes first and event processing is concurrent with add creator' () {
        given: 'creator name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userCreatorDto.getAggregateId(), updateNameDto)
        and: 'process update name event which updates the name of the creator in the tournament'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent();
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()

        when: 'trying to add creator as participant using the version of the user before update'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userCreatorDto.getAggregateId())

        then: 'fails because the event tournament subscribes an event that it has not processed, ' +
                'the course execution emitted the event and it is subscribed due to the participant which of a older version'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_PERFORM_CAUSAL_READ_DUE_TO_EMITTED_EVENT_NOT_PROCESSED
        and: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
        and: 'the creator is update in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.creator.name == UPDATED_NAME
        and: 'there is no participants'
        tournamentDtoResult.getParticipants().size() == 0
    }

    def 'concurrent add creator as tournament participant and update name in course execution - update name finishes first and event processing starts before add creator finishes' () {
        given: 'creator name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userCreatorDto.getAggregateId(), updateNameDto)
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()
        and: 'add creator as participant which uses a previous version of the name, creator and participant have the same info'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userCreatorDto.getAggregateId())
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()

        when: 'process update name in the tournament that does not have participant, so only the creator is updated, and' +
                'when merging with the tournament that has participant, creator and participant have different names'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent()

        then: 'fails because invariant about same info for creator and participant, if the creator, breaks'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.INVARIANT_BREAK
        and: 'process update name event using tournament version that has the creator and the participant'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent();
        and: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
        and: 'the creator is update in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.creator.name == UPDATED_NAME
        and: 'the creator is participant with updated name'
        tournamentDtoResult.getParticipants().size() == 1
        tournamentDtoResult.getParticipants().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
    }

    def 'concurrent add creator as tournament participant and update name in course execution: add creator finishes first' () {
        given: 'add creator as participant'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userCreatorDto.getAggregateId())
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()
        and: 'creator name is updated'
        def updateNameDto = new UserDto()
        updateNameDto.setName(UPDATED_NAME)
        courseExecutionFunctionalities.updateExecutionStudentName(courseExecutionDto.getAggregateId(), userCreatorDto.getAggregateId(), updateNameDto)

        when: 'the event is processed'
        tournamentEventDetection.handleUpdateExecutionStudentNameEvent()

        then: 'the name is updated in course execution'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
        and: 'the creator is update in tournament'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.creator.name == UPDATED_NAME
        and: 'the creator is a participant with the correct name'
        tournamentDtoResult.getParticipants().size() == 1
        tournamentDtoResult.getParticipants().find{it.aggregateId == userCreatorDto.aggregateId}.name == UPDATED_NAME
    }

    // anonymize creator in course execution and add student in tournament

    def 'sequential anonymize creator and add student: event is processed before add student' () {
        given: 'anonymize creator'
        courseExecutionFunctionalities.anonymizeStudent(courseExecutionDto.aggregateId, userCreatorDto.aggregateId)
        and: 'tournament processes event to anonymize the creator'
        tournamentEventDetection.handleAnonymizeStudentEvents()

        when: 'a student is added to a tournament'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        then: 'fails during commit because tournament is inactive due to anonymous creator'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_MODIFY_INACTIVE_AGGREGATE
        and: 'creator is anonymized'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == ANONYMOUS
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.username == ANONYMOUS
        and: 'the tournament is inactive and the creator anonymized'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.state == Aggregate.AggregateState.INACTIVE.toString()
        tournamentDtoResult.creator.name == ANONYMOUS
        tournamentDtoResult.creator.username == ANONYMOUS
        and: 'there are no participants'
        tournamentDtoResult.getParticipants().size() == 0
    }

    def 'sequential anonymize creator and add student: event is processed after add student' () {
        given: 'anonymize creator'
        courseExecutionFunctionalities.anonymizeStudent(courseExecutionDto.aggregateId, userCreatorDto.aggregateId)

        when: 'a student is added to a tournament'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        then: 'fails because it is not possible to get a causal snapshot, ' +
                'course execution emitted an event that is not processed by tournament'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_PERFORM_CAUSAL_READ_DUE_TO_EMITTED_EVENT_NOT_PROCESSED
        and: 'tournament processes event to anonymize the creator'
        tournamentEventDetection.handleAnonymizeStudentEvents()
        and: 'creator is anonymized'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == ANONYMOUS
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.username == ANONYMOUS
        and: 'the tournament is inactive and the creator anonymized'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.state == Aggregate.AggregateState.INACTIVE.toString()
        tournamentDtoResult.creator.name == ANONYMOUS
        tournamentDtoResult.creator.username == ANONYMOUS
        and: 'there are no participants'
        tournamentDtoResult.getParticipants().size() == 0
    }

    def 'concurrent anonymize creator and add student: anonymize finishes first' () {
        given: 'anonymize creator'
        courseExecutionFunctionalities.anonymizeStudent(courseExecutionDto.aggregateId, userCreatorDto.aggregateId)
        and: 'tournament processes event to anonymize the creator'
        tournamentEventDetection.handleAnonymizeStudentEvents()
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()

        when: 'another student is concurrently added to a tournament where the creator was anonymized in the course execution' +
                'but not in the tournament'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        then: 'fails during merge because course execution emitted an event that was not processed by the tournament version'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_PERFORM_CAUSAL_READ_DUE_TO_EMITTED_EVENT_NOT_PROCESSED
        and: 'creator is anonymized'
        def courseExecutionDtoResult = courseExecutionFunctionalities.getCourseExecutionByAggregateId(courseExecutionDto.getAggregateId())
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.name == ANONYMOUS
        courseExecutionDtoResult.getStudents().find{it.aggregateId == userCreatorDto.aggregateId}.username == ANONYMOUS
        and: 'the tournament is inactive and the creator anonymized'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.state == Aggregate.AggregateState.INACTIVE.toString()
        tournamentDtoResult.creator.name == ANONYMOUS
        tournamentDtoResult.creator.username == ANONYMOUS
        and: 'there are no participants'
        tournamentDtoResult.getParticipants().size() == 0
    }

    // delete tournament and add student in tournament

    def 'sequential remove tournament and add student' () {
        given: 'remove tournament'
        tournamentFunctionalities.removeTournament(tournamentDto.aggregateId)

        when: 'a student is added to a tournament that is removed'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        then: 'fails because the the tournament is not found'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.AGGREGATE_NOT_FOUND
    }

    def 'concurrent remove tournament and add student: remove finishes first' () {
        given: 'remove tournament'
        tournamentFunctionalities.removeTournament(tournamentDto.aggregateId)
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()

        when: 'a student is concurrently added to a tournament that is not deleted'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())

        then: 'fails during merge because the most recent version of the tournament is deleted'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.AGGREGATE_DELETED
    }

    def 'concurrent remove tournament and add student: add student finishes first' () {
        given: 'add student'
        tournamentFunctionalities.addParticipant(tournamentDto.getAggregateId(), userDto.getAggregateId())
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()

        when: 'remove tournament concurrently with add'
        tournamentFunctionalities.removeTournament(tournamentDto.aggregateId)

        then: 'fails during merge because breaks invariant that forbids to delete a tournament with participants'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.INVARIANT_BREAK
    }

    // delete tournament and update start time

    def 'concurrent remove tournament and update start time: update start time finishes first' () {
        given: 'update start time'
        def updateTournamentDto = new TournamentDto()
        updateTournamentDto.setAggregateId(tournamentDto.aggregateId)
        updateTournamentDto.setStartTime(DateHandler.toISOString(TIME_2))
        def topics =  new HashSet<>(Arrays.asList(topicDto1.aggregateId,topicDto2.aggregateId))
        tournamentFunctionalities.updateTournament(updateTournamentDto, topics)
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()
        and: 'remove tournament which merges with the update'
        tournamentFunctionalities.removeTournament(tournamentDto.aggregateId)

        when: 'find the tournament'
        tournamentFunctionalities.findTournament(tournamentDto.aggregateId)

        then: 'after merge the tournament is removed, not found'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.AGGREGATE_NOT_FOUND
    }

    // update topics in tournament and update topics in tournament

    def 'concurrent change of tournament topics' () {
        given: 'update topics to topic 2'
        def updateTournamentDto = new TournamentDto()
        updateTournamentDto.setAggregateId(tournamentDto.aggregateId)
        updateTournamentDto.setNumberOfQuestions(1)
        def topics =  new HashSet<>(Arrays.asList(topicDto2.aggregateId))
        tournamentFunctionalities.updateTournament(updateTournamentDto, topics)
        and: 'the version number is decreased to simulate concurrency'
        versionService.decrementVersionNumber()

        when: 'update topics to topic 3 in the same concurrent version of the tournament'
        topics =  new HashSet<>(Arrays.asList(topicDto3.aggregateId));
        tournamentFunctionalities.updateTournament(updateTournamentDto, topics)

        then: 'as result of the merge a new quiz is created for the last committed topics'
        def quizDtoResult = quizFunctionalities.findQuiz(tournamentDto.quiz.aggregateId)
        quizDtoResult.questionDtos.size() == 1
        quizDtoResult.questionDtos.get(0).aggregateId == questionDto3.aggregateId
        and: 'the tournament topics are updated and it refers to the new quiz'
        def tournamentDtoResult = tournamentFunctionalities.findTournament(tournamentDto.getAggregateId())
        tournamentDtoResult.topics.size() == 1
        tournamentDtoResult.topics.find{it.aggregateId == topicDto3.aggregateId} != null
        tournamentDtoResult.quiz.aggregateId == tournamentDto.quiz.aggregateId
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}