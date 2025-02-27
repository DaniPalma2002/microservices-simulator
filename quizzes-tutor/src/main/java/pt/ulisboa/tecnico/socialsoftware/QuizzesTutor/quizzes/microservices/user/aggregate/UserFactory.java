package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate;

import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate.User;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate.UserDto;

public interface UserFactory {
    User createUser(Integer aggregateId, pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate.UserDto userDto);
    User createUserFromExisting(User existingUser);
    UserDto createUserDto(User user);
}
