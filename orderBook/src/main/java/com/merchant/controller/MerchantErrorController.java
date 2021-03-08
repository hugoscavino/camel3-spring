package com.merchant.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.NestedServletException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MerchantErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // get error status
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMsg = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object errorException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // display specific error page
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", request.getContextPath());
                return "404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // add `message` attribute
                if (errorMsg != null && errorMsg.toString().isEmpty() == false) {
                    model.addAttribute("message", errorMsg.toString());
                } else if(errorException != null){
                    NestedServletException e = (NestedServletException)errorException;
                    model.addAttribute("message", e.getMessage());
                }
                return "500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "403";
            }
        }

        // display generic error
        return "error";
    }
}
