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
package com.rabobank.argos.service.adapter.out.mongodb.release;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

import com.rabobank.argos.domain.release.ReleaseDossierMetaData;


import static com.rabobank.argos.service.adapter.out.mongodb.release.ReleaseRepositoryImpl.*;

public class DocumentToReleaseDossierMetaDataConverter implements Converter<Document, ReleaseDossierMetaData>{

    @Override
    public ReleaseDossierMetaData convert(Document source) {
        Document metaData = (Document) source.get(METADATA_FIELD);
        List<Document> releaseArtifactsList = metaData
                .getList(RELEASE_ARTIFACTS_FIELD, Document.class,
                        Collections.emptyList());
        return ReleaseDossierMetaData
                .builder()
                .documentId(((ObjectId)source.get(ID_FIELD))
                        .toHexString())
                .releaseArtifacts(convertToReleaseArtifacts(releaseArtifactsList))
                .releaseDate(new DateToOffsetTimeConverter().convert(metaData.getDate(RELEASE_DATE_FIELD)))
                .supplyChainPath(metaData.getString(SUPPLY_CHAIN_PATH_FIELD))
                .build();
    }

    private static List<List<String>> convertToReleaseArtifacts(List<Document> releaseArtifacts) {
        return releaseArtifacts.stream()
                .map(d -> (List<String>) d.get(HASHES))
                .collect(Collectors.toList());
    }

}
