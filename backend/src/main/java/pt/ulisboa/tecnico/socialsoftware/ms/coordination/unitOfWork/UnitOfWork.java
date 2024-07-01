package pt.ulisboa.tecnico.socialsoftware.ms.coordination.unitOfWork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.Aggregate;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;

public abstract class UnitOfWork {
    private Integer id;
    private Integer version;
    private Map<Integer, Aggregate> aggregatesToCommit;
    private Set<Event> eventsToEmit;
    private String functionalityName;

    private ArrayList<Runnable> compensatingActions;

    public UnitOfWork(Integer version, String functionalityName) {
        this.aggregatesToCommit = new HashMap<>();
        this.eventsToEmit = new HashSet<>();
        setVersion(version);
        this.functionalityName = functionalityName;
        this.compensatingActions = new ArrayList<>(); //TODO might be only for sagas
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

    public String getFunctionalityName() {
        return functionalityName;
    }

    public void setFunctionalityName(String functionalityName) {
        this.functionalityName = functionalityName;
    }

    public Map<Integer, Aggregate> getAggregatesToCommit() {
        return this.aggregatesToCommit;
    }

    public void registerChanged(Aggregate aggregate) {
        // the id set to null to force a new entry in the db
        aggregate.setId(null);
        this.aggregatesToCommit.put(aggregate.getAggregateId(), aggregate);
    }

    public Set<Event> getEventsToEmit() {
        return eventsToEmit;
    }

    public void addEvent(Event event) {
        this.eventsToEmit.add(event);
    }

    public void registerCompensation(Runnable compensationLogic) {
        this.compensatingActions.add(compensationLogic);
    }
}
