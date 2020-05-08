package ca.hedman.springboothapifhirsimpleserver;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
public class CustomLoggingInterceptor {

    private final Logger log = LoggerFactory.getLogger(CustomLoggingInterceptor.class);

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_HANDLED)
    public void logRequests(RequestDetails rq) {
        log.info("Incoming Request: {} {}", rq.getRequestType().name(), rq.getCompleteUrl());
    }

}
