package org.ventura.cpe.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.domain.PublicardocWs;
import org.ventura.cpe.core.repository.PublicardocRepository;

@Service
@RequiredArgsConstructor
public class PublicadocService {

    private final PublicardocRepository publicardocRepository;

    public void createPublicadoc(PublicardocWs publicarDoc) {
        publicardocRepository.saveAndFlush(publicarDoc);
    }
}
