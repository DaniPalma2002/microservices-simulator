package pt.ulisboa.tecnico.socialsoftware.ms.sagas.workflow;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

import pt.ulisboa.tecnico.socialsoftware.ms.coordination.unitOfWork.UnitOfWork;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.AsyncStep;
import pt.ulisboa.tecnico.socialsoftware.ms.coordination.workflow.FlowStep;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.unityOfWork.SagaUnitOfWork;

public class SagaAsyncStep extends AsyncStep implements SagaStep {
    private Runnable compensationLogic;

    public SagaAsyncStep(String stepName, Supplier<CompletableFuture<Void>> asyncOperation, ArrayList<FlowStep> dependencies, ExecutorService executorService) {
        super(stepName, asyncOperation, dependencies, executorService);
    }

    public SagaAsyncStep(String stepName, Supplier<CompletableFuture<Void>> asyncOperation, ExecutorService executorService) {
        super(stepName, asyncOperation, executorService);
    }

    public SagaAsyncStep(String stepName, Supplier<CompletableFuture<Void>> asyncOperation) {
        super(stepName, asyncOperation);
    }

    public SagaAsyncStep(String stepName, Supplier<CompletableFuture<Void>> asyncOperation, ArrayList<FlowStep> dependencies) {
        super(stepName, asyncOperation, dependencies);
    }

    public void registerCompensation(Runnable compensationLogic, UnitOfWork unitOfWork) {
        this.compensationLogic = compensationLogic;
    }

    public Runnable getCompensation() {
        return this.compensationLogic;
    }

    @Override
    public CompletableFuture<Void> execute(UnitOfWork unitOfWork) {
        try {
            if (getCompensation() != null) {
                ((SagaUnitOfWork)unitOfWork).registerCompensation(getCompensation());
            }
            return CompletableFuture.supplyAsync(getAsyncOperation(), getExecutorService()).thenCompose(Function.identity());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
