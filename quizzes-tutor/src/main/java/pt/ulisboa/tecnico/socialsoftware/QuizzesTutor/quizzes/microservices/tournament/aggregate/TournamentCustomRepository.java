package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate;

import java.util.Set;

public interface TournamentCustomRepository {
    Set<Integer> findAllRelevantTournamentIds(Integer executionAggregateId);
}