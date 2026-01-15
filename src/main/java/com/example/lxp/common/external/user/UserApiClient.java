package com.example.lxp.common.external.user;

import com.example.lxp.common.external.user.dto.UserNicknameResponse;
import com.example.lxp.common.external.user.dto.UserNicknamesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "user-api",
        url = "${user-api.url:http://localhost:8080}"
)
public interface UserApiClient {

    @GetMapping("/server/users/nickname")
    UserNicknameResponse getNickname(@RequestParam Long userId);

    @GetMapping("/server/users/nicknames")
    UserNicknamesResponse getNicknames(@RequestParam List<Long> userIds);
    
}
