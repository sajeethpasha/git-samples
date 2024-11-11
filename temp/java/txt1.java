package com.sherwin.edps.batchmgmtbff.clients;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class WorkOrderDomainClientLoggingDecoratorTest {

    @Mock
    private Logger logger;

    @Mock
    private WorkOrderDomainClient decorated;

    @InjectMocks
    private WorkOrderDomainClientLoggingDecorator loggingDecorator;

    @Test
    void updateWorkOrder() {
        // Arrange
        WorkOrderUpdateRequest request = new WorkOrderUpdateRequest();
        Mono<WorkOrder> expectedResponse = Mono.just(new WorkOrder());
        when(decorated.updateWorkOrder(any(WorkOrderUpdateRequest.class))).thenReturn(expectedResponse);

        // Act
        Mono<WorkOrder> result = loggingDecorator.updateWorkOrder(request);

        // Assert
        assertEquals(expectedResponse, result);
        verify(logger, times(1)).info(eq("Update work order with request body: {}"), anyString());
        verify(decorated, times(1)).updateWorkOrder(request);
    }
}

// Additional mock classes for compilation purposes
class WorkOrderDomainClient {
    public Mono<WorkOrder> updateWorkOrder(WorkOrderUpdateRequest request) {
        return Mono.empty();
    }
}

class WorkOrderUpdateRequest {
    // Fields and methods for WorkOrderUpdateRequest
}

class WorkOrder {
    // Fields and methods for WorkOrder
}
