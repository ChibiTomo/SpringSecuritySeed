package net.chibidevteam.securityseed.test.integration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.util.Utils;

public abstract class AbstractControllerTest {

    protected final Log           logger = LogFactory.getLog(getClass());

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private SecurityConfig        config;

    protected MockMvc             mockMvc;
    @Autowired
    private FilterChainProxy      springSecurityFilterChain;

    @Before
    public void setUp() throws Exception {
        Utils.resetDefaults();
        Utils.applyConfig();
        SecurityContextHolder.clearContext();

        mockMvc = MockMvcBuilders //
                .webAppContextSetup(context) //
                .apply(springSecurity(springSecurityFilterChain)) //
                .build();
    }

    public SecurityConfig getConfig() {
        return config;
    }

    protected void expectGet(String path, int status, String body) throws Exception {
        expectGet(path, null, status, body);
    }

    protected void expectGet(String path, HttpHeaders headers, int status, String body) throws Exception {
        expectGet(path, headers, null, status, body);
    }

    protected void expectGet(String path, MultiValueMap<String, String> params, int status, String body)
            throws Exception {
        expectGet(path, null, params, status, body);
    }

    protected void expectGet(String path, HttpHeaders headers, MultiValueMap<String, String> params, int status,
            String body) throws Exception {
        logger.info("Performing GET on '" + path + "'");
        expect(get(path), headers, null, status, body);
    }

    protected void expectPost(String path, int status, String body) throws Exception {
        expectPost(path, null, null, status, body);
    }

    protected void expectPost(String path, MultiValueMap<String, String> params, int status, String body)
            throws Exception {
        expectPost(path, null, params, status, body);
    }

    protected void expectPost(String path, HttpHeaders headers, int status, String body) throws Exception {
        expectPost(path, headers, null, status, body);
    }

    protected void expectPost(String path, HttpHeaders headers, MultiValueMap<String, String> params, int status,
            String body) throws Exception {
        logger.info("Performing POST on '" + path + "'");
        expect(post(path), headers, params, status, body);
    }

    private void expect(MockHttpServletRequestBuilder rqBuilder, HttpHeaders headers,
            MultiValueMap<String, String> params, int status, String body) throws Exception {
        if (headers != null && !headers.isEmpty()) {
            logger.info("Headers are: " + headers);
            rqBuilder.headers(headers);
        }
        if (params != null && !params.isEmpty()) {
            logger.info("Params are: " + params);
            rqBuilder.params(params);
        }
        ResultActions resultActions = mockMvc.perform(rqBuilder);
        MvcResult result = resultActions.andReturn();

        logger.info("Expected status: " + status + ", Actual status: " + result.getResponse().getStatus());
        logger.info("Expected body: '" + body + "', Actual body: '" + result.getResponse().getContentAsString() + "'");
        resultActions.andExpect(status().is(status));
        Assert.assertEquals(body, result.getResponse().getContentAsString());
    }

}
