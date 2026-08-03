package com.shoestore.shared.web.error;

import com.shoestore.shared.application.error.ApplicationException;
import com.shoestore.shared.application.error.CommonErrorCode;
import com.shoestore.shared.web.correlation.RequestCorrelation;
import com.shoestore.shared.web.correlation.RequestCorrelationFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private static final String CORRELATION_ID =
            "6ae2d8b2-94fe-460b-ab14-b73209197542";

    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .addFilters(new RequestCorrelationFilter())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldHandleInvalidRequestApplicationException()
            throws Exception {
        mockMvc.perform(
                        get("/test/application-invalid")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().string(
                        RequestCorrelation.HEADER_NAME,
                        CORRELATION_ID
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INVALID_REQUEST"
                ))
                .andExpect(jsonPath("$.message").value(
                        "Custom invalid request message."
                ))
                .andExpect(jsonPath("$.path").value(
                        "/test/application-invalid"
                ))
                .andExpect(jsonPath("$.correlationId").value(
                        CORRELATION_ID
                ))
                .andExpect(jsonPath("$.violations").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldHideInternalApplicationExceptionMessage()
            throws Exception {
        mockMvc.perform(
                        get("/test/application-internal")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INTERNAL_ERROR"
                ))
                .andExpect(jsonPath("$.message").value(
                        "An unexpected error occurred."
                ))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.not(
                                "Database password was exposed."
                        )
                ));
    }

    @Test
    void shouldHandleRequestBodyValidationFailure()
            throws Exception {
        mockMvc.perform(
                        post("/test/validation")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "email": "invalid-email",
                                          "name": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INVALID_REQUEST"
                ))
                .andExpect(jsonPath("$.message").value(
                        "Request validation failed."
                ))
                .andExpect(jsonPath("$.violations.length()")
                        .value(2))
                .andExpect(jsonPath("$.violations[0].field")
                        .value("email"))
                .andExpect(jsonPath("$.violations[0].message")
                        .value("must be a well-formed email address"))
                .andExpect(jsonPath("$.violations[1].field")
                        .value("name"))
                .andExpect(jsonPath("$.violations[1].message")
                        .value("must not be blank"));
    }

    @Test
    void shouldHandleMalformedRequestBody()
            throws Exception {
        mockMvc.perform(
                        post("/test/validation")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "email": "user@example.com",
                                          "name":
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INVALID_REQUEST"
                ))
                .andExpect(jsonPath("$.message").value(
                        "Request body is malformed or unreadable."
                ))
                .andExpect(jsonPath("$.violations").isEmpty());
    }

    @Test
    void shouldHandleMissingRequestParameter()
            throws Exception {
        mockMvc.perform(
                        get("/test/parameter")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INVALID_REQUEST"
                ))
                .andExpect(jsonPath("$.message").value(
                        "A request parameter is missing or invalid."
                ));
    }

    @Test
    void shouldHandleRequestParameterTypeMismatch()
            throws Exception {
        mockMvc.perform(
                        get("/test/parameter")
                                .param("page", "not-a-number")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INVALID_REQUEST"
                ))
                .andExpect(jsonPath("$.message").value(
                        "A request parameter is missing or invalid."
                ));
    }

    @Test
    void shouldHandleUnexpectedExceptionWithoutLeakingMessage()
            throws Exception {
        mockMvc.perform(
                        get("/test/unexpected")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(
                        "COMMON_INTERNAL_ERROR"
                ))
                .andExpect(jsonPath("$.message").value(
                        "An unexpected error occurred."
                ))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.not(
                                "Sensitive implementation detail."
                        )
                ));
    }

    @Test
    void shouldGenerateCorrelationIdWhenHeaderIsMissing()
            throws Exception {
        mockMvc.perform(get("/test/application-invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(
                        RequestCorrelation.HEADER_NAME,
                        matchesPattern(
                                "^[0-9a-fA-F]{8}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{12}$"
                        )
                ))
                .andExpect(jsonPath("$.correlationId")
                        .value(matchesPattern(
                                "^[0-9a-fA-F]{8}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{12}$"
                        )));
    }

    @Test
    void shouldReplaceInvalidCorrelationId()
            throws Exception {
        mockMvc.perform(
                        get("/test/application-invalid")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        "not-a-valid-correlation-id"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().string(
                        RequestCorrelation.HEADER_NAME,
                        org.hamcrest.Matchers.not(
                                "not-a-valid-correlation-id"
                        )
                ))
                .andExpect(jsonPath("$.correlationId")
                        .value(matchesPattern(
                                "^[0-9a-fA-F]{8}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{4}-"
                                        + "[0-9a-fA-F]{12}$"
                        )));
    }

    @Test
    void generatedCorrelationIdShouldBeValidUuid()
                    throws Exception {
            String correlationId = mockMvc.perform(
                            get("/test/application-invalid"))
                            .andReturn()
                            .getResponse()
                            .getHeader(
                                            RequestCorrelation.HEADER_NAME);

            org.assertj.core.api.Assertions
                            .assertThat(correlationId)
                            .isNotNull();

            org.assertj.core.api.Assertions
                            .assertThatNoException()
                            .isThrownBy(() -> UUID.fromString(correlationId));
    }

    @Test
        void filterAndErrorBodyShouldUseSameCorrelationId()
                throws Exception {
        String responseHeader = mockMvc.perform(
                        get("/test/application-invalid")
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getHeader(RequestCorrelation.HEADER_NAME);

        org.assertj.core.api.Assertions
                .assertThat(responseHeader)
                .isNotBlank();

        mockMvc.perform(
                        get("/test/application-invalid")
                                .header(
                                        RequestCorrelation.HEADER_NAME,
                                        CORRELATION_ID
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().string(
                        RequestCorrelation.HEADER_NAME,
                        CORRELATION_ID
                ))
                .andExpect(jsonPath("$.correlationId")
                        .value(CORRELATION_ID));
        }

    @RestController
    static class TestController {

        @GetMapping("/test/application-invalid")
        void throwInvalidRequest() {
            throw new ApplicationException(
                    CommonErrorCode.INVALID_REQUEST,
                    "Custom invalid request message."
            );
        }

        @GetMapping("/test/application-internal")
        void throwInternalApplicationException() {
            throw new ApplicationException(
                    CommonErrorCode.INTERNAL_ERROR,
                    "Database password was exposed."
            );
        }

        @PostMapping("/test/validation")
        void validateRequest(
                @Valid @RequestBody TestRequest request
        ) {
            // No operation required for the exception-handler fixture.
        }

        @GetMapping("/test/parameter")
        void requireIntegerParameter(
                @RequestParam int page
        ) {
            // No operation required for the exception-handler fixture.
        }

        @GetMapping("/test/unexpected")
        void throwUnexpectedException() {
            throw new IllegalStateException(
                    "Sensitive implementation detail."
            );
        }
    }

    record TestRequest(
            @Email String email,
            @NotBlank String name
    ) {
    }
}
