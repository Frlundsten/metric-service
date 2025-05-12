package com.helidon.adapter;


import com.helidon.adapter.common.RepositoryId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatException;

class RepositoryIdTest {

    @ParameterizedTest
    @NullAndEmptySource
    void testCannotBeNull(String id) {
       assertThatException().isThrownBy(() -> new RepositoryId(id));
    }
}
