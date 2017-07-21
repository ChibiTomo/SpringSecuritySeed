package net.chibidevteam.restappliseed.secret.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.chibidevteam.restappliseed.main.dto.AuthenticationRequest;
import net.chibidevteam.restappliseed.main.dto.AuthenticationResponse;
import net.chibidevteam.restappliseed.main.service.TokenManagerService;

@RestController
@RequestMapping("${net.chibidevteam.securityseed.login.process}")
public class RESTAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService    userDetailsService;
    @Autowired
    private TokenManagerService   tokenManagerService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        // Perform the authentication
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-authentication so we can generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = tokenManagerService.generateToken(userDetails);

        // // Return the token
        // return ResponseEntity.ok(new AuthenticationResponse(token));
        return new ResponseEntity<>(new AuthenticationResponse(token), HttpStatus.OK);
    }
}
