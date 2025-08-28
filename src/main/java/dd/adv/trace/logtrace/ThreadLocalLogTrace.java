package dd.adv.trace.logtrace;

import dd.adv.trace.TraceId;
import dd.adv.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * FieldLogTrace 의 동시성 문제 해결
 * ThreadLocal 을 사용해서 각 스레드마다 별도의 변수를 가지도록 설계
 *
 * ThreadLocal이란?
 * - 각 thread마다 자신만의 독립적인 저장공간을 갖게 해주는 기술. 여러 스레드가 같은
 *   ThreadLocal 변수에 접근하더라도, 실제로는 각자 자신의 공간에 있는 데이터를 사용하므로 다른 스레드에 영향을 주지 않음
 *   즉, 기존에는 traceIdHolder라는 공용 사물함 하나를 같이 쓰려고해서 서로 물건을 넣고 빼는 과정에서 엉망이됨.
 *   ThreadLocal은 이 문제를 해결하기 위해 스레드마다 개인 사물함을 하나씩 나눠주는 관리인 역할을 함
 *
 * 주요 메소드?
 * - theradLocal.set(): 관리인에게 가서 "내 개인 사물함에 이 물건좀 넣어주세요" 라고 요청하는ㄱ서
 * - threadLocal.get(): "제 개인 사물함에 있는 물건좀 꺼내주세요" 라고 하는것
 */
@Slf4j
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString());
        }

        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove(); // ThreadLocal 사용 후 꼭 remove를 호출해서 ThreadLocal 에 저장된 값 제거
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }

}
