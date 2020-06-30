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

import com.rabobank.argos.service.adapter.in.rest.api.handler.SearchAccountApi;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestAccountKeyInfo;
import com.rabobank.argos.service.domain.account.AccountInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class SearchAccountRestservice implements SearchAccountApi {

    private final AccountInfoRepository accountInfoRepository;

    private final AccountKeyInfoMapper accountKeyInfoMapper;

    @Override
    public ResponseEntity<List<RestAccountKeyInfo>> searchKeysFromAccount(String supplyChainId, @Valid List<String> keyIds) {
        log.info(keyIds.toString());
        List<RestAccountKeyInfo> restAccountKeyInfos = accountInfoRepository.findByKeyIds(keyIds)
                .stream()
                .map(accountKeyInfoMapper::convertToRestAccountInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(restAccountKeyInfos);
    }
}
