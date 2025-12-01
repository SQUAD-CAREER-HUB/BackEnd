package org.squad.careerhub.global.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.error.response.ErrorResponse;

@Component
public class SwaggerCustomizer implements OperationCustomizer {

    private static final String MEDIA_JSON = "application/json";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiExceptions apiExceptions = handlerMethod.getMethodAnnotation(ApiExceptions.class);
        if (apiExceptions != null) {
            generateErrorCodeResponseExample(operation, apiExceptions.values());
        }
        return operation;
    }

    private void generateErrorCodeResponseExample(Operation operation, ErrorStatus[] errorStatuses) {
        ApiResponses responses = operation.getResponses();

        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = Arrays.stream(errorStatuses)
                .map(errorStatus -> ExampleHolder.builder()
                        .holder(getSwaggerExample(errorStatus))
                        .name(errorStatus.name())
                        .code(errorStatus.getStatusCode())
                        .build()
                )
                .collect(Collectors.groupingBy(ExampleHolder::code));

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private Example getSwaggerExample(ErrorStatus errorStatus) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(errorStatus.getStatusCode())
                .message(errorStatus.getMessage())
                .build();
        Example example = new Example();
        example.setValue(errorResponse);
        return example;
    }

    private void addExamplesToResponses(
            ApiResponses responses,
            Map<Integer, List<ExampleHolder>> statusWithExampleHolders
    ) {
        statusWithExampleHolders.forEach((status, examples) -> {
            Content content = new Content();
            MediaType mediaType = new MediaType();
            ApiResponse apiResponse = new ApiResponse();

            examples.forEach(exampleHolder -> mediaType.addExamples(
                    exampleHolder.name(),
                    exampleHolder.holder()
            ));
            content.addMediaType(MEDIA_JSON, mediaType);
            apiResponse.setContent(content);
            responses.addApiResponse(String.valueOf(status), apiResponse);
        });
    }

}