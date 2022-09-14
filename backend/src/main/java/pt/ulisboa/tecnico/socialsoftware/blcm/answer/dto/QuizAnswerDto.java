package pt.ulisboa.tecnico.socialsoftware.blcm.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.blcm.answer.domain.AnswerQuiz;
import pt.ulisboa.tecnico.socialsoftware.blcm.answer.domain.AnswerUser;
import pt.ulisboa.tecnico.socialsoftware.blcm.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.blcm.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.blcm.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.blcm.utils.DateHandler;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class QuizAnswerDto implements Serializable {
    private Integer aggregateId;

    private Integer version;

    private String answerDate;

    private boolean completed;

    private Integer userAggregateId;

    private Integer quizAggregateId;

    //private List<QuestionAnswer> questionAnswers;

    public QuizAnswerDto(QuizAnswer quizAnswer) {
        setAggregateId(quizAnswer.getAggregateId());
        setVersion(quizAnswer.getVersion());
        setAnswerDate(DateHandler.toISOString(quizAnswer.getCreationDate()));
        setCompleted(quizAnswer.isCompleted());
        setUserAggregateId(quizAnswer.getUser().getAggregateId());
        setQuizAggregateId(quizAnswer.getQuiz().getAggregateId());
    }

    public Integer getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(Integer aggregateId) {
        this.aggregateId = aggregateId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Integer getUserAggregateId() {
        return userAggregateId;
    }

    public void setUserAggregateId(Integer userAggregateId) {
        this.userAggregateId = userAggregateId;
    }

    public Integer getQuizAggregateId() {
        return quizAggregateId;
    }

    public void setQuizAggregateId(Integer quizAggregateId) {
        this.quizAggregateId = quizAggregateId;
    }
}
