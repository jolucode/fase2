package org.ventura.cpe.core.ubl.generator;

import org.ventura.cpe.core.domain.Transaccion;

import java.nio.file.Path;

public interface UBLGenerator {

    Path generateInvoiceType(Transaccion transaction);
}
