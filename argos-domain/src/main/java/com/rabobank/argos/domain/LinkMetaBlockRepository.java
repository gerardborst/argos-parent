package com.rabobank.argos.domain;


import com.rabobank.argos.domain.model.LinkMetaBlock;

import java.util.List;

public interface LinkMetaBlockRepository {
    List<LinkMetaBlock> findBySupplyChainId(String supplyChainId);

    List<LinkMetaBlock> findBySupplyChainAndSha(String supplyChainId, String hash);

    void save(LinkMetaBlock link);
}
