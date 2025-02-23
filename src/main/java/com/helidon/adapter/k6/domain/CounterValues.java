package com.helidon.adapter.k6.domain;

import com.helidon.application.domain.model.Values;

public record CounterValues(double count, double rate) implements Values {}
