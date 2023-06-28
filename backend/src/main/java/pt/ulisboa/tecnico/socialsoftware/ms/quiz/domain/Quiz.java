package pt.ulisboa.tecnico.socialsoftware.ms.quiz.domain;

import pt.ulisboa.tecnico.socialsoftware.ms.causalconsistency.aggregate.domain.Aggregate;
import pt.ulisboa.tecnico.socialsoftware.ms.causalconsistency.event.EventSubscription;
import pt.ulisboa.tecnico.socialsoftware.ms.exception.TutorException;
import pt.ulisboa.tecnico.socialsoftware.ms.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.ms.quiz.event.subscribe.QuizSubscribesRemoveCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.ms.quiz.event.subscribe.QuizSubscribesRemoveQuestion;
import pt.ulisboa.tecnico.socialsoftware.ms.quiz.event.subscribe.QuizSubscribesUpdateQuestion;
import pt.ulisboa.tecnico.socialsoftware.ms.utils.DateHandler;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.ms.causalconsistency.aggregate.domain.Aggregate.AggregateState.ACTIVE;
import static pt.ulisboa.tecnico.socialsoftware.ms.causalconsistency.aggregate.domain.AggregateType.*;
import static pt.ulisboa.tecnico.socialsoftware.ms.exception.ErrorMessage.*;

/*
    INTRA-INVARIANTS
        DATE_ORDERING
        QUESTIONS_FINAL_AFTER_AVAILABLE_DATE
        COURSE_EXECUTION_FINAL
        CREATION_DATE_FINAL
        AVAILABLE_DATE_FINAL_AFTER_AVAILABLE_DATE
        CONCLUSION_DATE_FINAL_AFTER_AVAILABLE_DATE
        RESULTS_DATE_FINAL_AFTER_AVAILABLE_DATE
    INTER-INVARIANTS
        QUESTION_EXISTS
        COURSE_EXECUTION_EXISTS
        QUESTION_COURSE_EXECUTION_SAME_AS_COURSE_EXECUTION
 */
@Entity
public class Quiz extends Aggregate {
    /*
        CREATION_DATE_FINAL
     */
    private final LocalDateTime creationDate;
    private LocalDateTime availableDate;
    private LocalDateTime conclusionDate;
    private LocalDateTime resultsDate;
    private String title = "Title";
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "quiz")
    private Set<QuizQuestion> quizQuestions = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private QuizType quizType;
    /*
        COURSE_EXECUTION_FINAL
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "quiz")
    private QuizCourseExecution quizCourseExecution;

    public Quiz() {
        this.quizCourseExecution = null;
        this.creationDate = null;
    }


    public Quiz(Integer aggregateId, QuizCourseExecution quizCourseExecution, Set<QuizQuestion> quizQuestions, QuizDto quizDto, QuizType quizType) {
        super(aggregateId, QUIZ);
        setQuizCourseExecution(quizCourseExecution);
        setQuizQuestions(quizQuestions);
        setTitle(quizDto.getTitle());
        this.creationDate = LocalDateTime.now();
        setAvailableDate(DateHandler.toLocalDateTime(quizDto.getAvailableDate()));
        setConclusionDate(DateHandler.toLocalDateTime(quizDto.getConclusionDate()));
        setResultsDate(DateHandler.toLocalDateTime(quizDto.getResultsDate()));
        setQuizType(quizType);
        setPrev(null);
    }

    public Quiz(Quiz other) {
        super(other);
        setQuizCourseExecution(new QuizCourseExecution(other.getQuizCourseExecution()));

        setQuizQuestions(other.getQuizQuestions().stream().map(QuizQuestion::new).collect(Collectors.toSet()));
        setTitle(other.getTitle());
        this.creationDate = other.getCreationDate();
        setAvailableDate(other.getAvailableDate());
        setConclusionDate(other.getConclusionDate());
        setResultsDate(other.getResultsDate());
        setQuizType(other.getQuizType());
        setQuizQuestions(other.getQuizQuestions().stream().map(QuizQuestion::new).collect(Collectors.toSet()));
    }



    public boolean invariantDateOrdering() {
        return getCreationDate().isBefore(getConclusionDate()) &&
                getAvailableDate().isBefore(getConclusionDate()) &&
                (getConclusionDate().isEqual(getResultsDate()) || getConclusionDate().isBefore(getResultsDate()));

    }

    @Override
    public void verifyInvariants() {
        if (!(invariantDateOrdering())) {
            throw new TutorException(INVARIANT_BREAK, getAggregateId());
        }
    }

    @Override
    public Set<EventSubscription> getEventSubscriptions() {
        Set<EventSubscription> eventSubscriptions = new HashSet<>();
        if (getState() == ACTIVE) {
            interInvariantCourseExecutionExists(eventSubscriptions);
            interInvariantQuestionsExist(eventSubscriptions);
        }
        return eventSubscriptions;
    }

    private void interInvariantCourseExecutionExists(Set<EventSubscription> eventSubscriptions) {
        eventSubscriptions.add(new QuizSubscribesRemoveCourseExecution(this.getQuizCourseExecution()));
    }

    private void interInvariantQuestionsExist(Set<EventSubscription> eventSubscriptions) {
        for (QuizQuestion quizQuestion : this.quizQuestions) {
            eventSubscriptions.add(new QuizSubscribesUpdateQuestion(quizQuestion));
            eventSubscriptions.add(new QuizSubscribesRemoveQuestion(quizQuestion));
        }
    }


    @Override
    public Set<String> getFieldsChangedByFunctionalities() {
        // we dont add the courseExecution because it can only change through events and the only events that comes from it is the delete which deletes the quiz
        return Set.of("availableDate", "conclusionDate", "resultsDate", "title" ,"quizQuestions");
    }

    @Override
    public Set<String[]> getIntentions() {
        return Set.of(
                new String[]{"availableDate", "conclusionDate"},
                new String[]{"availableDate", "resultsDate"},
                new String[]{"conclusionDate", "resultsDate"});
    }

    @Override
    public Aggregate mergeFields(Set<String> toCommitVersionChangedFields, Aggregate committedVersion, Set<String> committedVersionChangedFields) {
        Quiz committedQuiz = (Quiz) committedVersion;
        Quiz mergedQuiz = new Quiz(this);

        mergeAvailableDate(toCommitVersionChangedFields, committedQuiz, mergedQuiz);
        mergeConclusionDate(toCommitVersionChangedFields, committedQuiz, mergedQuiz);
        mergeResultsDate(toCommitVersionChangedFields, committedQuiz, mergedQuiz);
        mergeTitle(toCommitVersionChangedFields, committedQuiz, mergedQuiz);
        mergeQuizQuestions(toCommitVersionChangedFields, committedQuiz, mergedQuiz);
        return mergedQuiz;
    }

    private void mergeAvailableDate(Set<String> toCommitVersionChangedFields, Quiz committedQuiz, Quiz mergedQuiz) {
        if(toCommitVersionChangedFields.contains("availableDate")) {
            mergedQuiz.setAvailableDate(getAvailableDate());
        } else {
            mergedQuiz.setAvailableDate(committedQuiz.getAvailableDate());
        }
    }

    private void mergeConclusionDate(Set<String> toCommitVersionChangedFields, Quiz committedQuiz, Quiz mergedQuiz) {
        if(toCommitVersionChangedFields.contains("conclusionDate")) {
            mergedQuiz.setConclusionDate(getConclusionDate());
        } else {
            mergedQuiz.setConclusionDate(committedQuiz.getConclusionDate());
        }
    }

    private void mergeResultsDate(Set<String> toCommitVersionChangedFields, Quiz committedQuiz, Quiz mergedQuiz) {
        if(toCommitVersionChangedFields.contains("resultsDate")) {
            mergedQuiz.setResultsDate(getResultsDate());
        } else {
            mergedQuiz.setResultsDate(committedQuiz.getResultsDate());
        }
    }

    private void mergeTitle(Set<String> toCommitVersionChangedFields, Quiz committedQuiz, Quiz mergedQuiz) {
        if(toCommitVersionChangedFields.contains("title")) {
            mergedQuiz.setTitle(getTitle());
        } else {
            mergedQuiz.setTitle(committedQuiz.getTitle());
        }
    }
    
    private void mergeQuizQuestions(Set<String> toCommitVersionChangedFields, Quiz committedQuiz, Quiz mergedQuiz) {
        if(toCommitVersionChangedFields.contains("quizQuestions")) {
            mergedQuiz.setQuizQuestions(getQuizQuestions().stream().map(QuizQuestion::new).collect(Collectors.toSet()));
        } else {
            mergedQuiz.setQuizQuestions(committedQuiz.getQuizQuestions().stream().map(QuizQuestion::new).collect(Collectors.toSet()));
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        /*
            AVAILABLE_DATE_FINAL_AFTER_AVAILABLE_DATE
         */
        Quiz prev = (Quiz) getPrev();
        if (prev != null && (DateHandler.now()).isAfter(prev.getAvailableDate())) {
            throw new TutorException(CANNOT_UPDATE_QUIZ, getAggregateId());
        }
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        /*
            CONCLUSION_DATE_FINAL_AFTER_AVAILABLE_DATE
         */
        Quiz prev = (Quiz) getPrev();
        if (prev != null && (DateHandler.now()).isAfter(prev.getAvailableDate())) {
            throw new TutorException(CANNOT_UPDATE_QUIZ, getAggregateId());
        }
        this.conclusionDate = conclusionDate;
    }

    public LocalDateTime getResultsDate() {
        return resultsDate;
    }

    public void setResultsDate(LocalDateTime resultsDate) {
        /*
            RESULTS_DATE_FINAL_AFTER_AVAILABLE_DATE
         */
        Quiz prev = (Quiz) getPrev();
        if(prev != null && (DateHandler.now()).isAfter(prev.getAvailableDate())) {
            throw new TutorException(CANNOT_UPDATE_QUIZ, getAggregateId());
        }
        this.resultsDate = resultsDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }


    public void setQuizQuestions(Set<QuizQuestion> quizQuestions) {
        /*
            QUESTIONS_FINAL_AFTER_AVAILABLE_DATE
         */
        Quiz prev = (Quiz) getPrev();
        if (prev != null && DateHandler.now().isAfter(prev.getAvailableDate())) {
            throw new TutorException(CANNOT_UPDATE_QUIZ, getAggregateId());
        }
        this.quizQuestions = quizQuestions;
        this.quizQuestions.forEach(quizQuestion -> quizQuestion.setQuiz(this));
    }

    public QuizCourseExecution getQuizCourseExecution() {
        return quizCourseExecution;
    }

    public void setQuizCourseExecution(QuizCourseExecution quizCourseExecution) {
        this.quizCourseExecution = quizCourseExecution;
        this.quizCourseExecution.setQuiz(this);
    }

    public QuizType getQuizType() {
        return quizType;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public void update(QuizDto quizDto) {
        setTitle(quizDto.getTitle());
        setAvailableDate(LocalDateTime.parse(quizDto.getAvailableDate()));
        setConclusionDate(LocalDateTime.parse(quizDto.getConclusionDate()));
        setResultsDate(LocalDateTime.parse(quizDto.getResultsDate()));
    }

    public QuizQuestion findQuestion(Integer questionAggregateId) {
        for(QuizQuestion qq : quizQuestions) {
            if(qq.getQuestionAggregateId().equals(questionAggregateId)) {
                return qq;
            }
        }
        return null;
    }
}
