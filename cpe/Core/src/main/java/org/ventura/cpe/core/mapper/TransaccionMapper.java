package org.ventura.cpe.core.mapper;

import org.mapstruct.Mapper;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.dto.TransaccionDTO;

@Mapper(componentModel = "spring", uses = {})
public interface TransaccionMapper extends EntityMapper<TransaccionDTO, Transaccion> {

}
