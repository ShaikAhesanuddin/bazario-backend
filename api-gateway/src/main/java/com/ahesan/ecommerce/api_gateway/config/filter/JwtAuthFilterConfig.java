package com.ahesan.ecommerce.api_gateway.config.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtAuthFilterConfig {

    private boolean required;
    private List<String> roles;
}
