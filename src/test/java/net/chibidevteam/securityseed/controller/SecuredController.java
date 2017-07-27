package net.chibidevteam.securityseed.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.chibidevteam.securityseed.testconfig.TokenParamConfig;

@RestController
@RequestMapping(TokenParamConfig.SECURED_BASE_PATH)
public class SecuredController {

    @ResponseBody
    @RequestMapping(TokenParamConfig.SUB_PATH)
    public String hello() {
        return TokenParamConfig.SECURED_RESPONSE;
    }

}
