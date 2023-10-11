package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.Aggregate;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.AggregateIdGeneratorService;
import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.CausalUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.aggregates.CausalCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.CausalUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.course.service.CourseService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionStudent;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.events.publish.AnonymizeStudentEvent;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.events.publish.RemoveCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.events.publish.DisenrollStudentFromCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.events.publish.UpdateStudentNameEvent;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionCourse;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.user.aggregate.UserDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.exception.ErrorMessage.*;

@Service
public class CourseExecutionService {
    @Autowired
    private CourseExecutionRepository courseExecutionRepository;
    @Autowired
    private AggregateIdGeneratorService aggregateIdGeneratorService;
    @Autowired
    private CausalUnitOfWorkService unitOfWorkService;
    @Autowired
    private CourseService courseService;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CourseExecutionDto getCourseExecutionById(Integer executionAggregateId, CausalUnitOfWork unitOfWorkWorkService) {
        return new CourseExecutionDto((CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWorkWorkService));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CourseExecutionDto createCourseExecution(CourseExecutionDto courseExecutionDto, CausalUnitOfWork unitOfWork) {
        CourseExecutionCourse courseExecutionCourse = new CourseExecutionCourse(courseService.getAndOrCreateCourseRemote(courseExecutionDto, unitOfWork));

        CourseExecution courseExecution = new CausalCourseExecution(aggregateIdGeneratorService.getNewAggregateId(), courseExecutionDto, courseExecutionCourse);

        unitOfWork.registerChanged(courseExecution);
        return new CourseExecutionDto(courseExecution);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CourseExecutionDto> getAllCourseExecutions(CausalUnitOfWork unitOfWork) {
        return courseExecutionRepository.findCourseExecutionIdsOfAllNonDeleted().stream()
                .map(id -> (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(id, unitOfWork))
                .map(CourseExecutionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeCourseExecution(Integer executionAggregateId, CausalUnitOfWork unitOfWork) {
        CourseExecution oldCourseExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWork);
        CourseExecution newCourseExecution = new CausalCourseExecution((CausalCourseExecution) oldCourseExecution);

        /*
            REMOVE_COURSE_IS_VALID
         */
        Integer numberOfExecutionsOfCourse = Math.toIntExact(getAllCourseExecutions(unitOfWork).stream()
                .filter(ce -> ce.getCourseAggregateId() == newCourseExecution.getExecutionCourse().getCourseAggregateId())
                .count());
        if (numberOfExecutionsOfCourse == 1) {
            throw new TutorException(CANNOT_DELETE_COURSE_EXECUTION);
        }

        newCourseExecution.remove();
        unitOfWork.registerChanged(newCourseExecution);
        unitOfWork.addEvent(new RemoveCourseExecutionEvent(newCourseExecution.getAggregateId()));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void enrollStudent(Integer courseExecutionAggregateId, UserDto userDto, CausalUnitOfWork unitOfWork) {
        CourseExecution oldCourseExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(courseExecutionAggregateId, unitOfWork);

        CourseExecutionStudent courseExecutionStudent = new CourseExecutionStudent(userDto);
        if (!courseExecutionStudent.isActive()){
            throw new TutorException(ErrorMessage.INACTIVE_USER, courseExecutionStudent.getUserAggregateId());
        }

        CourseExecution newCourseExecution = new CausalCourseExecution((CausalCourseExecution) oldCourseExecution);
        newCourseExecution.addStudent(courseExecutionStudent);

        unitOfWork.registerChanged(newCourseExecution);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<CourseExecutionDto> getCourseExecutionsByUserId(Integer userAggregateId, CausalUnitOfWork unitOfWork) {
        return courseExecutionRepository.findAll().stream()
                .map(CourseExecution::getAggregateId)
                .map(aggregateId -> (CourseExecution) unitOfWorkService.aggregateLoad(aggregateId, unitOfWork))
                .filter(ce -> ce.hasStudent(userAggregateId))
                .map(courseExecution -> (CourseExecution) unitOfWorkService.registerRead(courseExecution, unitOfWork))
                .map(ce -> new CourseExecutionDto(ce))
                .collect(Collectors.toSet());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeStudentFromCourseExecution(Integer courseExecutionAggregateId, Integer userAggregateId, CausalUnitOfWork unitOfWork) {
        CourseExecution oldCourseExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(courseExecutionAggregateId, unitOfWork);
        CourseExecution newCourseExecution = new CausalCourseExecution((CausalCourseExecution) oldCourseExecution);
        newCourseExecution.removeStudent(userAggregateId);
        unitOfWork.registerChanged(newCourseExecution);
        unitOfWork.addEvent(new DisenrollStudentFromCourseExecutionEvent(courseExecutionAggregateId, userAggregateId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto getStudentByExecutionIdAndUserId(Integer executionAggregateId, Integer userAggregateId, CausalUnitOfWork unitOfWork) {
        CourseExecution courseExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWork);
        if (!courseExecution.hasStudent(userAggregateId)) {
            throw new TutorException(COURSE_EXECUTION_STUDENT_NOT_FOUND, userAggregateId, executionAggregateId);
        }
        return courseExecution.findStudent(userAggregateId).buildDto();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void anonymizeStudent(Integer executionAggregateId, Integer userAggregateId, CausalUnitOfWork unitOfWork) {
        CourseExecution oldExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWork);
        if (!oldExecution.hasStudent(userAggregateId)) {
            throw new TutorException(COURSE_EXECUTION_STUDENT_NOT_FOUND, userAggregateId, executionAggregateId);
        }
        CourseExecution newExecution = new CausalCourseExecution((CausalCourseExecution) oldExecution);
        newExecution.findStudent(userAggregateId).anonymize();
        unitOfWork.registerChanged(newExecution);
        unitOfWork.addEvent(new AnonymizeStudentEvent(executionAggregateId, "ANONYMOUS", "ANONYMOUS", userAggregateId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateExecutionStudentName(Integer executionAggregateId, Integer userAggregateId, String name, CausalUnitOfWork unitOfWork) {
        CourseExecution oldExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWork);
        if (!oldExecution.hasStudent(userAggregateId)) {
            throw new TutorException(COURSE_EXECUTION_STUDENT_NOT_FOUND, userAggregateId, executionAggregateId);
        }
        CourseExecution newExecution = new CausalCourseExecution((CausalCourseExecution) oldExecution);
        newExecution.findStudent(userAggregateId).setName(name);
        unitOfWork.registerChanged(newExecution);
        unitOfWork.addEvent(new UpdateStudentNameEvent(executionAggregateId, userAggregateId, name));
    }

    /************************************************ EVENT PROCESSING ************************************************/

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CourseExecution removeUser(Integer executionAggregateId, Integer userAggregateId, Integer aggregateEventVersion, CausalUnitOfWork unitOfWork) {
        CourseExecution oldExecution = (CourseExecution) unitOfWorkService.aggregateLoadAndRegisterRead(executionAggregateId, unitOfWork);
        CourseExecution newExecution = new CausalCourseExecution((CausalCourseExecution) oldExecution);
        newExecution.findStudent(userAggregateId).setState(Aggregate.AggregateState.INACTIVE);
        unitOfWork.registerChanged(newExecution);
        unitOfWork.addEvent(new DisenrollStudentFromCourseExecutionEvent(executionAggregateId, userAggregateId));
        return newExecution;
    }
}
