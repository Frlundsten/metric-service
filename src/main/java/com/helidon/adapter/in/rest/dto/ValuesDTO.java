package com.helidon.adapter.in.rest.dto;

/*
Marker interface
 */

import com.helidon.application.domain.model.Values;

public interface ValuesDTO {
    Values toDomain();
    ValuesDTO toDTO(Values domain);
}
