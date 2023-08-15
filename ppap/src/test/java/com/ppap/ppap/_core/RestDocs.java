package com.ppap.ppap._core;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap._core.security.JwtProvider;
import com.ppap.ppap.domain.user.Entity.User;
import com.ppap.ppap.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;


@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith({ SpringExtension.class, RestDocumentationExtension.class })
public class RestDocs {
    protected MockMvc mvc;
    protected RestDocumentationResultHandler document;

    protected final String snippt = "{class-name}/{method-name}";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected ObjectMapper om;

    @BeforeEach
    private void setup(WebApplicationContext webApplicationContext,
                       RestDocumentationContextProvider restDocumentation) {
        this.document = MockMvcRestDocumentationWrapper.document("{class-name}/{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(document)
                .build();
    }

    public OperationRequestPreprocessor getDocumentRequest(){
        return Preprocessors.preprocessRequest(Preprocessors.prettyPrint());
    }

    public OperationResponsePreprocessor getDocumentResponse() {
        return Preprocessors.preprocessResponse(Preprocessors.prettyPrint());
    }

    protected String getAccessToken(String email) {
        return JwtProvider.create(getUser(email));
    }

    protected String getRefreshToken(String email) {
        return JwtProvider.createRefreshToken(getUser(email));
    }

    protected User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new Exception404(BaseExceptionStatus.USER_NOT_FOUND));
    }
}
