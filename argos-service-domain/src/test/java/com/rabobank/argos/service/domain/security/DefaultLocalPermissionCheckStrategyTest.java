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
import com.rabobank.argos.domain.hierarchy.HierarchyMode;
import com.rabobank.argos.domain.hierarchy.TreeNode;
import com.rabobank.argos.domain.permission.LocalPermissions;
import com.rabobank.argos.domain.permission.Permission;
import com.rabobank.argos.service.domain.hierarchy.HierarchyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultLocalPermissionCheckStrategyTest {

    private static final String ACCOUNT_NAME = "accountName";
    private static final String LABEL_ID = "labelId";
    private static final String PARENT_LABEL_ID = "parentLabelId";
    @Mock
    private HierarchyRepository hierarchyRepository;

    @Mock
    private LocalPermissionCheckData localPermissionCheckData;

    private DefaultLocalPermissionCheckStrategy strategy;

    @Mock
    private Account account;

    @Mock
    private TreeNode treeNode;

    @Mock
    private LocalPermissions localPermissions;

    @BeforeEach
    void setUp() {
        strategy = new DefaultLocalPermissionCheckStrategy(hierarchyRepository);
    }

    @Test
    void hasLocalPermissionOnLabel() {
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(localPermissionCheckData.getLabelId()).thenReturn(LABEL_ID);
        when(hierarchyRepository.getSubTree(LABEL_ID, HierarchyMode.NONE, 0)).thenReturn(Optional.of(treeNode));
        when(treeNode.getIdPathToRoot()).thenReturn(Collections.emptyList());
        when(account.getLocalPermissions()).thenReturn(List.of(localPermissions));
        when(localPermissions.getPermissions()).thenReturn(List.of(Permission.READ));
        when(localPermissions.getLabelId()).thenReturn(LABEL_ID);
        assertThat(strategy.hasLocalPermission(localPermissionCheckData, new HashSet<>(List.of(Permission.READ)), account), is(true));
    }

    @Test
    void hasLocalPermissionOnParentLabel() {
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(localPermissionCheckData.getLabelId()).thenReturn(LABEL_ID);
        when(hierarchyRepository.getSubTree(LABEL_ID, HierarchyMode.NONE, 0)).thenReturn(Optional.of(treeNode));
        when(treeNode.getIdPathToRoot()).thenReturn(List.of(PARENT_LABEL_ID));
        when(account.getLocalPermissions()).thenReturn(List.of(localPermissions));
        when(localPermissions.getPermissions()).thenReturn(List.of(Permission.READ));
        when(localPermissions.getLabelId()).thenReturn(PARENT_LABEL_ID);
        assertThat(strategy.hasLocalPermission(localPermissionCheckData, new HashSet<>(List.of(Permission.READ)), account), is(true));
    }

    @Test
    void hasNoLocalPermissionOnLabel() {
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(localPermissionCheckData.getLabelId()).thenReturn(LABEL_ID);
        when(hierarchyRepository.getSubTree(LABEL_ID, HierarchyMode.NONE, 0)).thenReturn(Optional.of(treeNode));
        when(treeNode.getIdPathToRoot()).thenReturn(Collections.emptyList());
        when(account.getLocalPermissions()).thenReturn(List.of(localPermissions));
        when(localPermissions.getPermissions()).thenReturn(List.of(Permission.READ));
        when(localPermissions.getLabelId()).thenReturn(LABEL_ID);
        assertThat(strategy.hasLocalPermission(localPermissionCheckData, new HashSet<>(List.of(Permission.VERIFY)), account), is(false));
    }

    @Test
    void hasNoLocalPermissionNoLabelId() {
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(localPermissionCheckData.getLabelId()).thenReturn(null);
        when(account.getLocalPermissions()).thenReturn(List.of(localPermissions));
        when(localPermissions.getLabelId()).thenReturn(LABEL_ID);
        assertThat(strategy.hasLocalPermission(localPermissionCheckData, new HashSet<>(List.of(Permission.VERIFY)), account), is(false));
    }

    @Test
    void hasLocalPermissionOnLocalPermissions() {
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(localPermissionCheckData.getLabelId()).thenReturn(LABEL_ID);
        when(hierarchyRepository.getSubTree(LABEL_ID, HierarchyMode.NONE, 0)).thenReturn(Optional.of(treeNode));
        when(treeNode.getIdPathToRoot()).thenReturn(Collections.emptyList());
        when(account.getLocalPermissions()).thenReturn(Collections.emptyList());
        assertThat(strategy.hasLocalPermission(localPermissionCheckData, new HashSet<>(List.of(Permission.READ)), account), is(false));
    }

    @Test
    void hasNoLocalPermissionOtherLocalPermissions() {
        when(account.getName()).thenReturn(ACCOUNT_NAME);
        when(localPermissionCheckData.getLabelId()).thenReturn(LABEL_ID);
        when(hierarchyRepository.getSubTree(LABEL_ID, HierarchyMode.NONE, 0)).thenReturn(Optional.of(treeNode));
        when(treeNode.getIdPathToRoot()).thenReturn(Collections.emptyList());
        when(account.getLocalPermissions()).thenReturn(List.of(localPermissions));
        when(localPermissions.getLabelId()).thenReturn("otherLabel");
        assertThat(strategy.hasLocalPermission(localPermissionCheckData, new HashSet<>(List.of(Permission.READ)), account), is(false));
    }
}