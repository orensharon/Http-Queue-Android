package com.orensharon.httpqueue.presentation.vm;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.orensharon.httpqueue.StubEventBus;
import com.orensharon.httpqueue.data.event.RequestStateChangedEvent;
import com.orensharon.httpqueue.presentation.model.RequestEvent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

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
        // Prepare
        long now = System.currentTimeMillis();
        Observer<RequestEvent> observer = mock(Observer.class);
        this.viewModel.getLastSuccessEvent().observeForever(observer);
        // Act
        this.viewModel.onRequestStateChangedEvent(new RequestStateChangedEvent(1, now, true));
        // Assert
        //verify(observer).onChanged();
        /*Assert.assertThat(this.viewModel.getLastSuccessEvent().hasActiveObservers(), is(true));
        Assert.assertThat(this.viewModel.getSuccessPercentage().getValue(), is(100));
        Assert.assertNotNull(this.viewModel.getLastSuccessEvent().getValue());
        Assert.assertEquals(now, this.viewModel.getLastSuccessEvent().getValue().ts);
        Assert.assertEquals(1, this.viewModel.getAllSuccessEvents().size());
        Assert.assertEquals(0, this.viewModel.getAllFailedEvents().size());*/
    }

    /*@Test
    public void testNewFailedRequestEvent() {
        Assert.assertTrue(false);
    }*/
}
