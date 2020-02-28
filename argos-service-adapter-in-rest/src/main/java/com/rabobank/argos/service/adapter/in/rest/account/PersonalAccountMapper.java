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

import com.rabobank.argos.domain.account.PersonalAccount;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestPersonalAccount;
import com.rabobank.argos.service.adapter.in.rest.api.model.RestRole;
import com.rabobank.argos.service.adapter.in.rest.permission.RoleMapper;
import com.rabobank.argos.service.domain.permission.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class PersonalAccountMapper {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Mappings({
            @Mapping(target = "id", source = "accountId"),
            @Mapping(target = "roles", source = "roleIds", qualifiedByName = "convertToRestRoles")
    })
    public abstract RestPersonalAccount convertToRestPersonalAccount(PersonalAccount personalAccount);

    @Named("convertToRestRoles")
    public List<RestRole> convertToRestRoles(List<String> roleIds) {
        return roleRepository.findByIds(roleIds).stream().map(roleMapper::convertToRestRole).collect(Collectors.toList());
    }

    @Mapping(target = "id", source = "accountId")
    public abstract RestPersonalAccount convertToRestPersonalAccountWithoutRoles(PersonalAccount personalAccount);
}
