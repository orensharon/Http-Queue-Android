package com.orensharon.httpqueue.presentation.vm;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.orensharon.httpqueue.StubEventBus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class VisualizationVMTest {

    //It will tell JUnit to force tests to be executed synchronously,
    // especially when using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private StubEventBus eventBus;

    private VisualizationVM viewModel;

    @Before
    public void setUp() {
        this.viewModel = new VisualizationVM(this.eventBus);
    }

    @Test
    public void testNewSuccessRequestEvent() {
    }
}
