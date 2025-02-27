package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.causal.aggregates.factories;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.causal.aggregates.CausalQuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.*;

@Service
@Profile("tcc")
public class CausalQuizAnswerFactory implements QuizAnswerFactory {
    @Override
    public QuizAnswer createQuizAnswer(Integer aggregateId, AnswerCourseExecution answerCourseExecution, AnswerStudent answerStudent, AnsweredQuiz answeredQuiz) {
        return new CausalQuizAnswer(aggregateId, answerCourseExecution, answerStudent, answeredQuiz);
    }
    
    @Override
    public QuizAnswer createQuizAnswerFromExisting(QuizAnswer existingAnswer) {
        return new CausalQuizAnswer((CausalQuizAnswer) existingAnswer);
    }

	@Override
	public QuizAnswerDto createQuizAnswerDto(QuizAnswer quizAnswer) {
		return new QuizAnswerDto(quizAnswer);
	}
}