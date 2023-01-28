package pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event;

import pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event.utils.EventType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(EventType.UPDATE_QUESTION)
public class UpdateQuestionEvent extends Event {
    private String title;

    private String content;

    public UpdateQuestionEvent() {
        super();
    }
    public UpdateQuestionEvent(Integer aggregateId, String title, String content) {
        super(aggregateId);
        setTitle(title);
        setContent(content);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
