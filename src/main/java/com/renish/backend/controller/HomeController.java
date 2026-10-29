package com.renish.backend.controller;


import com.renish.backend.swagger.SwaggerOperations;
import com.renish.backend.swagger.SwaggerResponses;
import com.renish.backend.swagger.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {


    @GetMapping("/")

    @Operation(
            summary = SwaggerOperations.Home.HOME_SUMMARY,
            description = SwaggerOperations.Home.HOME_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            )
    })
    @Tag(name = SwaggerTags.HOME, description = SwaggerTags.HOME_DESCRIPTION)
    @ResponseBody
    public String home() {
        return "Welcome to the Home Page!";
    }
}
