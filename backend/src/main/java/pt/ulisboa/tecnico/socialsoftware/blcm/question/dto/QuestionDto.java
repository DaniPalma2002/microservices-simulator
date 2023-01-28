package pt.ulisboa.tecnico.socialsoftware.blcm.question.dto;

import pt.ulisboa.tecnico.socialsoftware.blcm.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.blcm.question.domain.QuestionTopic;
import pt.ulisboa.tecnico.socialsoftware.blcm.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.blcm.topic.dto.TopicDto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionDto implements Serializable {
    private Integer aggregateId;

    private Integer version;

    private String title;

    private String content;

    private String creationDate;

    private CourseDto course;

    private Set<TopicDto> topicDto;

    private List<OptionDto> optionDtos;

    private Integer sequence;

    public QuestionDto() {}

   public QuestionDto(Question question) {
       setAggregateId(question.getAggregateId());
       setVersion(question.getVersion());
       setTitle(question.getTitle());
       setContent(question.getContent());
       setCreationDate(question.getCreationDate().toString());
       setCourse(question.getCourse().buildDto());
       setTopicDto(question.getTopics().stream()
               .map(QuestionTopic::buildDto)
               .collect(Collectors.toSet()));
       setOptionDtos(question.getOptions().stream()
               .map(OptionDto::new)
               .collect(Collectors.toList()));
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public CourseDto getCourse() {
        return course;
    }

    public void setCourse(CourseDto course) {
        this.course = course;
    }



    public Set<TopicDto> getTopicDto() {
        return topicDto;
    }

    public void setTopicDto(Set<TopicDto> topicDto) {
        this.topicDto = topicDto;
    }

    public List<OptionDto> getOptionDtos() {
        return optionDtos;
    }

    public void setOptionDtos(List<OptionDto> optionDtos) {
        this.optionDtos = optionDtos;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
