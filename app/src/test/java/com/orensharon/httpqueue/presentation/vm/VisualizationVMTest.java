package com.orensharon.httpqueue.presentation.vm;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.orensharon.httpqueue.ISystemClock;
import com.orensharon.httpqueue.StubEventBus;
import com.orensharon.httpqueue.SystemClockMock;
import com.orensharon.httpqueue.data.event.RequestStateChangedEvent;
import com.orensharon.httpqueue.presentation.LiveDataTestUtil;
import com.orensharon.httpqueue.presentation.model.RequestEvent;

import org.junit.Assert;
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

    private ISystemClock clock = new SystemClockMock();

    @Before
    public void setUp() {
        this.viewModel = new VisualizationVM(this.eventBus);
    }

    @Test
    public void testNewSuccessRequestEvent() throws InterruptedException {
        long ts = this.clock.getElapsedRealTime();
        this.viewModel.onRequestStateChangedEvent(new RequestStateChangedEvent(1L, ts, true));
        int percentage = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getSuccessPercentage());
        RequestEvent requestEvent = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getLastSuccessEvent());
        Assert.assertEquals(100, percentage);
        Assert.assertEquals(1, requestEvent.number);
        Assert.assertEquals(1L, requestEvent.requestId);
        Assert.assertEquals(ts, requestEvent.ts);
        Assert.assertTrue( requestEvent.success);
        Assert.assertEquals( 1, this.viewModel.getAllSuccessEvents().size());
        Assert.assertEquals( 0, this.viewModel.getAllFailedEvents().size());
    }

    @Test
    public void testNewFailedRequestEvent() throws InterruptedException {
        long ts = this.clock.getElapsedRealTime();
        this.viewModel.onRequestStateChangedEvent(new RequestStateChangedEvent(1L, ts, false));
        int percentage = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getSuccessPercentage());
        RequestEvent requestEvent = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getLastFailedEvent());
        Assert.assertEquals(0, percentage);
        Assert.assertEquals(1, requestEvent.number);
        Assert.assertEquals(1L, requestEvent.requestId);
        Assert.assertEquals(ts, requestEvent.ts);
        Assert.assertFalse( requestEvent.success);
        Assert.assertEquals( 0, this.viewModel.getAllSuccessEvents().size());
        Assert.assertEquals( 1, this.viewModel.getAllFailedEvents().size());
    }

    @Test
    public void testSuccessPercentage_50_50() throws InterruptedException {
        int events = 1000;
        for (int event = 1; event <= events; event++) {
            long ts = this.clock.getElapsedRealTime();
            boolean success = event % 2 == 0;
            this.viewModel.onRequestStateChangedEvent(new RequestStateChangedEvent(event, ts, success));
            RequestEvent requestEvent = success?
                    LiveDataTestUtil.getOrAwaitValue(this.viewModel.getLastSuccessEvent()):
                    LiveDataTestUtil.getOrAwaitValue(this.viewModel.getLastFailedEvent());
            Assert.assertEquals(ts, requestEvent.ts);
            Assert.assertEquals(success, requestEvent.success);
        }
        Assert.assertEquals(events / 2, this.viewModel.getAllFailedEvents().size());
        Assert.assertEquals(events / 2, this.viewModel.getAllSuccessEvents().size());
        int percentage = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getSuccessPercentage());
        Assert.assertEquals(50, percentage);
    }

    @Test
    public void testSuccessPercentage_75_25() throws InterruptedException {
        int events = 40;
        for (int event = 0; event < events; event++) {
            long ts = this.clock.getElapsedRealTime();
            this.viewModel.onRequestStateChangedEvent(new RequestStateChangedEvent(event, ts, event < 30));
        }
        int percentage = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getSuccessPercentage());
        Assert.assertEquals(75, percentage);
    }

    @Test
    public void testSuccessPercentage_25_75() throws InterruptedException {
        int events = 40;
        for (int event = 0; event < events; event++) {
            long ts = this.clock.getElapsedRealTime();
            this.viewModel.onRequestStateChangedEvent(new RequestStateChangedEvent(event, ts, event < 10));
        }
        int percentage = LiveDataTestUtil.getOrAwaitValue(this.viewModel.getSuccessPercentage());
        Assert.assertEquals(25, percentage);
    }
}
