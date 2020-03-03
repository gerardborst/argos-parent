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
package com.rabobank.argos.service.domain.security;

import com.rabobank.argos.domain.account.Account;
import com.rabobank.argos.domain.account.PersonalAccount;
import com.rabobank.argos.domain.permission.Role;
import com.rabobank.argos.service.domain.permission.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PermissionCheckAdvisor {

    private final AccountSecurityContext accountSecurityContext;

    private final ApplicationContext applicationContext;

    private final RoleRepository roleRepository;

    @Pointcut("@annotation(permissionCheck)")
    public void permissionCheckPointCut(PermissionCheck permissionCheck) {
        //This is an AspectJ pointcut implemented as method
    }

    @Before(value = "permissionCheckPointCut(permissionCheck)", argNames = "joinPoint,permissionCheck")
    public void checkPermissions(JoinPoint joinPoint, PermissionCheck permissionCheck) {
        Account account = accountSecurityContext.getAuthenticatedAccount().orElseThrow();

        log.info("checking permissions of method:{} for account: {} with permissions: {}",
                joinPoint.getSignature().getName(),
                account.getName(),
                permissionCheck.permissions()
        );


        if (!(hasGlobalPermissions(permissionCheck, account) || hasLocalPermissions(joinPoint, permissionCheck, account))) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private boolean hasGlobalPermissions(PermissionCheck permissionCheck, Account account) {
        if (account instanceof PersonalAccount) {
            PersonalAccount personalAccount = (PersonalAccount) account;
            if (!personalAccount.getRoleIds().isEmpty()) {
                return roleRepository.findByIds(personalAccount.getRoleIds()).stream().map(Role::getPermissions).flatMap(List::stream).collect(Collectors.toSet())
                        .containsAll(List.of(permissionCheck.permissions()));
            }
        }
        return false;
    }

    private boolean hasLocalPermissions(JoinPoint joinPoint, PermissionCheck permissionCheck, Account account) {

        LocalPermissionCheckDataExtractor localPermissionCheckDataExtractor = applicationContext
                .getBean(permissionCheck.localPermissionDataExtractorBean(), LocalPermissionCheckDataExtractor.class);
        LocalPermissionCheckStrategy localPermissionCheckStrategy = applicationContext.getBean(permissionCheck.localPermissionCheckStrategyBean(), LocalPermissionCheckStrategy.class);

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] argumentValues = joinPoint.getArgs();
        LocalPermissionCheckData labelCheckData = localPermissionCheckDataExtractor.extractLocalPermissionCheckData(method, argumentValues);
        return localPermissionCheckStrategy.hasLocalPermission(labelCheckData, new HashSet<>(List.of(permissionCheck.permissions())), account);
    }
}
