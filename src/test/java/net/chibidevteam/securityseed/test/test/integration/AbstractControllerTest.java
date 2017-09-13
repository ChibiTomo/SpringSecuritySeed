package net.chibidevteam.securityseed.test.test.integration;

import static net.chibidevteam.securityseed.test.config.AbstractConfig.AUTH_HEADER;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_PASSWORD;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.test.util.Utils;

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

    // ~ Authentication : HTTP Basic
    // ============================================================================================================

    protected ResultActions authBasic(String url, String login, String password, int status, String body)
            throws Exception {
        return expectGet(url, buildBasicAuth(login, password), status, body);
    }

    private HttpHeaders buildBasicAuth(String login, String password) {
        if (login == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, "Basic " + Base64.getEncoder().encodeToString((login + ":" + password).getBytes()));
        return headers;
    }

    // ~ Authentication : Token Header
    // ============================================================================================================

    protected ResultActions authTokenHeaderFail(String login, String password, int status) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(getConfig().getUsernameParam(), login);
        params.add(getConfig().getPasswordParam(), password);
        ResultActions resultActions = expectPost(config.getLoginProcess(), params, status, null);
        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        Assert.assertTrue(StringUtils.isEmpty(response.getHeader(config.getTokenHeader())));
        return resultActions;
    }

    protected ResultActions authTokenHeader(String login, String password, int status) throws Exception {
        return authTokenHeader(login, password, status, "");
    }

    private ResultActions authTokenHeader(String login, String password, int status, String body) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(getConfig().getUsernameParam(), login);
        params.add(getConfig().getPasswordParam(), password);
        return expectPost(config.getLoginProcess(), params, status, body);
    }

    // ~ Authentication : Token Param
    // ============================================================================================================

    protected ResultActions authTokenParamFail(String login, String password, int status) throws Exception {
        ResultActions resultActions = authTokenParam(USER_LOGIN, USER_PASSWORD, status);
        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        String strResponse = response.getContentAsString();
        Assert.assertTrue(
                strResponse == null || strResponse.contains("HTTP Status 401") || StringUtils.isEmpty(strResponse));
        return resultActions;
    }

    protected ResultActions authTokenParam(String login, String password, int status) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(getConfig().getUsernameParam(), login);
        params.add(getConfig().getPasswordParam(), password);
        return expectPost(config.getLoginProcess(), params, status, null);
    }

    // ~ Expect : GET
    // ============================================================================================================

    protected ResultActions expectGet(String path, int status, String body) throws Exception {
        return expectGet(path, null, status, body);
    }

    protected ResultActions expectGet(String path, HttpHeaders headers, int status, String body) throws Exception {
        return expectGet(path, headers, null, status, body);
    }

    protected ResultActions expectGet(String path, MultiValueMap<String, String> params, int status, String body)
            throws Exception {
        return expectGet(path, null, params, status, body);
    }

    protected ResultActions expectGet(String path, HttpHeaders headers, MultiValueMap<String, String> params,
            int status, String body) throws Exception {
        logger.info("Performing GET on '" + path + "'");
        return expect(get(path), headers, null, status, body);
    }

    // ~ Expect : POST
    // ============================================================================================================

    protected ResultActions expectPost(String path, int status, String body) throws Exception {
        return expectPost(path, null, null, status, body);
    }

    protected ResultActions expectPost(String path, MultiValueMap<String, String> params, int status, String body)
            throws Exception {
        return expectPost(path, null, params, status, body);
    }

    protected ResultActions expectPost(String path, HttpHeaders headers, int status, String body) throws Exception {
        return expectPost(path, headers, null, status, body);
    }

    protected ResultActions expectPost(String path, HttpHeaders headers, MultiValueMap<String, String> params,
            int status, String body) throws Exception {
        logger.info("Performing POST on '" + path + "'");
        return expect(post(path), headers, params, status, body);
    }

    // ~ Expect
    // ============================================================================================================

    private ResultActions expect(MockHttpServletRequestBuilder rqBuilder, HttpHeaders headers,
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

        MockHttpServletResponse response = result.getResponse();
        if (status != -1) {
            logger.info("Expected status: " + status + ", Actual status: " + response.getStatus());
            resultActions.andExpect(status().is(status));
        }
        if (body != null) {
            logger.info("Expected body: '" + body + "', Actual body: '" + response.getContentAsString() + "'");
            Assert.assertEquals(body, response.getContentAsString());
        }
        return resultActions;
    }

}
