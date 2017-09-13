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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.test.config.TokenHeaderConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SecurityConfig.class, TokenHeaderConfig.class })
@WebAppConfiguration
public class TokenHeaderTest extends AbstractControllerTest {

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
        authTokenHeader(WRONG_LOGIN, WRONG_PASSWORD, HttpStatus.UNAUTHORIZED.value());
        authTokenHeader(USER_DISABLED_LOGIN, USER_DISABLED_PASSWORD, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void authSuccess() throws Exception {
        authTokenHeader(USER_LOGIN, USER_PASSWORD, HttpStatus.OK.value());
        authTokenHeader(ADMIN_LOGIN, ADMIN_PASSWORD, HttpStatus.OK.value());
    }

    @Test
    public void auth() throws Exception {
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);

        ResultActions resultActions = authTokenHeader(USER_LOGIN, USER_PASSWORD, HttpStatus.OK.value());
        MvcResult result = resultActions.andReturn();

        MockHttpServletResponse response = result.getResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.add(getConfig().getTokenHeader(), response.getHeader(getConfig().getTokenHeader()));
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, headers, HttpStatus.OK.value(), PUBLIC_AUTHENTICATED_RESPONSE);

        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);
        expectPost(SECURED_BASE_PATH + SUB_PATH, headers, HttpStatus.OK.value(), SECURED_RESPONSE);
    }

    @Test
    public void otherAuthFail() throws Exception {
        authTokenParamFail(USER_LOGIN, USER_PASSWORD, HttpStatus.OK.value());
    }

}
