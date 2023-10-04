package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.UnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.domain.UserTCC;
import pt.ulisboa.tecnico.socialsoftware.ms.causal.unityOfWork.UnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.user.event.publish.RemoveUserEvent;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.user.domain.Role;
import pt.ulisboa.tecnico.socialsoftware.ms.aggregate.service.AggregateIdGeneratorService;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.user.dto.UserDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.ms.quizzes.modules.exception.ErrorMessage.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AggregateIdGeneratorService aggregateIdGeneratorService;
    @Autowired
    private UnitOfWorkService unitOfWorkService;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto getUserById(Integer aggregateId, UnitOfWork unitOfWork) {
        return new UserDto((User) unitOfWorkService.aggregateLoadAndRegisterRead(aggregateId, unitOfWork));
    }

    /*simple user creation*/
    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto createUser(UserDto userDto, UnitOfWork unitOfWork) {
        Integer aggregateId = aggregateIdGeneratorService.getNewAggregateId();
        User user = new UserTCC(aggregateId, userDto);
        unitOfWork.registerChanged(user);
        return new UserDto(user);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void activateUser(Integer userAggregateId, UnitOfWork unitOfWork) {
        User oldUser = (User) unitOfWorkService.aggregateLoadAndRegisterRead(userAggregateId, unitOfWork);
        if (oldUser.isActive()) {
            throw new TutorException(USER_ACTIVE);
        }
        User newUser = new UserTCC((UserTCC) oldUser);
        newUser.setActive(true);
        unitOfWork.registerChanged(newUser);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(Integer userAggregateId, UnitOfWork unitOfWork) {
        User oldUser = (User) unitOfWorkService.aggregateLoadAndRegisterRead(userAggregateId, unitOfWork);
        User newUser = new UserTCC((UserTCC) oldUser);
        newUser.remove();
        unitOfWork.registerChanged(newUser);
        unitOfWork.addEvent(new RemoveUserEvent(newUser.getAggregateId()));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<UserDto> getStudents(UnitOfWork unitOfWork) {
        Set<Integer> studentsIds = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(Role.STUDENT))
                .map(User::getAggregateId)
                .collect(Collectors.toSet());
        return studentsIds.stream()
                .map(id -> (User) unitOfWorkService.aggregateLoadAndRegisterRead(id, unitOfWork))
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<UserDto> getTeachers(UnitOfWork unitOfWork) {
        Set<Integer> teacherIds = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(Role.TEACHER))
                .map(User::getAggregateId)
                .collect(Collectors.toSet());
        return teacherIds.stream()
                .map(id -> (User) unitOfWorkService.aggregateLoadAndRegisterRead(id, unitOfWork))
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
}
