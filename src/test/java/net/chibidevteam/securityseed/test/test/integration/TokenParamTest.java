package net.chibidevteam.securityseed.test.test.integration;

import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.PUBLIC_AUTHENTICATED_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.PUBLIC_BASE_PATH;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.PUBLIC_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.SECURED_BASE_PATH;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.SECURED_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.SUB_PATH;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_DISABLED_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_DISABLED_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.WRONG_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.WRONG_PASSWORD;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.test.config.TokenParamConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SecurityConfig.class, TokenParamConfig.class })
@WebAppConfiguration
public class TokenParamTest extends AbstractControllerTest {

    @Test
    public void canAccessPublic() throws Exception {
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);
    }

    @Test
    public void cannotAccessSecured() throws Exception {
        expectGet(SECURED_BASE_PATH + SUB_PATH, HttpStatus.FORBIDDEN.value(), "");
    }

    @Test
    public void authFail() throws Exception {
        authTokenParam(WRONG_LOGIN, WRONG_PASSWORD, HttpStatus.UNAUTHORIZED.value());
        authTokenParam(USER_DISABLED_LOGIN, USER_DISABLED_PASSWORD, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void authSuccess() throws Exception {
        authTokenParam(USER_LOGIN, USER_PASSWORD, HttpStatus.OK.value());
        authTokenParam(ADMIN_LOGIN, ADMIN_PASSWORD, HttpStatus.OK.value());
    }

    @Test
    public void auth() throws Exception {
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);

        ResultActions resultActions = authTokenParam(USER_LOGIN, USER_PASSWORD, HttpStatus.OK.value());
        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        JSONObject obj = new JSONObject(response.getContentAsString());
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(getConfig().getTokenParam(), obj.getString(getConfig().getTokenParam()));

        expectPost(PUBLIC_BASE_PATH + SUB_PATH, params, HttpStatus.OK.value(), PUBLIC_AUTHENTICATED_RESPONSE);
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);
        expectPost(SECURED_BASE_PATH + SUB_PATH, params, HttpStatus.OK.value(), SECURED_RESPONSE);
        expectGet(SECURED_BASE_PATH + SUB_PATH, HttpStatus.FORBIDDEN.value(), "");
    }

    @Test
    public void otherAuthFail() throws Exception {
        authTokenHeaderFail(USER_LOGIN, USER_PASSWORD, HttpStatus.OK.value());
    }
}
