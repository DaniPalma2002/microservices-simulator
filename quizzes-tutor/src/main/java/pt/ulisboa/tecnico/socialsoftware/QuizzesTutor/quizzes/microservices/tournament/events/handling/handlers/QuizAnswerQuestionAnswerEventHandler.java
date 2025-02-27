package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.TournamentEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.events.publish.QuizAnswerQuestionAnswerEvent;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.events.handling.handlers.TournamentEventHandler;

public class QuizAnswerQuestionAnswerEventHandler extends TournamentEventHandler {
    public QuizAnswerQuestionAnswerEventHandler(TournamentRepository tournamentRepository, TournamentEventProcessing tournamentEventProcessing) {
        super(tournamentRepository, tournamentEventProcessing);
    }

    @Override
    public void handleEvent(Integer subscriberAggregateId, Event event) {
        this.tournamentEventProcessing.processAnswerQuestionEvent(subscriberAggregateId, (QuizAnswerQuestionAnswerEvent) event);
    }
}
