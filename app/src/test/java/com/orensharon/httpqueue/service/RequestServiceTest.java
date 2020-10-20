package com.orensharon.httpqueue.service;

import com.orensharon.httpqueue.BuildConfig;
import com.orensharon.httpqueue.DispatcherStub;
import com.orensharon.httpqueue.ExecutorStub;
import com.orensharon.httpqueue.ISystemClock;
import com.orensharon.httpqueue.QueueWorkerStub;
import com.orensharon.httpqueue.StubEventBus;
import com.orensharon.httpqueue.SystemClockMock;
import com.orensharon.httpqueue.data.RequestRepository;
import com.orensharon.httpqueue.data.event.RequestStateChangedEvent;
import com.orensharon.httpqueue.data.model.Request;

import org.greenrobot.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RequestServiceTest {

    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EventBus eventBus;

    private DispatcherStub dispatcherStub;

    private ISystemClock clock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        EventBus eventBus = new StubEventBus();
        Executor executor = new ExecutorStub();
        this.clock = new SystemClockMock();
        QueueWorker queueWorker = new QueueWorkerStub(new ExecutorServiceStub(), this.clock);
        this.dispatcherStub = new DispatcherStub();
        this.requestService = new RequestService(this.requestRepository, queueWorker, this.dispatcherStub, this.eventBus, executor, this.clock);
    }

    @Test
    public void testInit_emptyRepository() {
        when(this.requestRepository.list()).thenReturn(new ArrayList<>());
        this.requestService.init();
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
    }

    @Test
    public void testInit_nonEmptyRepository_alreadySent() {
        this.dispatcherStub.setSuccess(true);
        List<Request> data = new ArrayList<>();
        data.add(new Request(0, "endpoint", "payload", 0, -1, 0));
        data.add(new Request(1, "endpoint", "payload", 0, -1, 0));
        when(this.requestRepository.list()).thenReturn(data);
        when(this.requestRepository.getById(0)).thenReturn(data.get(0));
        when(this.requestRepository.getById(1)).thenReturn(data.get(1));
        this.requestService.init();
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
        Mockito.verify(this.requestRepository, never()).getById(anyLong());
        Mockito.verify(this.requestRepository, never()).store(any(Request.class));
        Mockito.verify(this.eventBus, never()).post(any(RequestStateChangedEvent.class));
    }

    @Test
    public void testInit_nonEmptyRepository_allBackOffLimitReached_dropped() {
        this.dispatcherStub.setSuccess(true);
        List<Request> data = new ArrayList<>();
        data.add(new Request(0, "endpoint", "payload", 0, BuildConfig.BACKOFF_LIMIT, 0));
        data.add(new Request(1, "endpoint", "payload", 0, BuildConfig.BACKOFF_LIMIT, 0));
        when(this.requestRepository.list()).thenReturn(data);
        when(this.requestRepository.getById(0)).thenReturn(data.get(0));
        when(this.requestRepository.getById(1)).thenReturn(data.get(1));
        this.requestService.init();
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
        Mockito.verify(this.requestRepository, never()).getById(anyLong());
        Mockito.verify(this.requestRepository, never()).store(any(Request.class));
        Mockito.verify(this.eventBus, never()).post(any(RequestStateChangedEvent.class));
    }

    @Test
    public void testInit_nonEmptyRepository_allValid_dispatchAllSuccessfully() {
        this.dispatcherStub.setSuccess(true);
        List<Request> data = new ArrayList<>();
        data.add(new Request(0, "endpoint", "payload", 0, 0, 0));
        data.add(new Request(1, "endpoint", "payload", 0, 0, 0));
        when(this.requestRepository.list()).thenReturn(data);
        when(this.requestRepository.getById(0)).thenReturn(data.get(0));
        when(this.requestRepository.getById(1)).thenReturn(data.get(1));
        this.requestService.init();
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
        Mockito.verify(this.requestRepository, times(2 * data.size())).getById(anyLong());
        Mockito.verify(this.requestRepository, times(data.size())).store(any(Request.class));
        Mockito.verify(this.eventBus, times(data.size())).post(any(RequestStateChangedEvent.class));
        Assert.assertTrue(data.get(0).isSuccess());
        Assert.assertTrue(data.get(1).isSuccess());
    }

    @Test
    public void testInit_nonEmptyRepository_allValid_dispatchAllUnsuccessfully() {
        this.dispatcherStub.setSuccess(false);
        List<Request> data = new ArrayList<>();
        data.add(new Request(0, "endpoint", "payload", 0, 0, 0));
        data.add(new Request(1, "endpoint", "payload", 0, 0, 0));
        when(this.requestRepository.list()).thenReturn(data);
        when(this.requestRepository.getById(0)).thenReturn(data.get(0));
        when(this.requestRepository.getById(1)).thenReturn(data.get(1));
        this.requestService.init();
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
        Mockito.verify(this.requestRepository, times(2 * data.size() * BuildConfig.BACKOFF_LIMIT)).getById(anyLong());
        Mockito.verify(this.requestRepository, times(data.size() * BuildConfig.BACKOFF_LIMIT)).store(any(Request.class));
        Mockito.verify(this.eventBus, times(data.size() * BuildConfig.BACKOFF_LIMIT)).post(any(RequestStateChangedEvent.class));
        Assert.assertFalse(data.get(0).isSuccess());
        Assert.assertFalse(data.get(1).isSuccess());
    }

    @Test
    public void testAdd_validRequest_dispatchSuccess() {
        this.dispatcherStub.setSuccess(true);
        Request newRequest = new Request(0, "endpoint", "payload", 0, 0, 0);
        when(this.requestRepository.getById(anyLong())).thenReturn(newRequest);
        when(this.requestRepository.list()).thenReturn(new ArrayList<>());
        this.requestService.init();
        this.requestService.add(newRequest);
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
        Mockito.verify(this.requestRepository, times(2)).getById(anyLong());
        Mockito.verify(this.requestRepository, times(2)).store(any(Request.class));
        Mockito.verify(this.eventBus, times(1)).post(any(RequestStateChangedEvent.class));
        Assert.assertTrue(newRequest.isSuccess());
    }

    @Test
    public void testAdd_validRequest_dispatchUnsuccessful() {
        this.dispatcherStub.setSuccess(false);
        Request newRequest = new Request(0, "endpoint", "payload", 0, 0, 0);
        when(this.requestRepository.getById(anyLong())).thenReturn(newRequest);
        when(this.requestRepository.list()).thenReturn(new ArrayList<>());
        this.requestService.init();
        this.requestService.add(newRequest);
        Mockito.verify(this.requestRepository, times(1)).init();
        Mockito.verify(this.requestRepository, times(1)).list();
        Mockito.verify(this.requestRepository, times(38)).getById(anyLong());
        Mockito.verify(this.requestRepository, times(20)).store(any(Request.class));
        Mockito.verify(this.eventBus, times(19)).post(any(RequestStateChangedEvent.class));
        Assert.assertFalse(newRequest.isSuccess());
    }
}
