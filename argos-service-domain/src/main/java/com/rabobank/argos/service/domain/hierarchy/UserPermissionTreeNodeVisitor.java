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
package com.rabobank.argos.service.domain.hierarchy;

import com.rabobank.argos.domain.hierarchy.TreeNode;
import com.rabobank.argos.domain.hierarchy.TreeNodeVisitor;
import com.rabobank.argos.domain.permission.Permission;
import com.rabobank.argos.service.domain.security.AccountSecurityContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserPermissionTreeNodeVisitor implements TreeNodeVisitor<Optional<TreeNode>> {

    private TreeNode treeNodeWithUserPermissions;
    private HashMap<String, TreeNode> parentRegistry = new HashMap<>();
    private final AccountSecurityContext accountSecurityContext;

    UserPermissionTreeNodeVisitor(final AccountSecurityContext accountSecurityContext) {
        this.accountSecurityContext = accountSecurityContext;
    }

    @Override
    public boolean visitEnter(TreeNode treeNode) {

        TreeNode copyOfTreeNode = treeNode
                .withChildren(new ArrayList<>())
                .withUserPermissions(determineAggregatedPermissions(treeNode));

        if (copyOfTreeNode.getUserPermissions().isEmpty()) {
            return false;
        }

        if (treeNodeWithUserPermissions == null) {
            treeNodeWithUserPermissions = copyOfTreeNode;
        } else {
            TreeNode parent = parentRegistry.get(copyOfTreeNode.getParentLabelId());
            parent.addChild(copyOfTreeNode);
        }

        parentRegistry.put(copyOfTreeNode.getReferenceId(), copyOfTreeNode);
        return true;
    }

    private List<Permission> determineAggregatedPermissions(TreeNode treeNode) {
        Set<Permission> aggregatedPermissions = new HashSet<>();
        List<String> labelIdsUpTree = new ArrayList<>(treeNode.getIdPathToRoot());
        if (!treeNode.isLeafNode()) {
            labelIdsUpTree.add(treeNode.getReferenceId());
        }
        aggregatedPermissions.addAll(accountSecurityContext.allLocalPermissions(labelIdsUpTree));
        aggregatedPermissions.addAll(accountSecurityContext.getGlobalPermission());
        List<Permission> arrayOfAggregatedPermissions = new ArrayList<>(aggregatedPermissions);
        arrayOfAggregatedPermissions.sort(Comparator.comparing(Permission::name));
        return arrayOfAggregatedPermissions;
    }

    @Override
    public boolean visitExit(TreeNode treeNode) {
        return true;
    }

    @Override
    public boolean visitLeaf(TreeNode treeNode) {
        TreeNode copyOfTreeNode = treeNode.withUserPermissions(determineAggregatedPermissions(treeNode));

        if (copyOfTreeNode.getUserPermissions().isEmpty()) {
            return false;
        }
        TreeNode parent = parentRegistry.get(copyOfTreeNode.getParentLabelId());
        parent.addChild(copyOfTreeNode);
        return true;
    }

    @Override
    public Optional<TreeNode> result() {
        return Optional.ofNullable(treeNodeWithUserPermissions);
    }
}
