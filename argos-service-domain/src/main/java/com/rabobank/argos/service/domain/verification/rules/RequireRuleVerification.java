/*
 * Copyright (C) 2019 Rabobank Nederland
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
package com.rabobank.argos.service.domain.verification.rules;

/*-
 * #%L
 * Argos Supply Chain Notary
 * %%
 * Copyright (C) 2019 Rabobank Nederland
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.rabobank.argos.domain.layout.rule.Rule;
import com.rabobank.argos.domain.layout.rule.RuleType;
import com.rabobank.argos.domain.link.Artifact;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RequireRuleVerification implements RuleVerification {
    @Override
    public RuleType getRuleType() {
        return RuleType.REQUIRE;
    }

    @Override
    public RuleVerificationResult verifyExpectedProducts(RuleVerificationContext<? extends Rule> context) {
        return verify(context.getFilteredProducts());
    }

    @Override
    public RuleVerificationResult verifyExpectedMaterials(RuleVerificationContext<? extends Rule> context) {
        return verify(context.getFilteredMaterials());
    }

    private RuleVerificationResult verify(Stream<Artifact> filteredArtifacts) {
        Set<Artifact> artifacts = filteredArtifacts.collect(Collectors.toSet());
        if (!artifacts.isEmpty()) {
            return RuleVerificationResult.okay(artifacts);
        } else {
            return RuleVerificationResult.notOkay();
        }
    }


}
