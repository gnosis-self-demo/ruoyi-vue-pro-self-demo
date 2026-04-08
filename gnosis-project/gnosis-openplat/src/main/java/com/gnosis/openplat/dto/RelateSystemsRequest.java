package com.gnosis.openplat.dto;

import lombok.Data;
import java.util.List;

@Data
public class RelateSystemsRequest {
    private String apiId;
    private List<String> systemIds;
}
