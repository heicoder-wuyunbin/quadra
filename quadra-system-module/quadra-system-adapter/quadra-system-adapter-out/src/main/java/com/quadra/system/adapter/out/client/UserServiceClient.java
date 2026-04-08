package com.quadra.system.adapter.out.client;

import com.quadra.system.application.port.in.dto.UserAdminDTO;
import com.quadra.system.application.port.in.dto.UserDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "quadra-user", path = "/users/admin/users")
public interface UserServiceClient {

    @GetMapping
    UserServiceResult<UserServicePageResult<UserAdminDTO>> listUsers(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @GetMapping("/{id}")
    UserServiceResult<UserDetailDTO> getUserDetail(@PathVariable("id") String id);

    @PutMapping("/{id}/status")
    UserServiceResult<Void> updateStatus(
            @PathVariable("id") String id,
            @RequestBody UpdateUserStatusRequest request
    );

    @PostMapping("/{id}/reset-password")
    UserServiceResult<ResetPasswordResult> resetPassword(@PathVariable("id") String id);
}
