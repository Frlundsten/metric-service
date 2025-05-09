package com.helidon.adapter;


import com.helidon.adapter.common.RepositoryId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatException;

class RepositoryIdTest {

    @Test
    void testCannotBeNull() {
       assertThatException().isThrownBy(() -> new RepositoryId(null)).withMessage("Repository id cannot be null");
    }
}
