/*
 * Copyright (C) 2019 - 2020 Rabobank Nederland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabobank.argos.service.domain.account;

import com.rabobank.argos.domain.key.KeyPair;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PersonalAccount extends Account {
    @Builder
    public PersonalAccount(
            String name,
            String email,
            KeyPair activeKeyPair,
            List<KeyPair> inactiveKeyPairs,
            AuthenticationProvider provider,
            String providerId

    ) {
        super(randomUUID().toString(),
                name,
                email,
                activeKeyPair,
                inactiveKeyPairs == null ? emptyList() : inactiveKeyPairs);
        this.provider = provider;
        this.providerId = providerId;
    }
    private AuthenticationProvider provider;
    private String providerId;
}
