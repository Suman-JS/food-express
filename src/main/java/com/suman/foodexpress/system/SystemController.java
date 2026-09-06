package com.suman.foodexpress.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suman.foodexpress.common.dto.ApiResponse;
import com.suman.foodexpress.system.dto.ServerInfoResponse;

@RestController
public class SystemController {

    private final ServerInfoService serverInfoService;

    public SystemController(ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<ServerInfoResponse>> info() {
        return ResponseEntity.ok(ApiResponse.success(serverInfoService.collect(), "Server information"));
    }
}