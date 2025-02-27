package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.coordination.tournament;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.WorkflowFunctionality;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.aggregate.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.tournament.service.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWorkService;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaSyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow.SagaWorkflow;

import java.util.List;

public class GetOpenedTournamentsForCourseExecutionFunctionalitySagas extends WorkflowFunctionality {
    private List<TournamentDto> openedTournaments;
    private final TournamentService tournamentService;
    private final SagaUnitOfWorkService unitOfWorkService;

    public GetOpenedTournamentsForCourseExecutionFunctionalitySagas(TournamentService tournamentService,SagaUnitOfWorkService unitOfWorkService, 
                                Integer executionAggregateId, SagaUnitOfWork unitOfWork) {
        this.tournamentService = tournamentService;
        this.unitOfWorkService = unitOfWorkService;
        this.buildWorkflow(executionAggregateId, unitOfWork);
    }

    public void buildWorkflow(Integer executionAggregateId, SagaUnitOfWork unitOfWork) {
        this.workflow = new SagaWorkflow(this, unitOfWorkService, unitOfWork);

        SagaSyncStep getOpenedTournamentsStep = new SagaSyncStep("getOpenedTournamentsStep", () -> {
            List<TournamentDto> openedTournaments = tournamentService.getOpenedTournamentsForCourseExecution(executionAggregateId, unitOfWork);
            this.setOpenedTournaments(openedTournaments);
        });
    
        workflow.addStep(getOpenedTournamentsStep);
    }
    


    public List<TournamentDto> getOpenedTournaments() {
        return openedTournaments;
    }

    public void setOpenedTournaments(List<TournamentDto> openedTournaments) {
        this.openedTournaments = openedTournaments;
    }
}
