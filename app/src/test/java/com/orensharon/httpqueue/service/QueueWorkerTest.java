package com.orensharon.httpqueue.service;

import com.orensharon.httpqueue.ISystemClock;
import com.orensharon.httpqueue.SystemClockMock;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class QueueWorkerTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private QueueWorker queueWorker;

    private ExecutorService executorService;

    private ISystemClock clock;

    @Before
    public void setUp() {
        this.clock = new SystemClockMock();
        this.executorService = Executors.newSingleThreadExecutor();
        this.queueWorker = new QueueWorker(this.executorService, this.clock);
    }

    @After
    public void tearDown() {
        this.queueWorker.terminate();
    }

    @Test(expected = NullPointerException.class)
    public void testEnqueueWithoutNullListener_throwsException() {
        this.queueWorker.enqueue(0, 0, null);
    }

    @Test(expected = RuntimeException.class)
    public void testEnqueueWithoutStart_throwsException() {
        long id = 0;
        this.queueWorker.enqueue(id, 0, reqId -> {});
    }

    @Test
    public void testTermination() {
        this.queueWorker.listen();
        Assert.assertTrue(this.queueWorker.isStarted());
        this.queueWorker.terminate();
        Assert.assertFalse(this.queueWorker.isStarted());
    }

    @Test
    public void testStartListenMoreThenOnce_throwsException() {
        this.queueWorker.listen();

        Assert.assertTrue(this.queueWorker.isStarted());

        this.exceptionRule.expect(RuntimeException.class);
        this.exceptionRule.expectMessage("ALREADY_LISTENING");

        this.queueWorker.listen();

        Assert.assertTrue(this.queueWorker.isStarted());
    }

    @Test
    public void testSimpleEnqueue() throws InterruptedException {
        final CountDownLatch lastExecuted = new CountDownLatch(1);
        final AtomicInteger executedCount = new AtomicInteger(0);
        this.queueWorker.listen();
        this.queueWorker.enqueue(0, 0, reqId -> {
            executedCount.incrementAndGet();
            lastExecuted.countDown();
        });
        lastExecuted.await();
        assertEquals(executedCount.get(),1);
    }

    @Test
    public void testPriorityEnqueue_orderedRequests() throws InterruptedException {
        final CountDownLatch lastExecuted = new CountDownLatch(2);
        final AtomicInteger executedCount = new AtomicInteger(0);
        this.queueWorker.listen();
        List<Long> orders = new ArrayList<>();
        this.queueWorker.enqueue(1, 0,  reqId -> {
            executedCount.incrementAndGet();
            lastExecuted.countDown();
            orders.add(reqId);
        });
        this.queueWorker.enqueue(2, 0,  reqId -> {
            executedCount.incrementAndGet();
            lastExecuted.countDown();
            orders.add(reqId);
        });
        lastExecuted.await();
        assertEquals(2, executedCount.get());
        assertEquals(2, orders.size());
        assertEquals(1, orders.get(0).longValue());
        assertEquals(2, orders.get(1).longValue());
    }

    @Test(timeout = 1500)
    public void testPriorityEnqueue_unorderedRequests() throws InterruptedException {
        final CountDownLatch lastExecuted = new CountDownLatch(2);
        this.queueWorker.listen();
        List<Long> orders = new ArrayList<>();
        long ts = this.clock.getElapsedRealTime();
        this.queueWorker.enqueue(1, ts + 1000,  reqId -> {
            lastExecuted.countDown();
            orders.add(reqId);
        });
        this.queueWorker.enqueue(2, ts + 500,  reqId -> {
            lastExecuted.countDown();
            orders.add(reqId);
        });
        lastExecuted.await();
        assertEquals(2, orders.size());
        assertEquals(2, orders.get(0).longValue());
        assertEquals(1, orders.get(1).longValue());
    }

    @Test(timeout = 1100)
    public void testDequeueTiming() throws InterruptedException {
        final CountDownLatch lastExecuted = new CountDownLatch(1);
        this.queueWorker.listen();
        List<Long> orders = new ArrayList<>();
        long ts = this.clock.getElapsedRealTime();
        long delay = 1000L;
        this.queueWorker.enqueue(1, ts + delay,  reqId -> {
            lastExecuted.countDown();
            orders.add(reqId);
        });
        lastExecuted.await();
        long timeTook = this.clock.getElapsedRealTime() - ts;
        assertEquals(1, orders.get(0).longValue());
        assertThat(timeTook, Matchers.greaterThanOrEqualTo(delay));
    }

    @Test
    public void testLoadPendingStatedRequestsOf3Days() throws InterruptedException {
        long requests = TimeUnit.DAYS.toSeconds(3);
        final CountDownLatch lastExecuted = new CountDownLatch((int) requests);
        List<Long> dequeueRequests = new ArrayList<>();
        this.queueWorker.listen(1);
        for (long request = 0; request < requests; request++) {
            this.queueWorker.enqueue(request, 0L,  reqId -> {
                lastExecuted.countDown();
                dequeueRequests.add(reqId);
            });
        }
        lastExecuted.await();
        assertEquals(requests, dequeueRequests.size());
    }
}
