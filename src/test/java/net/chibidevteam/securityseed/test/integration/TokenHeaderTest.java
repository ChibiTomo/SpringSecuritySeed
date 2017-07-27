package net.chibidevteam.securityseed.test.integration;

import static net.chibidevteam.securityseed.testconfig.AbstractConfig.PUBLIC_AUTHENTICATED_RESPONSE;
import static net.chibidevteam.securityseed.testconfig.AbstractConfig.PUBLIC_BASE_PATH;
import static net.chibidevteam.securityseed.testconfig.AbstractConfig.PUBLIC_RESPONSE;
import static net.chibidevteam.securityseed.testconfig.AbstractConfig.SECURED_BASE_PATH;
import static net.chibidevteam.securityseed.testconfig.AbstractConfig.SECURED_RESPONSE;
import static net.chibidevteam.securityseed.testconfig.AbstractConfig.SUB_PATH;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import net.chibidevteam.securityseed.config.SecurityConfig;
import net.chibidevteam.securityseed.testconfig.TokenHeaderConfig;

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
    public void auth() throws Exception {
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(getConfig().getUsernameParam(), "user");
        params.add(getConfig().getPasswordParam(), "hello");
        MockHttpServletResponse response = expectPost(getConfig().getLoginProcess(), params, HttpStatus.OK.value(), "");

        HttpHeaders headers = new HttpHeaders();
        headers.add(getConfig().getTokenHeader(), response.getHeader(getConfig().getTokenHeader()));
        expectGet(PUBLIC_BASE_PATH + SUB_PATH, headers, HttpStatus.OK.value(), PUBLIC_AUTHENTICATED_RESPONSE);

        expectGet(PUBLIC_BASE_PATH + SUB_PATH, HttpStatus.OK.value(), PUBLIC_RESPONSE);
        expectPost(SECURED_BASE_PATH + SUB_PATH, headers, HttpStatus.OK.value(), SECURED_RESPONSE);
    }
}
