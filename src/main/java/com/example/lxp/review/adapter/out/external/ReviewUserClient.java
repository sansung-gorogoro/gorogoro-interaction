package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.adapter.out.external.dto.UserNicknameResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-nickname-api",
        url = "${user-api.url:http://localhost:8080}"
)
public interface ReviewUserClient {

    @GetMapping("/server/users/nickname")
    UserNicknameResponse getNickname(@RequestParam Long userId);
}
