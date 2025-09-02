package dd.adv.trace.callback;


import dd.adv.trace.TraceStatus;
import dd.adv.trace.logtrace.LogTrace;

/**
 * 템플릿 콜백 패턴 예제.
 * ContextV2와 내용이 같고 이름만 다름
 * Context -> Template
 * Strategy -> Callback
 */
public class TraceTemplate {

    private final LogTrace trace;

    public TraceTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public <T> T execute(String message, TraceCallback<T> callback) {
        TraceStatus status = null;
        try {
            status = trace.begin(message);
            //로직 호출
            T result = callback.call();

            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }


}
