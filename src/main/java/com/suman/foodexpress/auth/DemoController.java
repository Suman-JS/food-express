package com.suman.foodexpress.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suman.foodexpress.auth.dto.ApiResponse;
import com.suman.foodexpress.websocket.WebSocketEventEmitter;

@RestController
@RequestMapping("/api/v1")
public class DemoController {

    private final WebSocketEventEmitter eventEmitter;

    public DemoController(WebSocketEventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/demo/ws-emit/{event}")
    public ResponseEntity<ApiResponse<Void>> demoWsEmit(@PathVariable String event,
            @RequestBody(required = false) Object payload) {
        eventEmitter.emit(event, payload != null ? payload : java.util.Map.of());
        return ResponseEntity.ok(ApiResponse.success(null, "Event emitted: " + event));
    }

    @GetMapping("/public/info")
    public ResponseEntity<ApiResponse<String>> publicInfo() {
        return ResponseEntity.ok(ApiResponse.success("Anyone can see this"));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/profile")
    public ResponseEntity<ApiResponse<String>> userProfile() {
        return ResponseEntity.ok(ApiResponse.success("Only USER role"));
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @GetMapping("/restaurant/dashboard")
    public ResponseEntity<ApiResponse<String>> restaurantDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Only RESTAURANT_OWNER role"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Only ADMIN role"));
    }
}