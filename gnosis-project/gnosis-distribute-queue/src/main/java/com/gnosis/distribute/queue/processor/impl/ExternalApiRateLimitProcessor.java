package com.gnosis.distribute.queue.processor.impl;

import com.gnosis.distribute.queue.dto.BusinessProcessRequest;
import com.gnosis.distribute.queue.dto.BusinessProcessResult;
import com.gnosis.distribute.queue.processor.AbstractDistributeQueueBusinessProcessor;
import org.springframework.stereotype.Service;

@Service
public class ExternalApiRateLimitProcessor extends AbstractDistributeQueueBusinessProcessor {

    @Override
    public BusinessProcessResult process(BusinessProcessRequest request) throws Exception {
        return null;
    }

    @Override
    public String getSupportedQueueType() {
        return "external-api-rate-limit";
    }

    @Override
    public String getName() {
        return "external-api-rate-limit";
    }
}
