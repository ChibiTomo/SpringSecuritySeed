package net.chibidevteam.securityseed.test.test.integration;

import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.ADMIN_PASSWORD;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.AUTH_HEADER_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.PUBLIC_AUTHENTICATED_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.PUBLIC_BASE_PATH;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.PUBLIC_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.SECURED_BASE_PATH;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.SECURED_RESPONSE;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.SUB_PATH;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_LOGIN;
import static net.chibidevteam.securityseed.test.config.AbstractConfig.USER_PASSWORD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.test.config.HttpBasicConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SecurityConfig.class, HttpBasicConfig.class })

@WebAppConfiguration
public class HttpBasicTest extends AbstractControllerTest {

    @Test
    public void canAccessPublic() throws Exception {
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);
    }

    @Test
    public void cannotAccessSecured() throws Exception {
        MvcResult result = expectGet(SECURED_BASE_PATH + SUB_PATH, HttpStatus.UNAUTHORIZED.value(), null) //
                .andExpect(header().string(AUTH_HEADER_RESPONSE, "Basic realm=\"" + getConfig().getBasicRealm() + "\""))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        logger.debug(response.getHeader(AUTH_HEADER_RESPONSE));
    }

    @Test
    public void auth() throws Exception {
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);

        authBasic(PUBLIC_BASE_PATH + SUB_PATH, ADMIN_LOGIN, ADMIN_PASSWORD, HttpStatus.OK.value(),
                PUBLIC_AUTHENTICATED_RESPONSE);
        authBasic(PUBLIC_BASE_PATH + SUB_PATH, null, null, HttpStatus.OK.value(), PUBLIC_RESPONSE);
        authBasic(SECURED_BASE_PATH + SUB_PATH, ADMIN_LOGIN, ADMIN_PASSWORD, HttpStatus.OK.value(), SECURED_RESPONSE);
        authBasic(SECURED_BASE_PATH + SUB_PATH, null, null, HttpStatus.UNAUTHORIZED.value(), null).andExpect(
                header().string(AUTH_HEADER_RESPONSE, "Basic realm=\"" + getConfig().getBasicRealm() + "\""));
    }

    @Test
    public void otherAuthFail() throws Exception {
        authTokenHeaderFail(USER_LOGIN, USER_PASSWORD, HttpStatus.UNAUTHORIZED.value());
        authTokenParamFail(USER_LOGIN, USER_PASSWORD, HttpStatus.UNAUTHORIZED.value());
    }
}
