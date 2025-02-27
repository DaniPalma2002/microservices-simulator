package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate;

import jakarta.persistence.Embeddable;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.Option;

import java.io.Serializable;

@Embeddable
public class OptionDto implements Serializable {
    private Integer key;
    private Integer sequence;
    private boolean correct;
    private String content;

    public OptionDto() {
    }

    public OptionDto(Option option) {
        setKey(option.getOptionKey());
        setSequence(option.getSequence());
        setCorrect(option.isCorrect());
        setContent(option.getContent());
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
