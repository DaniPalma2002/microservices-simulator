package pt.ulisboa.tecnico.socialsoftware.blcm.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.blcm.quiz.dto.QuizDto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TournamentQuiz {
    @Column(name = "quiz_aggregate_id")
    private Integer aggregateId;
    @Column(name = "quiz_version")
    private Integer version;

    public TournamentQuiz() {

    }
    public TournamentQuiz(Integer aggregateId, Integer version) {
        setAggregateId(aggregateId);
        setVersion(version);
    }

    public TournamentQuiz(TournamentQuiz other) {
        setAggregateId(other.getAggregateId());
        setVersion(other.getVersion());
    }


    public Integer getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(Integer id) {
        this.aggregateId = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public QuizDto buildDto() {
        QuizDto quizDto = new QuizDto();
        quizDto.setAggregateId(getAggregateId());
        quizDto.setVersion(quizDto.getVersion());

        return  quizDto;
    }
}
