package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.aggregate.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
}
