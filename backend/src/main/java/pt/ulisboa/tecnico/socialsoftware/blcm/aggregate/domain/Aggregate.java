package pt.ulisboa.tecnico.socialsoftware.blcm.aggregate.domain;

import pt.ulisboa.tecnico.socialsoftware.blcm.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.blcm.unityOfWork.Dependency;
import pt.ulisboa.tecnico.socialsoftware.blcm.unityOfWork.UnitOfWork;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

import static pt.ulisboa.tecnico.socialsoftware.blcm.aggregate.domain.Aggregate.AggregateState.DELETED;
import static pt.ulisboa.tecnico.socialsoftware.blcm.exception.ErrorMessage.CANNOT_PERFORM_CAUSAL_READ;

//@MappedSuperclass
@Entity
@Embeddable
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Aggregate {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "aggregate_id")
    private Integer aggregateId;

    @Column
    private Integer version;

    @Column(name = "creation_ts")
    private LocalDateTime creationTs;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private AggregateState state;

    public void remove() {
        setState(DELETED);
    }


    public enum AggregateState {
        ACTIVE,
        INACTIVE,
        DELETED
    }

    public Aggregate() {

    }

    /* used when creating a new aggregate*/
    public Aggregate(Integer aggregateId) {
        setAggregateId(aggregateId);
        setState(AggregateState.ACTIVE);
    }


    public abstract boolean verifyInvariants();

    //public abstract Aggregate merge(Aggregate prev, Aggregate v1, Aggregate v2);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDateTime getCreationTs() {
        return creationTs;
    }

    public void setCreationTs(LocalDateTime creationTs) {
        this.creationTs = creationTs;
    }

    public AggregateState getState() {
        return state;
    }

    public void setState(AggregateState state) {
        this.state = state;
    }


    public abstract Aggregate getPrev();

    public void checkDependencies(UnitOfWork unitOfWork) {
        for(Dependency dep : this.getDependenciesMap().values()) {
            // TODO fetch new version of one aggregate
            if (unitOfWork.hasAggregateDep(dep.getAggregateId()) && unitOfWork.getAggregateDep(dep.getAggregateId()).getVersion() < dep.getVersion()) {
                throw new TutorException(CANNOT_PERFORM_CAUSAL_READ, dep.getAggregateId());
            }
        }
    }

    public abstract Aggregate merge(Aggregate other);

    public abstract Map<Integer, Dependency> getDependenciesMap();
}
