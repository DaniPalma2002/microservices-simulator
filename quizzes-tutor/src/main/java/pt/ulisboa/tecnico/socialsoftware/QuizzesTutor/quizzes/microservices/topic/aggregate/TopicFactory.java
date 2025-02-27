package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate;

import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.Topic;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.TopicCourse;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.TopicDto;

public interface TopicFactory {
    pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.Topic createTopic(Integer aggregateId, String name, TopicCourse topicCourse);
    pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.Topic createTopicFromExisting(pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.topic.aggregate.Topic existingTopic);
    TopicDto createTopicDto(Topic topic);
}
