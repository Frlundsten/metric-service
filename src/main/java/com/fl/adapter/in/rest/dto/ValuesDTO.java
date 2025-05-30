package com.fl.adapter.in.rest.dto;

/*
Marker interface
 */

import com.fl.application.domain.model.Values;

public interface ValuesDTO {
    Values toDomain();
    ValuesDTO toDTO(Values domain);
}
