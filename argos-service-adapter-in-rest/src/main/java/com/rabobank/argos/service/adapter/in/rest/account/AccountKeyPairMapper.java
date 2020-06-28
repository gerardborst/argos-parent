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
package com.rabobank.argos.service.adapter.in.rest.account;

import com.rabobank.argos.domain.crypto.KeyAlgorithm;
import com.rabobank.argos.domain.crypto.KeyIdProvider;
import com.rabobank.argos.domain.crypto.KeyPair;
import com.rabobank.argos.domain.crypto.PublicKey;
import com.rabobank.argos.domain.crypto.ServiceAccountKeyPair;
import com.rabobank.argos.domain.crypto.ServiceAccountKeyPair.ServiceAccountKeyPairBuilder;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestKeyPair;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestPublicKey;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestServiceAccountKeyPair;

import java.util.Arrays;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class AccountKeyPairMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(source = "hashedKeyPassphrase", target = "encryptedHashedKeyPassphrase", qualifiedByName = "encryptHashedKeyPassphrase")
    public abstract ServiceAccountKeyPair convertFromRestKeyPair(RestServiceAccountKeyPair keyPair);
    /*
        if ( keyPair == null ) {
            return null;
        }

        ServiceAccountKeyPairBuilder serviceAccountKeyPairBuilder = ServiceAccountKeyPair.builder();

        byte[] publicKey = keyPair.getPublicKey();
        if ( publicKey != null ) {
        	serviceAccountKeyPairBuilder
        		.keyId(KeyIdProvider.computeKeyId(publicKey))
        		.publicKey( Arrays.copyOf( publicKey, publicKey.length ))
        		.algorithm(KeyAlgorithm.valueOf(keyPair.getAlgorithm().name()));
        }
        
        byte[] encryptedPrivateKey = keyPair.getEncryptedPrivateKey();
        if ( encryptedPrivateKey != null ) {
        	serviceAccountKeyPairBuilder
        		.encryptedPrivateKey( Arrays.copyOf( encryptedPrivateKey, encryptedPrivateKey.length ));
        }
        
        String hashedKeyPassphrase = keyPair.getHashedKeyPassphrase();
        if (hashedKeyPassphrase != null) {
        	serviceAccountKeyPairBuilder.encryptedHashedKeyPassphrase(encryptedHashedKeyPassphrase);
        }

        return serviceAccountKeyPairBuilder.build();
    }*/

    public abstract RestServiceAccountKeyPair convertToRestKeyPair(ServiceAccountKeyPair keyPair);

    public abstract KeyPair convertFromRestKeyPair(RestKeyPair restKeyPair);

    public abstract RestKeyPair convertToRestKeyPair(KeyPair keyPair);

    @Mapping(source = "keyId", target = "keyId")
    @Mapping(source = "publicKey", target = "publicKey")
    @Mapping(source = "algorithm", target = "algorithm")
    public abstract RestPublicKey convertToRestPublicKey(KeyPair keyPair);
    
    @Mapping(source = "keyId", target = "keyId")
    @Mapping(source = "publicKey", target = "publicKey")
    public abstract PublicKey convertFromRestPublicKey(RestPublicKey publicKey);

    @Named("encryptHashedKeyPassphrase")
    public String encryptHashedKeyPassphrase(String hashedKeyPassphrase) {
        return passwordEncoder.encode(hashedKeyPassphrase);
    }
}
