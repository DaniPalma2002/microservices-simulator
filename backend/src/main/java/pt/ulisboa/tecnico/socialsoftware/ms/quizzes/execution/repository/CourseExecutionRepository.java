package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.execution.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.execution.domain.CourseExecution;

import java.util.Set;

@Repository
@Transactional
public interface CourseExecutionRepository extends JpaRepository<CourseExecution, Integer> {
    @Query(value = "select ce1.aggregateId from CourseExecution ce1 where ce1.aggregateId NOT IN (select ce2.aggregateId from CourseExecution ce2 where ce2.state = 'DELETED')")
    Set<Integer> findCourseExecutionIdsOfAllNonDeleted();
}
