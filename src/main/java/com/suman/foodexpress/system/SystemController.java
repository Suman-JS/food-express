package com.suman.foodexpress.system;

import com.suman.foodexpress.auth.security.Public;
import com.suman.foodexpress.common.dto.ApiResponse;
import com.suman.foodexpress.system.dto.ServerInfoResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class SystemController {

  private final ServerInfoService serverInfoService;

  public SystemController(ServerInfoService serverInfoService) {
    this.serverInfoService = serverInfoService;
  }

  @GetMapping("/")
  @Public
  public ResponseEntity<ApiResponse<ServerInfoResponse>> info() {
    return ResponseEntity.ok(
        ApiResponse.success(serverInfoService.collect(), "Server information"));
  }
}
