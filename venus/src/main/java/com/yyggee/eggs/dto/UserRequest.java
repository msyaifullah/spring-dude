package com.yyggee.eggs.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yyggee.eggs.model.ds1.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    @NotNull private String username;
    @NotNull private String email;
    @NotNull private String password;
    @NotNull List<Role> roles;

}

