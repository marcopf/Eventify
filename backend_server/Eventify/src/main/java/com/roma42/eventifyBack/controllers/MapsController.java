package com.roma42.eventifyBack.controllers;

import com.roma42.eventifyBack.services.MapService;
import com.google.maps.errors.ApiException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/map")
@CrossOrigin(origins = "${client.protocol}://${client.ip}:${client.port}", allowCredentials = "true")
public class MapsController {

    @GetMapping("")
    public String getMap(@RequestParam("address") String address) throws IOException,
            InterruptedException, ApiException {
        return MapService.makeRequest(address);
    }
}