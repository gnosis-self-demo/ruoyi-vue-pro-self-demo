package com.gnosis.openplat.dto;

import lombok.Data;

@Data
public class ApiConfigCheckRequest {
    private String apiCode;
    private String apiPath;
}
