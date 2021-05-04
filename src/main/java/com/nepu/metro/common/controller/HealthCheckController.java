package com.nepu.metro.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HealthCheckController {

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/healthcheck", method = RequestMethod.GET)
    public ResponseEntity healthcheck(Model model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return new ResponseEntity(HttpStatus.OK);
    }
}
