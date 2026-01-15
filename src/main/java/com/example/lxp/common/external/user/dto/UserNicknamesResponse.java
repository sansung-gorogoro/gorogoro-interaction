package com.example.lxp.common.external.user.dto;

import java.util.List;
import java.util.Map;

public record UserNicknamesResponse(
        Map<Long, String> nicknames,
        List<Long> missingUserIds
) {
}
