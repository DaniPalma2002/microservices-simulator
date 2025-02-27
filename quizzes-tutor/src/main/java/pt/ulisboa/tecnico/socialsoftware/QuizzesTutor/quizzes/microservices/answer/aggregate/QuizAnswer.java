package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.Aggregate;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventSubscription;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.events.subscribe.*;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.exception.TutorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.ms.domain.aggregate.Aggregate.AggregateState.ACTIVE;
import static pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.exception.ErrorMessage.QUESTION_ALREADY_ANSWERED;

/*
    INTRA-INVARIANTS:
        FINAL_ANSWER_DATE
        FINAL_CREATION_DATE
        FINAL_USER
        FINAL_QUIZ
        FINAL_COURSE_EXECUTION
    INTER-INVARIANTS:
        USER_EXISTS
        QUIZ_EXISTS
        QUESTION_EXISTS
        COURSE_EXECUTION_EXISTS
        QUESTION_ANSWERS_QUESTION_BELONGS_TO_QUIZ
        QUIZ_COURSE_EXECUTION_SAME_AS_USER_COURSE_EXECUTION
        COURSE_EXECUTION_SAME_AS_USER_COURSE_EXECUTION
        COURSE_EXECUTION_SAME_QUIZ_COURSE_EXECUTION (verified at the service and the quiz doesnt change execution)

 */
@Entity
public abstract class QuizAnswer extends Aggregate {
    private LocalDateTime creationDate;
    private LocalDateTime answerDate;
    private boolean completed;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "quizAnswer")
    private pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution answerCourseExecution;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "quizAnswer")
    private pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent student;
    /* it is not final because of the question ids inside*/
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "quizAnswer")
    private pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz quiz;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "quizAnswer")
    private List<pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer> questionAnswers = new ArrayList<>();

    public QuizAnswer() {
        setAnswerCourseExecution(new pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution());
        setStudent(new pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent());
        setQuiz(new pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz());
    }

    public QuizAnswer(Integer aggregateId, pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution answerCourseExecution, pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent answerStudent, pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz answeredQuiz) {
        super(aggregateId);
        setAggregateType(getClass().getSimpleName());
        setAnswerCourseExecution(answerCourseExecution);
        setStudent(answerStudent);
        setQuiz(answeredQuiz);
    }

    public QuizAnswer(QuizAnswer other) {
        super(other);
        setAnswerCourseExecution(new pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution(other.getAnswerCourseExecution()));
        setStudent(new pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent(other.getStudent()));
        setQuiz(new pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz(other.getQuiz()));
        setAnswerDate(other.getAnswerDate());
        setCreationDate(other.getCreationDate());
        setQuestionAnswers(other.getQuestionAnswers().stream().map(pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer::new).collect(Collectors.toList()));
    }

    @Override
    public void verifyInvariants() {

    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public Set<EventSubscription> getEventSubscriptions() {
        //return Set.of(REMOVE_USER, UNENROLL_STUDENT, INVALIDATE_QUIZ/*, REMOVE_QUIZ*/);
        Set<EventSubscription> eventSubscriptions = new HashSet<>();
        if (getState() == ACTIVE) {
            interInvariantCourseExecutionExists(eventSubscriptions);
            interInvariantQuizExists(eventSubscriptions);
            interInvariantStudentExists(eventSubscriptions);
        }
        return eventSubscriptions;
    }

    private void interInvariantCourseExecutionExists(Set<EventSubscription> eventSubscriptions) {
        // TODO: this event is not handled
        eventSubscriptions.add(new QuizAnswerSubscribesRemoveCourseExecution(this.getAnswerCourseExecution()));
    }

    private void interInvariantQuizExists(Set<EventSubscription> eventSubscriptions) {
        // also verifies QUESTION_EXISTS because if the question is DELETED the quiz sends this event
        // TODO: this event is not handled
        eventSubscriptions.add(new QuizAnswerSubscribesInvalidateQuiz(this));
    }

    private void interInvariantStudentExists(Set<EventSubscription> eventSubscriptions) {
        eventSubscriptions.add(new QuizAnswerSubscribesUnerollStudentFromCourseExecution(this));
        // TODO: this event is not handled
        eventSubscriptions.add(new QuizAnswerSubscribesAnonymizeStudent(this));
        eventSubscriptions.add(new QuizAnswerSubscribesUpdateStudentName(this));
    }


    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution getAnswerCourseExecution() {
        return answerCourseExecution;
    }

    public void setAnswerCourseExecution(AnswerCourseExecution answerCourseExecution) {
        this.answerCourseExecution = answerCourseExecution;
        answerCourseExecution.setQuizAnswer(this);
    }

    public pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent getStudent() {
        return student;
    }

    public void setStudent(AnswerStudent answerStudent) {
        this.student = answerStudent;
        answerStudent.setQuizAnswer(this);
    }

    public pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz getQuiz() {
        return quiz;
    }

    public void setQuiz(AnsweredQuiz quiz) {
        this.quiz = quiz;
        quiz.setQuizAnswer(this);
    }

    public List<pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(List<pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer> questionAnswers) {
        this.questionAnswers.forEach(questionAnswer -> questionAnswer.setQuizAnswer(null));
        this.questionAnswers = questionAnswers;
        questionAnswers.forEach(questionAnswer -> questionAnswer.setQuizAnswer(this));
    }

    public void addQuestionAnswer(pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer questionAnswer) {
        List<Integer> answeredQuestionIds = this.questionAnswers.stream()
                .map(pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer::getQuestionAggregateId)
                .collect(Collectors.toList());
        if (answeredQuestionIds.contains(questionAnswer.getQuestionAggregateId())) {
            throw new TutorException(QUESTION_ALREADY_ANSWERED, questionAnswer.getQuestionAggregateId(), this.getQuiz().getQuizAggregateId());
        }
        this.questionAnswers.add(questionAnswer);
        questionAnswer.setQuizAnswer(this);
    }

    public pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuestionAnswer findQuestionAnswer(Integer questionAggregateId) {
        for (QuestionAnswer qa : this.questionAnswers) {
            if (qa.getQuestionAggregateId().equals(questionAggregateId)) {
                return qa;
            }
        }
        return null;
    }

}
