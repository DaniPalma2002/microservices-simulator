package pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.blcm.causalconsistency.event.domain.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query(value = "select * from events where aggregate_id = :senderAggregateId AND aggregate_version > :lastVersion AND type = :eventType ORDER BY ts ASC" ,nativeQuery = true)
    List<Event> findByIdVersionType(Integer senderAggregateId, Integer lastVersion, String eventType);
}
