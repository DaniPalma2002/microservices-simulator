package pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event;

import pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event.utils.EventType;
import pt.ulisboa.tecnico.socialsoftware.blcm.topic.domain.Topic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(EventType.UPDATE_TOPIC)
public class UpdateTopicEvent extends Event {

    private String topicName;

    public UpdateTopicEvent() {
        super();
    }

    public UpdateTopicEvent(Topic topic) {
        super(topic.getAggregateId());
        setTopicName(topic.getName());
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String name) {
        this.topicName = name;
    }
}
