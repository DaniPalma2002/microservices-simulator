package pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.unityOfWork;

import pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.aggregate.domain.EventSubscription;
import pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event.Event;
import pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.aggregate.domain.Aggregate;
import pt.ulisboa.tecnico.socialsoftware.blcm.exception.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.blcm.exception.TutorException;

import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.blcm.exception.ErrorMessage.CANNOT_MODIFY_INACTIVE_AGGREGATE;
import static pt.ulisboa.tecnico.socialsoftware.blcm.exception.ErrorMessage.CANNOT_PERFORM_CAUSAL_READ;



public class UnitOfWork {

    private Integer id;

    private Integer version;

    private Map<Integer, Aggregate> aggregatesToCommit;

    private Set<Aggregate> aggregatesToDelete;

    private Set<Event> eventsToEmit;

    private Map<Integer, Aggregate> causalSnapshot;

    public UnitOfWork() {

    }

    public UnitOfWork(Integer version) {
        this.aggregatesToCommit = new HashMap<>();
        this.aggregatesToDelete = new HashSet<>();
        this.eventsToEmit = new HashSet<>();
        this.causalSnapshot = new HashMap<>();
        setVersion(version);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<Integer, Aggregate> getAggregatesToCommit() {
        return aggregatesToCommit;
    }


    public void registerChanged(Aggregate aggregate) {
        if(aggregate.getState() == Aggregate.AggregateState.INACTIVE) {
            throw new TutorException(CANNOT_MODIFY_INACTIVE_AGGREGATE, aggregate.getAggregateId());
        }
        // the id set to null to force a new entry in the db
        aggregate.setId(null);
        this.aggregatesToCommit.put(aggregate.getAggregateId(), aggregate);
    }

    public Set<Aggregate> getAggregatesToDelete() {
        return aggregatesToDelete;
    }

    public void registerForDeletion(Aggregate aggregate) {
        if(aggregate.getState() == Aggregate.AggregateState.INACTIVE) {
            throw new TutorException(CANNOT_MODIFY_INACTIVE_AGGREGATE, aggregate.getAggregateId());
        }
        this.aggregatesToDelete.add(aggregate);
    }

    public Set<Event> getEventsToEmit() {
        return eventsToEmit;
    }

    public void addEvent(Event event) {
        this.eventsToEmit.add(event);
    }

    public void addToCausalSnapshot(Aggregate aggregate, List<Event> allEvents) {
        verifyProcessedEventsByAggregate(aggregate);
        verifyEmittedEventsByAggregate(aggregate);
        verifySameProcessedEvents(aggregate, allEvents);
        addAggregateToSnapshot(aggregate);
    }

    private void verifyProcessedEventsByAggregate(Aggregate aggregate) {
        for (EventSubscription es : aggregate.getEventSubscriptions()) {
            for (Aggregate snapshotAggregate : this.causalSnapshot.values()) {
                if (es.getSenderAggregateId().equals(snapshotAggregate.getAggregateId())) {
                    if(!es.getSenderLastVersion().equals(snapshotAggregate.getVersion())) {
                        Map<String, Integer> snapshotAggregateEmittedEvents = snapshotAggregate.getEmittedEvents();
                        if (snapshotAggregateEmittedEvents.containsKey(es.getEventType()) && snapshotAggregateEmittedEvents.get(es.getEventType()) > es.getSenderLastVersion()) {
                            throw new TutorException(CANNOT_PERFORM_CAUSAL_READ, aggregate.getAggregateId(), getVersion());
                        }
                    }
                    // if event version equals aggregate in snapshot version  OK
                    // else get all events of type for aggregate id which is higher than subscribed version and lower or equal than aggregate in snapshot version
                    // confirm that these events are processed
                }
            }
        }
    }

    private void verifyEmittedEventsByAggregate(Aggregate aggregate) {
        Map<String, Integer> aggregateEmittedEvents = aggregate.getEmittedEvents();
        for (Aggregate snapshotAggregate : this.causalSnapshot.values()) {
            for (EventSubscription es : snapshotAggregate.getEventSubscriptions()) {
                if (es.getSenderAggregateId().equals(aggregate.getAggregateId())) {
                    if(!es.getSenderLastVersion().equals(snapshotAggregate.getVersion())) {
                        if (aggregateEmittedEvents.containsKey(es.getEventType()) && aggregateEmittedEvents.get(es.getEventType()) > es.getSenderLastVersion()) {
                            throw new TutorException(CANNOT_PERFORM_CAUSAL_READ, aggregate.getAggregateId(), getVersion());
                        }
                    }
                }
            }
        }
    }

    private void verifySameProcessedEvents(Aggregate aggregate, List<Event> allEvents) {
        Set<EventSubscription> aggregateEventSubscriptions = aggregate.getEventSubscriptions();
        for(Aggregate snapshotAggregate : this.causalSnapshot.values()) {
            for(EventSubscription es1 : aggregateEventSubscriptions)
                for(EventSubscription es2 : snapshotAggregate.getEventSubscriptions()) {
                    // if they correspond to the same aggregate and type
                    if(es1.getSenderAggregateId().equals(es2.getSenderAggregateId()) && es1.getEventType().equals(es2.getEventType()) && !es1.getSenderLastVersion().equals(es2.getSenderLastVersion())) {
                        Integer minVersion = Math.min(es1.getSenderLastVersion(), es2.getSenderLastVersion());
                        Integer maxVersion = Math.max(es1.getSenderLastVersion(), es2.getSenderLastVersion());
                        Long numberOfEventsBetweenAggregates = allEvents.stream()
                                .filter(event -> event.getAggregateId().equals(es1.getSenderAggregateId()))
                                .filter(event -> event.getType().equals(es1.getEventType()))
                                .filter(event -> minVersion < event.getAggregateVersion() && event.getAggregateVersion() <= maxVersion)
                                .count();
                        if(numberOfEventsBetweenAggregates > 0) {
                            throw new TutorException(CANNOT_PERFORM_CAUSAL_READ, aggregate.getAggregateId(), getVersion());
                        }
                    }
                }
        }
    }

    private void addAggregateToSnapshot(Aggregate aggregate) {
        if(!this.causalSnapshot.containsKey(aggregate.getAggregateId()) || !(aggregate.getVersion() <= this.causalSnapshot.get(aggregate.getAggregateId()).getVersion())) {
            this.causalSnapshot.put(aggregate.getAggregateId(), aggregate);
        }
    }
}
