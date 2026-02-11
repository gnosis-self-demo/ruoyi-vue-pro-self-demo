package gnosis.sample.distribute.queue.processor.impl;

import gnosis.sample.distribute.queue.dto.BusinessProcessRequest;
import gnosis.sample.distribute.queue.dto.BusinessProcessResult;
import gnosis.sample.distribute.queue.processor.AbstractDistributeQueueBusinessProcessor;
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
