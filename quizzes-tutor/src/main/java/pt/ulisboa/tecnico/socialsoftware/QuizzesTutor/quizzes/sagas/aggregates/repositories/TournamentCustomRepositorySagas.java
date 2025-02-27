package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.TournamentCustomRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.TournamentRepository;

import java.util.Set;

@Service
@Profile("sagas")
public class TournamentCustomRepositorySagas implements TournamentCustomRepository {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public Set<Integer> findAllRelevantTournamentIds(Integer executionAggregateId) {
        return tournamentRepository.findAllTournamentIdsOfNotDeletedAndNotInactiveByCourseExecutionForSaga(executionAggregateId);
    }
}