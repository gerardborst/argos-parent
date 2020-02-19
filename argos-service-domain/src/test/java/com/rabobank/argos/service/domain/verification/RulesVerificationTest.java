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
package com.rabobank.argos.service.domain.verification;

import com.rabobank.argos.domain.layout.LayoutSegment;
import com.rabobank.argos.domain.layout.Step;
import com.rabobank.argos.domain.layout.rule.Rule;
import com.rabobank.argos.domain.layout.rule.RuleType;
import com.rabobank.argos.domain.link.Artifact;
import com.rabobank.argos.domain.link.Link;
import com.rabobank.argos.domain.link.LinkMetaBlock;
import com.rabobank.argos.service.domain.verification.RulesVerification;
import com.rabobank.argos.service.domain.verification.VerificationContext;
import com.rabobank.argos.service.domain.verification.rules.RuleVerification;
import com.rabobank.argos.service.domain.verification.rules.RuleVerificationContext;
import com.rabobank.argos.service.domain.verification.rules.RuleVerificationResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.rabobank.argos.service.domain.verification.Verification.Priority.RULES;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RulesVerificationTest {

    private static final String STEP_NAME = "stepName";
    private static final String SEGMENT_NAME = "segmentName";
    @Mock
    private RuleVerification ruleVerification;

    @Mock
    private RulesVerification verification;

    @Mock
    private VerificationContext verificationContext;

    @Mock
    private Step step;

    @Mock
    private LinkMetaBlock linkMetaBlock;

    @Mock
    private LayoutSegment layoutSegment;

    @Mock
    private Rule expectedMaterialRule;

    @Mock
    private Rule expectedProductRule;

    @Mock
    private Link link;

    @Mock
    private RuleVerificationResult productRuleVerificationResult;

    @Mock
    private RuleVerificationResult materialRuleVerificationResult;

    @Mock
    private Artifact materialArtifact;

    @Mock
    private Artifact productArtifact;

    @Captor
    private ArgumentCaptor<RuleVerificationContext<?>> ruleVerificationContextArgumentCaptor;

    @BeforeEach
    void setUp() {
        verification = new RulesVerification(List.of(ruleVerification));
    }

    @Test
    void getPriority() {
        assertThat(verification.getPriority(), is(RULES));
    }

    @Test
    void verifyHappyFlow() {
        when(ruleVerification.verifyExpectedProducts(any(RuleVerificationContext.class))).thenReturn(productRuleVerificationResult);
        setupMocks();

        when(link.getMaterials()).thenReturn(List.of(materialArtifact));
        when(link.getProducts()).thenReturn(List.of(productArtifact));

        when(materialRuleVerificationResult.isValid()).thenReturn(true);
        when(productRuleVerificationResult.isValid()).thenReturn(true);

        when(materialRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(materialArtifact));
        when(productRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(productArtifact));

        assertThat(verification.verify(verificationContext).isRunIsValid(), is(true));

        verify(verificationContext).removeLinkMetaBlocks(Collections.emptyList());

        verify(ruleVerification).verifyExpectedProducts(ruleVerificationContextArgumentCaptor.capture());
        RuleVerificationContext<?> ruleVerificationContext = ruleVerificationContextArgumentCaptor.getValue();
        assertThat(ruleVerificationContext.getMaterials(), empty());
        assertThat(ruleVerificationContext.getProducts(), empty());
        assertThat(ruleVerificationContext.getRule(), sameInstance(expectedProductRule));
        assertThat(ruleVerificationContext.getVerificationContext(), sameInstance(verificationContext));
    }

    @Test
    void verifyMaterialRuleFailed() {
        when(ruleVerification.verifyExpectedProducts(any(RuleVerificationContext.class))).thenReturn(productRuleVerificationResult);
        setupMocks();

        when(materialRuleVerificationResult.isValid()).thenReturn(false);
        when(productRuleVerificationResult.isValid()).thenReturn(true);

        when(materialRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(materialArtifact));
        when(productRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(productArtifact));

        assertThat(verification.verify(verificationContext).isRunIsValid(), is(true));
        verify(verificationContext).removeLinkMetaBlocks(List.of(linkMetaBlock));

    }

    @Test
    void verifyProductRuleFailed() {
        when(ruleVerification.verifyExpectedProducts(any(RuleVerificationContext.class))).thenReturn(productRuleVerificationResult);
        setupMocks();

        when(materialRuleVerificationResult.isValid()).thenReturn(true);
        when(productRuleVerificationResult.isValid()).thenReturn(false);

        when(materialRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(materialArtifact));
        when(productRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(productArtifact));

        assertThat(verification.verify(verificationContext).isRunIsValid(), is(true));
        verify(verificationContext).removeLinkMetaBlocks(List.of(linkMetaBlock));

    }

    @Test
    void verifyNotAllProductArtifactsChecked() {
        when(ruleVerification.verifyExpectedProducts(any(RuleVerificationContext.class))).thenReturn(productRuleVerificationResult);
        setupMocks();

        when(materialRuleVerificationResult.isValid()).thenReturn(true);
        when(productRuleVerificationResult.isValid()).thenReturn(true);

        when(link.getMaterials()).thenReturn(List.of(materialArtifact));
        when(link.getProducts()).thenReturn(List.of(productArtifact));

        when(materialRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(materialArtifact));
        when(productRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of());

        assertThat(verification.verify(verificationContext).isRunIsValid(), is(true));
        verify(verificationContext).removeLinkMetaBlocks(List.of(linkMetaBlock));

    }

    @Test
    void verifyNotAllMaterialArtifactsChecked() {
        when(ruleVerification.verifyExpectedProducts(any(RuleVerificationContext.class))).thenReturn(productRuleVerificationResult);
        setupMocks();

        when(materialRuleVerificationResult.isValid()).thenReturn(true);
        when(productRuleVerificationResult.isValid()).thenReturn(true);

        when(link.getMaterials()).thenReturn(List.of(materialArtifact));
        when(link.getProducts()).thenReturn(List.of(productArtifact));

        when(materialRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of());
        when(productRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(productArtifact));

        assertThat(verification.verify(verificationContext).isRunIsValid(), is(true));
        verify(verificationContext).removeLinkMetaBlocks(List.of(linkMetaBlock));

    }

    @Test
    void verifyNotImplementedRule() {
        setupMocks();

        when(expectedProductRule.getRuleType()).thenReturn(RuleType.DELETE);
        when(materialRuleVerificationResult.isValid()).thenReturn(true);


        when(materialRuleVerificationResult.getValidatedArtifacts()).thenReturn(Set.of(materialArtifact));

        assertThat(verification.verify(verificationContext).isRunIsValid(), is(true));
        verify(verificationContext).removeLinkMetaBlocks(List.of(linkMetaBlock));

    }

    private void setupMocks() {
        when(ruleVerification.getRuleType()).thenReturn(RuleType.ALLOW);
        verification.init();
        when(verificationContext.layoutSegments()).thenReturn(singletonList(layoutSegment));
        when(layoutSegment.getName()).thenReturn(SEGMENT_NAME);
        when(verificationContext.getExpectedStepNamesBySegmentName(SEGMENT_NAME)).thenReturn(singletonList(STEP_NAME));
        when(verificationContext.getStepBySegmentNameAndStepName(SEGMENT_NAME, STEP_NAME)).thenReturn(step);
        when(linkMetaBlock.getLink()).thenReturn(link);
        when(verificationContext.getLinksBySegmentNameAndStepName(SEGMENT_NAME, STEP_NAME)).thenReturn(List.of(linkMetaBlock));
        when(step.getStepName()).thenReturn(STEP_NAME);
        when(expectedMaterialRule.getRuleType()).thenReturn(RuleType.ALLOW);
        when(step.getExpectedMaterials()).thenReturn(List.of(expectedMaterialRule));

        when(expectedProductRule.getRuleType()).thenReturn(RuleType.ALLOW);
        when(step.getExpectedProducts()).thenReturn(List.of(expectedProductRule));

        when(ruleVerification.verifyExpectedMaterials(any(RuleVerificationContext.class))).thenReturn(materialRuleVerificationResult);


    }
}