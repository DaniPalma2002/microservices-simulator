package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.course.aggregate.CourseType;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionDto;

@Entity
public class CourseExecutionCourse {
    @Id
    @GeneratedValue
    private Long id;
    private Integer courseAggregateId;
    private String name;
    @Enumerated(EnumType.STRING)
    private CourseType type;
    private Integer courseVersion;
    @OneToOne
    private pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution courseExecution;

    public CourseExecutionCourse() {}

    public CourseExecutionCourse(CourseExecutionDto courseExecutionDto) {
        setCourseAggregateId(courseExecutionDto.getCourseAggregateId());
        setName(courseExecutionDto.getName());
        setType(CourseType.valueOf(courseExecutionDto.getType()));
        setCourseVersion(courseExecutionDto.getCourseVersion());
    }

    public CourseExecutionCourse(CourseExecutionCourse courseExecutionCourse) {
        setCourseAggregateId(courseExecutionCourse.getCourseAggregateId());
        setName(courseExecutionCourse.getName());
        setType(courseExecutionCourse.getType());
        setCourseVersion(courseExecutionCourse.getCourseVersion());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCourseAggregateId() {
        return courseAggregateId;
    }

    public void setCourseAggregateId(Integer courseAggregateId) {
        this.courseAggregateId = courseAggregateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public Integer getCourseVersion() {
        return courseVersion;
    }

    public void setCourseVersion(Integer courseVersion) {
        this.courseVersion = courseVersion;
    }

    public pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }
}
