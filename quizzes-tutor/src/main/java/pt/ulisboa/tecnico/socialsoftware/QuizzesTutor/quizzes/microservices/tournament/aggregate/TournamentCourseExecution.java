package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.Tournament;

@Entity
public class TournamentCourseExecution {
    @Id
    @GeneratedValue
    private Long id;
    private Integer courseExecutionAggregateId;
    private Integer courseExecutionCourseId;
    private String courseExecutionAcronym;
    private String courseExecutionStatus;
    private Integer courseExecutionVersion;
    @OneToOne
    private pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.Tournament tournament;

    public TournamentCourseExecution() { }

    public TournamentCourseExecution(CourseExecutionDto courseExecutionDto) {
        setCourseExecutionAggregateId(courseExecutionDto.getAggregateId());
        setCourseExecutionVersion(courseExecutionDto.getVersion());
        setCourseExecutionCourseId(courseExecutionDto.getCourseAggregateId());
        setCourseExecutionAcronym(courseExecutionDto.getAcronym());
        setCourseExecutionStatus(courseExecutionDto.getStatus());
    }

    public TournamentCourseExecution(TournamentCourseExecution other) {
        setCourseExecutionAggregateId(other.getCourseExecutionAggregateId());
        setCourseExecutionVersion(other.getCourseExecutionVersion());
        setCourseExecutionCourseId(other.getCourseExecutionCourseId());
        setCourseExecutionAcronym(other.getCourseExecutionAcronym());
        setCourseExecutionStatus(other.getCourseExecutionStatus());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getCourseExecutionAggregateId() {
        return courseExecutionAggregateId;
    }

    public void setCourseExecutionAggregateId(Integer id) {
        this.courseExecutionAggregateId = id;
    }

    public Integer getCourseExecutionCourseId() {
        return courseExecutionCourseId;
    }

    public void setCourseExecutionCourseId(Integer courseExecutionCourseId) {
        this.courseExecutionCourseId = courseExecutionCourseId;
    }

    public String getCourseExecutionAcronym() {
        return courseExecutionAcronym;
    }

    public void setCourseExecutionAcronym(String courseExecutionAcronym) {
        this.courseExecutionAcronym = courseExecutionAcronym;
    }

    public String getCourseExecutionStatus() {
        return courseExecutionStatus;
    }

    public void setCourseExecutionStatus(String courseExecutionStatus) {
        this.courseExecutionStatus = courseExecutionStatus;
    }

    public Integer getCourseExecutionVersion() {
        return courseExecutionVersion;
    }

    public void setCourseExecutionVersion(Integer courseExecutionVersion) {
        this.courseExecutionVersion = courseExecutionVersion;
    }

    public pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public CourseExecutionDto buildDto() {
        CourseExecutionDto courseExecutionDto = new CourseExecutionDto();
        courseExecutionDto.setAggregateId(getCourseExecutionAggregateId());
        courseExecutionDto.setVersion(getCourseExecutionVersion());
        courseExecutionDto.setAcronym(getCourseExecutionAcronym());
        courseExecutionDto.setStatus(getCourseExecutionStatus());

        return courseExecutionDto;
    }
}
