package org.example;

public class ExecutionStatus {

    Process process;
    Exception exception;

    public ExecutionStatus(Process process, Exception exception) {
        this.process = process;
        this.exception = exception;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
