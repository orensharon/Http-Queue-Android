package com.orensharon.httpqueue.data.model;

import com.orensharon.httpqueue.Constants;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RequestTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testCreatePutValidRequest_returnRequest() {
        Request request = createValidGenericPutRequest();
        Assert.assertNotNull(request);
        Assert.assertFalse(request.isSuccess());
        Assert.assertEquals(0, request.getLastRetryMs());
        Assert.assertEquals(0, request.getReties());
        Assert.assertEquals(0, request.getId());
        Assert.assertEquals(0, request.getScheduledTime());
    }

    @Test(expected = NullPointerException.class)
    public void testCreatePutInvalidEndpoint_throwsException() {
        Request.put("str",null);
        Request.put(null,"str");
        Request.put(null,null);
    }

    @Test(expected = NullPointerException.class)
    public void testCreatePutInvalidPayload_throwsException() {
        Request.put(null,"str");
    }

    @Test
    public void testSetId() {
        long id = 10;
        Request request = createValidGenericPutRequest();
        request.setId(id);
        Assert.assertEquals(id, request.getId());
    }

    @Test
    public void testSetIdAfterAlreadySet_throwsException() {
        Request request = createValidGenericPutRequest();
        request.setId(10);


        this.exceptionRule.expect(RuntimeException.class);
        this.exceptionRule.expectMessage("ID_ALREADY_EXISTS");

        request.setId(11);
        Assert.assertEquals(10, request.getId());
    }

    @Test
    public void testUpdateState_successAtFirstTry() {
        Request request = createValidGenericPutRequest();
        request.setId(1);
        request.updateState(true, 1000);

        Assert.assertEquals(-1, request.getReties());
        Assert.assertEquals(0, request.getScheduledTime());
        Assert.assertEquals(0, request.getLastRetryMs());
    }

    @Test
    public void testUpdateState_successAfterFail() {
        long ts = 1000L;
        Request request = createValidGenericPutRequest();
        request.setId(1);
        request.updateState(false, ts);
        request.updateState(true, ts * 2);
        Assert.assertEquals(-1, request.getReties());
        Assert.assertEquals(ts, request.getLastRetryMs());
        Assert.assertEquals(ts, request.getScheduledTime());
    }

    @Test
    public void testUpdateState_successAfterSuccess_Idempotent() {
        long ts = 1000L;
        Request request = createValidGenericPutRequest();
        request.setId(1);
        request.updateState(true, ts);
        request.updateState(true, ts * 2);
        Assert.assertEquals(-1, request.getReties());
        Assert.assertEquals(0, request.getLastRetryMs());
        Assert.assertEquals(0, request.getScheduledTime());
    }

    @Test
    public void testUpdateState_invalidTimestamp_throwsException() {
        long ts = 10;
        Request request = createValidGenericPutRequest();
        request.setId(1);
        request.updateState(false, ts);

        this.exceptionRule.expect(RuntimeException.class);
        this.exceptionRule.expectMessage("INVALID_TIMESTAMP");

        // Fail try
        request.updateState(false, 5);
        Assert.assertEquals(ts, request.getLastRetryMs());
        Assert.assertEquals(1, request.getReties());
        Assert.assertEquals(ts, request.getScheduledTime());

        // Success try
        request.updateState(true, 3);
        Assert.assertEquals(ts, request.getLastRetryMs());
        Assert.assertEquals(1, request.getReties());
        Assert.assertEquals(ts, request.getScheduledTime());
    }

    @Test
    public void testUpdateState_failed() {
        long ts = 10;
        Request request = createValidGenericPutRequest();
        request.setId(1);

        for (int retry = 1; retry < Constants.MAX_BACKOFF_LIMIT; retry++) {
            long exponent = (long) Math.pow(2, retry) * 1000;
            request.updateState(false, ts);
            Assert.assertEquals(ts, request.getLastRetryMs());
            Assert.assertEquals(retry, request.getReties());
            Assert.assertEquals(exponent + ts, request.getScheduledTime());
        }
    }

    @Test
    public void testUpdateState_failedAfterAlreadySuccess_throwsException() {
        long ts = 10;
        Request request = createValidGenericPutRequest();

        this.exceptionRule.expect(RuntimeException.class);
        this.exceptionRule.expectMessage("STATE_IMMUTABLE");

        request.updateState(true, ts);
        request.updateState(false, ts * 2);

        Assert.assertEquals(1, request.getReties());
        Assert.assertEquals(0, request.getScheduledTime());
        Assert.assertEquals(0, request.getLastRetryMs());
    }

    private Request createValidGenericPutRequest() {
        return Request.put("http://www.cc.com/", "{payload}");
    }
}
