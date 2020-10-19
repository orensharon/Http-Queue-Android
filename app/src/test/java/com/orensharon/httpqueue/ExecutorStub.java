package com.orensharon.httpqueue;

import java.util.concurrent.Executor;

public class ExecutorStub implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
