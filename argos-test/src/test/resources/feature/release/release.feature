#
# Copyright (C) 2019 - 2020 Rabobank Nederland
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

Feature: Verification

  Background:
    * call read('classpath:feature/reset.feature')
    * def defaultReleaseRequest = {releaseArtifacts: [[{uri: 'target/argos-test-0.0.1-SNAPSHOT.jar',hash: '49e73a11c5e689db448d866ce08848ac5886cac8aa31156ea4de37427aca6162'}]] }
    * def defaultSteps = [{link:'build-step-link.json', signingKey:2},{link:'test-step-link.json', signingKey:3}]
    * def defaultTestData = call read('classpath:default-test-data.js')
    * configure headers = call read('classpath:headers.js') { token: #(defaultTestData.adminToken)}
    * def defaultValidResponse =  read('classpath:testmessages/release/release-valid.json')
    * def releaseInvalidResponse = read('classpath:testmessages/release/release-invalid.json')

  Scenario: happy flow all rules and commit to audit log
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest) ,testDir: 'happy-flow',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse
    * def auditlog = call read('classpath:feature/auditlog.feature')
    * string stringResponse = auditlog.response
    And match stringResponse contains 'createRelease'
    And match stringResponse contains 'releaseArtifacts'

  Scenario: products to verify wrong hash
    * def releaseRequest = {releaseArtifacts: [[{uri: 'target/argos-test-0.0.1-SNAPSHOT.jar',hash: '0123456789012345678901234567890012345678901234567890123456789012'}]] }
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(releaseRequest) ,testDir: 'happy-flow',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: expected expected end products not matches
    * def releaseRequest = {releaseArtifacts: [[{uri: 'argos-test-0.0.1-SNAPSHOT.jar',hash: '49e73a11c5e689db448d866ce08848ac5886cac8aa31156ea4de37427aca6162'}]] }
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(releaseRequest) ,testDir: 'happy-flow',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: multi segment happy flow all rules
    * def steps = [{link:'segment-1-build-step-link.json', signingKey:2},{link:'segment-1-test-step-link.json', signingKey:2},{link:'segment-2-build-step-link.json', signingKey:3},{link:'segment-2-test-step-link.json',signingKey:3}]
    * def releaseRequest = {releaseArtifacts: [[{uri: 'target/argos-test-0.0.1-SNAPSHOT.jar',hash: '49e73a11c5e689db448d866ce08848ac5886cac8aa31156ea4de37427aca6162'}]]}
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(releaseRequest) ,testDir: 'multi-segment-happy-flow',steps:#(steps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: multi segment happy flow with three segment hop
    * def steps = [{link:'segment-1-build-step-link.json', signingKey:2},{link:'segment-1-test-step-link.json', signingKey:2},{link:'segment-2-build-step-link.json', signingKey:3},{link:'segment-2-test-step-link.json',signingKey:3},{link:'segment-3-build-step-link.json', signingKey:2},{link:'segment-3-test-step-link.json', signingKey:2}]
    * def releaseRequest = {releaseArtifacts: [[ {uri: 'target/argos-test-0.0.1-SNAPSHOT.jar',hash: '49e73a11c5e689db448d866ce08848ac5886cac8aa31156ea4de37427aca6162'}]] }
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(releaseRequest) ,testDir: 'multi-segment-happy-flow-with-three-segment-hop',steps:#(steps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: multi segment with multiple verification context
    * def steps = [{link:'segment-1-build-step-link.json', signingKey:2},{link:'segment-1-test-step-link.json', signingKey:2},{link:'segment-2-build-step-link.json', signingKey:3},{link:'segment-2-build-step-link-invalid.json', signingKey:3},{link:'segment-2-test-step-link.json',signingKey:3},{link:'segment-2-test-step-link-invalid.json',signingKey:3},{link:'segment-3-build-step-link.json', signingKey:2},{link:'segment-3-test-step-link.json', signingKey:2}]
    * def releaseRequest = {releaseArtifacts: [[ {uri: 'target/argos-test-0.0.1-SNAPSHOT.jar',hash: '49e73a11c5e689db448d866ce08848ac5886cac8aa31156ea4de37427aca6162'}]] }
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(releaseRequest) ,testDir: 'multi-segment-with-multiple-verification-context',steps:#(steps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: happy flow match-rule-happy-flow
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest) ,testDir: 'match-rule-happy-flow',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: happy flow match-rule-happy-flow-with-prefix
    * def resp = call read('classpath:feature/release/release-template.feature') {releaseRequest:#(defaultReleaseRequest) ,testDir: 'match-rule-happy-flow-with-prefix',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: happy flow match-rule-no-destination-artifact
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'match-rule-no-destination-artifact',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: happy flow match-rule-no-source-artifact
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'match-rule-no-source-artifact',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: build-steps-incomplete-run
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'build-steps-incomplete-run',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: delete-rule-no-deletion
    * def resp = call read('classpath:feature/release/release-template.feature')  { releaseRequest:#(defaultReleaseRequest),testDir: 'delete-rule-no-deletion',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: create-rule-no-creation
    * def resp = call read('classpath:feature/release/release-template.feature')  { releaseRequest:#(defaultReleaseRequest),testDir: 'create-rule-no-creation',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: modify-rule-not-modified
    * def resp = call read('classpath:feature/release/release-template.feature')  { releaseRequest:#(defaultReleaseRequest),testDir: 'modify-rule-not-modified',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: require-rule-no-required-product-material
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'require-rule-no-required-product-material',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: disallow-rule-non-empty
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'disallow-rule-non-empty',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: allow-rule-no-match
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'allow-rule-no-match',steps:#(defaultSteps),layoutSigningKey:1}
    And match resp.response == releaseInvalidResponse

  Scenario: multiple-run-id-happy-flow
    * def steps = [{link:'runid1-build-step-link.json', signingKey:2},{link:'runid1-test-step-link.json', signingKey:3},{link:'runid2-build-step-link.json', signingKey:2},{link:'runid2-test-step-link.json',signingKey:3}]
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'multiple-run-id-happy-flow',steps:#(steps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: multiple-link-files-per-step-one-invalid
    * def steps = [{link:'build-step-link1.json', signingKey:2},{link:'build-step-link2.json', signingKey:2},{link:'test-step-link1.json', signingKey:2},{link:'test-step-link2.json',signingKey:2}]
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'multiple-link-files-per-step-one-invalid',steps:#(steps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse

  Scenario: multiple-verification-contexts-happy-flow
    * def steps = [{link:'build-step-link-valid.json', signingKey:2},{link:'build-step-link-invalid.json', signingKey:3},{link:'test-step-link-invalid.json', signingKey:2},{link:'test-step-link-valid.json',signingKey:3}]
    * def resp = call read('classpath:feature/release/release-template.feature') { releaseRequest:#(defaultReleaseRequest),testDir: 'multiple-verification-contexts',steps:#(steps),layoutSigningKey:1}
    And match resp.response == defaultValidResponse


  Scenario: verification without authorization should return a 401 error
    * url karate.properties['server.baseurl']
    * def supplyChain = call read('classpath:feature/supplychain/create-supplychain-with-label.feature') { supplyChainName: 'name'}
    * def supplyChainPath = '/api/supplychain/'+ supplyChain.response.id
    * configure headers = null
    Given path supplyChainPath + '/verification'
    And request defaultReleaseRequest
    When method POST
    Then status 401

  Scenario: verification without permission RELEASE should return a 403 error
    * url karate.properties['server.baseurl']
    * def supplyChain = call read('classpath:feature/supplychain/create-supplychain-with-label.feature') { supplyChainName: 'name'}
    * def supplyChainPath = '/api/supplychain/'+ supplyChain.response.id
    * def accounWithNoReleasePermission = call read('classpath:feature/account/create-personal-account.feature') {name: 'Release unauthorized person',email: 'local.noverify@extra.nogo'}
    * call read('classpath:feature/account/set-local-permissions.feature') { accountId: #(accounWithNoReleasePermission.response.id),labelId: #(supplyChain.response.parentLabelId), permissions: ["READ"]}
    * configure headers = call read('classpath:headers.js') { token: #(accounWithNoReleasePermission.response.token)}
    Given path supplyChainPath + '/release'
    And request defaultReleaseRequest
    When method POST
    Then status 403

  Scenario: SERVICE_ACCOUNT in other root label cannot verify
    * url karate.properties['server.baseurl']
    * def rootLabel = call read('classpath:feature/label/create-label.feature') { name: 'root1'}
    * def otherRootLabel = call read('classpath:feature/label/create-label.feature') { name: 'other_root_label'}
    * def personalAccount = defaultTestData.personalAccounts['default-pa1']
    * call read('classpath:feature/account/set-local-permissions.feature') {accountId: #(personalAccount.accountId), labelId: #(rootLabel.response.id), permissions: [READ, SERVICE_ACCOUNT_EDIT,TREE_EDIT]}
    * call read('classpath:feature/account/set-local-permissions.feature') {accountId: #(personalAccount.accountId), labelId: #(otherRootLabel.response.id), permissions: [READ, SERVICE_ACCOUNT_EDIT,TREE_EDIT]}
    * configure headers = call read('classpath:headers.js') { token: #(personalAccount.token)}
    * call read('classpath:feature/account/create-service-account-with-key.feature') {accountName: 'sa6', parentLabelId: #(rootLabel.response.id), keyFile: 'sa-keypair1'}
    * def otherSupplyChain = call read('classpath:feature/supplychain/create-supplychain.feature') {supplyChainName: other-supply-chain, parentLabelId: #(otherRootLabel.response.id)}
    * def keyPair = read('classpath:testmessages/key/sa-keypair1.json')
    * configure headers = call read('classpath:headers.js') { username: #(keyPair.keyId),password:#(keyPair.hashedKeyPassphrase)}
    Given path '/api/supplychain/'+ otherSupplyChain.response.id + '/release'
    And request defaultReleaseRequest
    When method POST
    Then status 403
