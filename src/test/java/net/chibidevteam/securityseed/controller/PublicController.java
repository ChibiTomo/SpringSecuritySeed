package net.chibidevteam.securityseed.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.chibidevteam.securityseed.testconfig.TokenParamConfig;
import net.chibidevteam.securityseed.util.SecurityHelper;

@RestController
@RequestMapping(TokenParamConfig.PUBLIC_BASE_PATH)
public class PublicController {

    @ResponseBody
    @RequestMapping(TokenParamConfig.SUB_PATH)
    public String hello() {
        if (SecurityHelper.isAuthenticated()) {
            return TokenParamConfig.PUBLIC_AUTHENTICATED_RESPONSE;
        }
        return TokenParamConfig.PUBLIC_RESPONSE;
    }
}
