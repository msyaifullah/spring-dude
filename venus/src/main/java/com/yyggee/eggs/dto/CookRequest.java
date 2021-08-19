package com.yyggee.eggs.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yyggee.eggs.model.ds1.Auditor;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CookRequest {

    @NonNull private String username;
    @NonNull private String method;
    @NonNull private String status;
    @NonNull private String url;

    public boolean equals(Auditor auditor) {
        if (auditor == null) return false;
        return Objects.equals(username, auditor.getUsername()) &&
                Objects.equals(method, auditor.getMethod()) &&
                Objects.equals(status, auditor.getStatus()) &&
                Objects.equals(url, auditor.getUrl());
    }

}

