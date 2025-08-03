package ventura.soluciones.commons.exception.error;

import ventura.soluciones.commons.config.IUBLConfig;

/**
 * Esta interfaz contiene todos los codigos de errores presentados en el Sistema
 * de HOMOLOGACION.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public interface IVenturaError {

    /**
     * Stados GENERALES
     * <p>
     * Disponible: 0 - 10
     */
    public ErrorObj ERROR_0 = new ErrorObj(0, "Success");
    public ErrorObj ERROR_2 = new ErrorObj(2, "Error");

    /**
     * Errores Generales
     * <p>
     * Disponible: 11 - 100
     */
    public ErrorObj ERROR_11 = new ErrorObj(11,
            "[GENERAL] Ocurrio un error mientras se convertia un FILE a un objeto JAXB.");
    public ErrorObj ERROR_12 = new ErrorObj(
            12,
            "[GENERAL] Ocurrio un problema al obtener el numero de errores de inicio de sesion.");
    public ErrorObj ERROR_13 = new ErrorObj(
            13,
            "[GENERAL] Ocurrio un error con el archivo de configuracion de la aplicacion. <sunat_config>");
    public ErrorObj ERROR_14 = new ErrorObj(14,
            "[GENERAL] La ruta especificada no contiene un archivo.");
    public ErrorObj ERROR_15 = new ErrorObj(15,
            "[GENERAL] Hubo un problema al convertir el archivo en bytes.");

    /**
     * Seccion: Hibernate
     * <p>
     * Disponible: 101 - 150
     */
    public ErrorObj ERROR_101 = new ErrorObj(101,
            "[HOMOLOGATION-DB] Hibernate sesion no puede abrirse.");
    public ErrorObj ERROR_102 = new ErrorObj(102,
            "[HOMOLOGATION-DB] Hibernate sesion is nulo o cerrado.");
    public ErrorObj ERROR_103 = new ErrorObj(
            103,
            "[HOMOLOGATION-DB] Hubo un problema al procesar la transaccion. La transaccion no fue registrada.");

    /**
     * Seccion: Sunat
     * <p>
     * Disponible: 151 - 200
     */
    public ErrorObj ERROR_151 = new ErrorObj(
            151,
            "[SUNAT-WS] Hubo un problema al intentar crear un cliente de servicio web de Sunat. El nombre del cliente no existe.");
    public ErrorObj ERROR_152 = new ErrorObj(152,
            "[SUNAT-WS] Ocurrio un problema al intentar conectarse con SUNAT.");

    public ErrorObj ERROR_153 = new ErrorObj(153,
            "[SUNAT-WS] Hubo un problema en el proceso de envio al SERVICIO WEB de Sunat.");

    public ErrorObj ERROR_154 = new ErrorObj(154,
            "[SUNAT-WS-CONSULT] Ocurrio un problema al intentar conectarse con SUNAT.");
    public ErrorObj ERROR_155 = new ErrorObj(
            155,
            "[SUNAT-WS-CONSULT] Hubo un problema en el proceso del SERVICIO WEB de consulta de Sunat.");
    public ErrorObj ERROR_156 = new ErrorObj(156,
            "[SUNAT-WS] Hubo un problema de conexion. TIMEOUT");


    /**
     * Seccion: Homologation
     * <p>
     * Disponible: 201 - 250
     */
    public ErrorObj ERROR_201 = new ErrorObj(
            201,
            "[HOMOLOGATION] El sistema retorno una lista de departamentos vacia. Verificar en la Base de Datos.");
    public ErrorObj ERROR_202 = new ErrorObj(
            202,
            "[HOMOLOGATION] El sistema retorno una lista de casos de prueba vacia. Verificar en la Base de Datos.");
    public ErrorObj ERROR_203 = new ErrorObj(
            203,
            "[HOMOLOGATION] Hubo un problema al extraer el PK del JTable, PK es nulo o vacio.");
    public ErrorObj ERROR_204 = new ErrorObj(
            204,
            "[HOMOLOGATION] La actualizacion del estado de la ficha de contribuyente no fue exitosa.");
    public ErrorObj ERROR_205 = new ErrorObj(
            205,
            "[HOMOLOGATION] Hubo un problema al extraer la lista de fichas de contribuyentes a ser homologadas.");
    public ErrorObj ERROR_206 = new ErrorObj(
            206,
            "[HOMOLOGATION] No se puede iniciar el Proceso de Homologacion porque no existen Fichas de contribuyentes para homologar.");
    public ErrorObj ERROR_207 = new ErrorObj(
            207,
            "[HOMOLOGATION] No es posible realizar el proceso de TESTEO. No existen Fichas de Contribuyentes.");
    public ErrorObj ERROR_208 = new ErrorObj(
            208,
            "[HOMOLOGATION] No es posible iniciar el proceso de HOMOLOGACION en hilos. No existen registros de Homologaciones.");
    public ErrorObj ERROR_209 = new ErrorObj(
            209,
            "[HOMOLOGATION] Ocurrio un problema al crear el directorio para los documentos del contribuyente.");
    public ErrorObj ERROR_210 = new ErrorObj(
            210,
            "[HOMOLOGATION] No se encontraron casos de prueba para el registro de Homologacion.");
    public ErrorObj ERROR_211 = new ErrorObj(211,
            "[HOMOLOGATION] No se pudo generar el nombre del documento UBL.");
    public ErrorObj ERROR_212 = new ErrorObj(212,
            "[HOMOLOGATION] Ocurrio un error al convertir el documento UBL al formato ZIP.");
    public ErrorObj ERROR_213 = new ErrorObj(
            213,
            "[HOMOLOGATION] GRAVE Ocurrio un error en el sistema, no se ha retornado una constancia de recepcion (CDR).");
    public ErrorObj ERROR_214 = new ErrorObj(214,
            "[HOMOLOGATION] Ocurrio un problema al intentar conectarse con SUNAT.");
    public ErrorObj ERROR_215 = new ErrorObj(
            215,
            "[HOMOLOGATION] No se puede procesar el caso de prueba ACTUAL porque el caso de prueba relacionado no fue EXITOSO.");
    public ErrorObj ERROR_216 = new ErrorObj(
            216,
            "[HOMOLOGATION] Ocurrio un error al extraer los numeros de documentos para el RESUMEN DIARIO.");
    public ErrorObj ERROR_217 = new ErrorObj(
            217,
            "[HOMOLOGATION] Ocurrio un error al extraer el ARRAY de error de Sunat (descripcion, tipo).");
    public ErrorObj ERROR_218 = new ErrorObj(
            218,
            "[HOMOLOGATION] Hubo un problema en el proceso de envio al SERVICIO WEB de Sunat.");
    public ErrorObj ERROR_219 = new ErrorObj(219,
            "[HOMOLOGATION] Ocurrio un problema al extraer el TICKET, estaba vacio o nulo.");
    public ErrorObj ERROR_220 = new ErrorObj(220,
            "[HOMOLOGATION] El caso de prueba fue rechazado por SUNAT.");
    public ErrorObj ERROR_221 = new ErrorObj(221,
            "[HOMOLOGATION] Proceso de HOMOLOGACION culminado correctamente.");
    public ErrorObj ERROR_222 = new ErrorObj(222,
            "[HOMOLOGATION] El proceso de HOMOLOGACION no culmino correctamente.");
    public ErrorObj ERROR_223 = new ErrorObj(
            223,
            "[HOMOLOGATION] El estado de la HOMOLOGACION ya fue actualizada al estado USER_ERROR.");
    public ErrorObj ERROR_224 = new ErrorObj(
            224,
            "[HOMOLOGATION] Hubo un problema al extraer la Ficha de Homologacion de la Base de Datos.");
    public ErrorObj ERROR_225 = new ErrorObj(
            225,
            "[HOMOLOGATION] Hubo un problema a nivel de los HILOS en el restablecimiento del proceso de Homologacion.");
    public ErrorObj ERROR_226 = new ErrorObj(
            226,
            "[HOMOLOGATION] El objeto ERROR de respuesta del proceso de PRUEBA de Homologacion es nulo.");
    public ErrorObj ERROR_227 = new ErrorObj(227,
            "[HOMOLOGATION] El documento ZIP a enviar es nulo.");

    /**
     * Seccion: Signer-documents
     * <p>
     * Disponible: 251 - 300
     */
    public ErrorObj ERROR_251 = new ErrorObj(251,
            "[SIGNER-DOCUMENTS] Ocurrio un problema al convertir el certificado a bytes.");
    public ErrorObj ERROR_252 = new ErrorObj(252,
            "[SIGNER-DOCUMENTS] Ocurrio un error al extraer el ALIAS del certificado.");
    public ErrorObj ERROR_253 = new ErrorObj(253,
            "[SIGNER-DOCUMENTS] Ocurrio un error al cargar el certificado digital.");
    public ErrorObj ERROR_254 = new ErrorObj(
            254,
            "[SIGNER-DOCUMENTS] La ruta del documento UBL ha firmar, no contiene un archivo.");
    public ErrorObj ERROR_255 = new ErrorObj(255,
            "[SIGNER-DOCUMENTS] Ocurrio un problema al intentar firmar el documento UBL.");
    public ErrorObj ERROR_256 = new ErrorObj(
            256,
            "[SIGNER-DOCUMENTS] Ocurrio un problema al buscar la posicion del TAG a firmar del documento UBL.");
    public ErrorObj ERROR_257 = new ErrorObj(
            257,
            "[SIGNER-DOCUMENTS] La verificacion del certificado digital a retornado errores. No es posible firmar.");
    public ErrorObj ERROR_258 = new ErrorObj(
            258,
            "[SIGNER-DOCUMENTS] La validacion del certificado digital retorno error. Verifique la contrasenia del certificado.");
    public ErrorObj ERROR_259 = new ErrorObj(
            259,
            "[SIGNER-DOCUMENTS] El certificado digital es nulo. Verifique la ruta en DISCO.");

    /**
     * Seccion: Documentos UBL
     * <p>
     * Disponible: 301 - 400
     */
    public ErrorObj ERROR_301 = new ErrorObj(301,
            "[UBL-DOCUMENT] Ocurrio un problema al generar el objeto SignatureType.");
    public ErrorObj ERROR_302 = new ErrorObj(302,
            "[UBL-DOCUMENT] Ocurrio un problema al generar el objeto SupplierPartyType.");
    public ErrorObj ERROR_303 = new ErrorObj(303,
            "[UBL-DOCUMENT] Ocurrio un problema al generar el objeto CustomerPartyType.");
    public ErrorObj ERROR_304 = new ErrorObj(
            304,
            "[UBL-DOCUMENT] Ocurrio un problema al generar el documento UBL de la ruta del TEMPLATE.");
    public ErrorObj ERROR_305 = new ErrorObj(
            305,
            "[UBL-DOCUMENT] El valor del objeto InvoiceTypeCodeType referente al TEMPLATE no es valido.");
    public ErrorObj ERROR_306 = new ErrorObj(306,
            "[UBL-DOCUMENT] Ocurrio un problema al guardar el documento UBL en DISCO.");
    public ErrorObj ERROR_307 = new ErrorObj(
            307,
            "[UBL-DOCUMENT] Ocurrio un problema al colocar la informacion del contribuyente en el TEMPLATE.");
    public ErrorObj ERROR_308 = new ErrorObj(
            308,
            "[UBL-DOCUMENT] El valor del objeto DocumentReferenceType referente a una NOTA DE CREDITO o NOTA DE DEBITO del TEMPLATE no es valido.");
    public ErrorObj ERROR_309 = new ErrorObj(309,
            "[UBL-DOCUMENT] El tipo de documento UBL ingresado no es valido.");
    public ErrorObj ERROR_310 = new ErrorObj(
            310,
            "[UBL-DOCUMENT] No es posible generar el RESUMEN DIARIO porque sus casos de pruebas referentes no estan en estado SUCCESS.");
    public ErrorObj ERROR_311 = new ErrorObj(
            311,
            "[UBL-DOCUMENT] No es posible generar la COMUNICACION DE BAJA porque sus casos de pruebas referentes no estan en estado SUCCESS.");
    public ErrorObj ERROR_312 = new ErrorObj(312,
            "[UBL-DOCUMENT] La fecha de emision es nula.");
    public ErrorObj ERROR_313 = new ErrorObj(313,
            "[UBL-DOCUMENT] Hubo un problema con la fecha de emision.");
    public ErrorObj ERROR_314 = new ErrorObj(314,
            "[UBL-DOCUMENT] La fecha de vencimiento es nula.");
    public ErrorObj ERROR_315 = new ErrorObj(315,
            "[UBL-DOCUMENT] Hubo un problema con la fecha de vencimiento.");
    public ErrorObj ERROR_316 = new ErrorObj(316,
            "[UBL-DOCUMENT] No se encontraron impuestos en la transaccion.");
    public ErrorObj ERROR_317 = new ErrorObj(317,
            "[UBL-DOCUMENT] Hubo un problema al extraer los impuestos de la transaccion.");
    public ErrorObj ERROR_318 = new ErrorObj(318,
            "[UBL-DOCUMENT] El tipo de impuesto ingresado no es valido.");
    public ErrorObj ERROR_319 = new ErrorObj(319,
            "[UBL-DOCUMENT] No se encontraron items en la transaccion.");
    public ErrorObj ERROR_320 = new ErrorObj(320,
            "[UBL-DOCUMENT] Hubo un problema al extraer los items de la transaccion.");
    public ErrorObj ERROR_321 = new ErrorObj(321,
            "[UBL-DOCUMENT] No se encontro un IMPORTE TOTAL en la transaccion.");
    public ErrorObj ERROR_322 = new ErrorObj(
            322,
            "[UBL-DOCUMENT] No se encontraron impuestos en uno de los items de la transaccion.");
    public ErrorObj ERROR_323 = new ErrorObj(
            323,
            "[UBL-DOCUMENT] Hubo un problema al extraer los impuestos en uno de los items de la transaccion.");
    public ErrorObj ERROR_324 = new ErrorObj(324,
            "[UBL-DOCUMENT] El tipo de impuesto de item ingresado no es valido.");
    public ErrorObj ERROR_325 = new ErrorObj(
            325,
            "[UBL-DOCUMENT] Hubo un problema al extraer la descripcion de uno de los items de la transaccion.");
    public ErrorObj ERROR_326 = new ErrorObj(
            326,
            "[UBL-DOCUMENT] Hubo un problema al extraer la DESCRIPCION o el CODIGO DE ARTICULO de uno de los items de la transaccion.");
    public ErrorObj ERROR_327 = new ErrorObj(
            327,
            "[UBL-DOCUMENT] Hubo un problema al extraer el DESCUENTO DE LINEA de un item de la transaccion.");
    public ErrorObj ERROR_328 = new ErrorObj(
            328,
            "[UBL-DOCUMENT] Hubo un problema al generar el UBLExtension de TOTALES y PROPIEDADES.");
    public ErrorObj ERROR_329 = new ErrorObj(
            329,
            "[UBL-DOCUMENT] Hubo un problema al generar el NODO con la informacion de TOTALES y PROPIEDADES.");
    public ErrorObj ERROR_330 = new ErrorObj(
            330,
            "[UBL-DOCUMENT] Ocurrio un problema al extraer la informacion de TOTALES. Lista nula o vacia.");
    public ErrorObj ERROR_331 = new ErrorObj(
            331,
            "[UBL-DOCUMENT] Ocurrio un problema al extraer la informacion de PROPIEDADES. Lista nula o vacia.");
    public ErrorObj ERROR_332 = new ErrorObj(
            332,
            "[UBL-DOCUMENT] Ocurrio un problema al extraer el VALOR UNITARIO de un item en la transaccion.");
    public ErrorObj ERROR_333 = new ErrorObj(
            333,
            "[UBL-DOCUMENT] No se encontro una lista de BillReference de un item para extraer el VALOR UNITARIO.");
    public ErrorObj ERROR_334 = new ErrorObj(
            334,
            "[UBL-DOCUMENT] No se encontro un VALOR UNITARIO para un item de la transaccion.");
    public ErrorObj ERROR_335 = new ErrorObj(335,
            "[UBL-DOCUMENT] No se encontro un IMPORTE en la transaccion.");
    public ErrorObj ERROR_336 = new ErrorObj(336,
            "[UBL-DOCUMENT] Ocurrio un problema al extraer la informacion del ANTICIPO.");
    public ErrorObj ERROR_337 = new ErrorObj(337,
            "[UBL-DOCUMENT] Ocurrio un problema al generar el objeto DiscrepancyResponse.");
    public ErrorObj ERROR_338 = new ErrorObj(338,
            "[UBL-DOCUMENT] Ocurrio un problema al generar el objeto BillingReference.");
    public ErrorObj ERROR_339 = new ErrorObj(
            339,
            "[UBL-DOCUMENT] No es posible generar el objeto AccountingCustomerParty porque la serie del documento no es valida.");
    public ErrorObj ERROR_340 = new ErrorObj(340,
            "[UBL-DOCUMENT] La fecha de referencia es nula.");
    public ErrorObj ERROR_341 = new ErrorObj(
            341,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto InvoiceType de la FACTURA.");
    public ErrorObj ERROR_342 = new ErrorObj(
            342,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto InvoiceType de la BOLETA DE VENTA.");
    public ErrorObj ERROR_343 = new ErrorObj(
            343,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto CreditNoteType.");
    public ErrorObj ERROR_344 = new ErrorObj(
            344,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto DebitNoteType.");
    public ErrorObj ERROR_345 = new ErrorObj(
            345,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto VoidedDocumentsType.");
    public ErrorObj ERROR_346 = new ErrorObj(
            346,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto SummaryDocumentsType.");
    public ErrorObj ERROR_351 = new ErrorObj(
            351,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto PerceptionType.");
    public ErrorObj ERROR_352 = new ErrorObj(
            352,
            "[UBL-DOCUMENT] Hubo un problema generico al construir el objeto RetentionType.");
    public ErrorObj ERROR_347 = new ErrorObj(347,
            "[UBL-DOCUMENT] Hubo un problema con la fecha de referencia.");
    /*public ErrorObj ERROR_348 = new ErrorObj(348,
            "[UBL-DOCUMENT] No se encontro el valor del SUBTOTAL en la transaccion.");*/
    public ErrorObj ERROR_349 = new ErrorObj(
            349,
            "[UBL-DOCUMENT] Hubo un problema al generar el BillingPayment en una de las LINEAS del RESUMEN DIARIO.");
    public ErrorObj ERROR_350 = new ErrorObj(
            350,
            "[UBL-DOCUMENT] Hubo un problema al generar el TaxtTotal de Impuestos del RESUMEN DIARIO.");

    /**
     * Seccion: PDFCreatorDocument
     * <p>
     * Disponible: 401 - 450
     */
    public ErrorObj ERROR_401 = new ErrorObj(401,
            "[PDF] No se encontro el TEMPLATE del PDF de la Factura.");
    public ErrorObj ERROR_402 = new ErrorObj(402,
            "[PDF] No se encontro el TEMPLATE del PDF de la Boleta de Venta.");
    public ErrorObj ERROR_403 = new ErrorObj(403,
            "[PDF] No se encontro el TEMPLATE del PDF de la Nota de Credito.");
    public ErrorObj ERROR_404 = new ErrorObj(404,
            "[PDF] No se encontro el TEMPLATE del PDF de la Nota de Debito.");
    public ErrorObj ERROR_405 = new ErrorObj(405,
            "[PDF] Hubo un problema al iniciar la carga del TEMPLATE.");
    public ErrorObj ERROR_406 = new ErrorObj(406,
            "[PDF] El objeto InvoiceObj es nulo.");
    public ErrorObj ERROR_407 = new ErrorObj(407,
            "[PDF] El objeto BoletaObj es nulo.");
    public ErrorObj ERROR_408 = new ErrorObj(408,
            "[PDF] El objeto CreditNoteObj es nulo.");
    public ErrorObj ERROR_409 = new ErrorObj(409,
            "[PDF] El objeto DebitNoteObj es nulo.");
    public ErrorObj ERROR_410 = new ErrorObj(410,
            "[PDF] No se pudo dar formato al departamento, provincia y distrito.");
    public ErrorObj ERROR_411 = new ErrorObj(411,
            "[PDF] No se encontraron items en el objeto InvoiceType de la FACTURA.");
    public ErrorObj ERROR_412 = new ErrorObj(412,
            "[PDF] No se encontraron items en el objeto InvoiceType de la BOLETA.");
    public ErrorObj ERROR_413 = new ErrorObj(
            413,
            "[PDF] No se encontraron items en el objeto CreditNoteType de la NOTA DE CREDITO.");
    public ErrorObj ERROR_414 = new ErrorObj(
            414,
            "[PDF] No se encontraron items en el objeto DebitNoteType de la NOTA DE DEBITO.");
    public ErrorObj ERROR_415 = new ErrorObj(415,
            "[PDF] Hubo un problema al extraer la informacion de los items.");
    public ErrorObj ERROR_416 = new ErrorObj(416,
            "[PDF] La lista de AlternativeConditionPrice de un item es nula.");
    public ErrorObj ERROR_417 = new ErrorObj(
            417,
            "[PDF] No se pudo encontrar el valor del AlternativeConditionPrice especificado segun el codigo.");
    public ErrorObj ERROR_418 = new ErrorObj(418,
            "[PDF] Ocurrio un problema al generar el CODIGO DE BARRAS.");
    public ErrorObj ERROR_419 = new ErrorObj(419,
            "[PDF] Ocurrio un problema al extraer uno de los valores TOTALES.");
    public ErrorObj ERROR_420 = new ErrorObj(420,
            "[PDF] Ocurrio un problema al extraer uno de los valores de PROPIEDADES.");
    public ErrorObj ERROR_421 = new ErrorObj(421,
            "[PDF] El codigo del SUNATTransaction no es valido.");
    public ErrorObj ERROR_422 = new ErrorObj(422,
            "[PDF] El codigo de moneda no tiene una definicion en texto.");
    public ErrorObj ERROR_423 = new ErrorObj(423,
            "[PDF] El valor del DigestValue es nulo o vacio.");
    public ErrorObj ERROR_424 = new ErrorObj(424,
            "[PDF] El valor del SignatureValue es nulo o vacio.");
    public ErrorObj ERROR_425 = new ErrorObj(425,
            "[PDF] El tipo de identificador del receptor no es valido.");
    public ErrorObj ERROR_426 = new ErrorObj(426,
            "[PDF] El codigo del tipo de Nota de Credito no es valido.");
    public ErrorObj ERROR_427 = new ErrorObj(427,
            "[PDF] El codigo del tipo de Nota de Credito esta vacio.");
    public ErrorObj ERROR_428 = new ErrorObj(
            428,
            "[PDF] No existe documento relacionado a la Nota de Credito. BillingReference=null");
    public ErrorObj ERROR_429 = new ErrorObj(
            429,
            "[PDF] No existe documento relacionado a la Nota de Debito. BillingReference=null");
    public ErrorObj ERROR_430 = new ErrorObj(430,
            "[PDF] El codigo del tipo de documento relacionado no es valido.");
    public ErrorObj ERROR_431 = new ErrorObj(
            431,
            "[PDF] No es posible generar los datos del RECEPTOR porque la serie del documento no es valida.");
    public ErrorObj ERROR_432 = new ErrorObj(432,
            "[PDF] El codigo del tipo de Nota de Debito no es valido.");
    public ErrorObj ERROR_433 = new ErrorObj(433,
            "[PDF] El codigo del tipo de Nota de Debito esta vacio.");

    public ErrorObj ERROR_441 = new ErrorObj(441,
            "[PDF] Ocurrio un error al crear el PDF de la FACTURA.");
    public ErrorObj ERROR_442 = new ErrorObj(442,
            "[PDF] Ocurrio un error al crear el PDF de la BOLETA DE VENTA.");
    public ErrorObj ERROR_443 = new ErrorObj(443,
            "[PDF] Ocurrio un error al crear el PDF de la NOTA DE CREDITO.");
    public ErrorObj ERROR_444 = new ErrorObj(444,
            "[PDF] Ocurrio un error al crear el PDF de la NOTA DE DEBITO.");

    /**
     * Seccion: SunatConnectorClient
     * <p>
     * Disponible: 451 - 500
     */
    public ErrorObj ERROR_451 = new ErrorObj(451,
            "[SUNAT-CONNECTOR] El tipo de transaccion es invalida.");
    public ErrorObj ERROR_452 = new ErrorObj(452,
            "[SUNAT-CONNECTOR] La transaccion de entrada es nula.");
    public ErrorObj ERROR_453 = new ErrorObj(
            453,
            "[SUNAT-CONNECTOR] Hubo un problema al transformar el archivo de configuracion a un objeto JAXB.");
    public ErrorObj ERROR_454 = new ErrorObj(454,
            "[SUNAT-CONNECTOR] Hubo un problema al guardar el documento UBL en DISCO.");
    public ErrorObj ERROR_455 = new ErrorObj(
            455,
            "[SUNAT-CONNECTOR] Ocurrio un error al convertir el documento UBL al formato ZIP.");
    public ErrorObj ERROR_456 = new ErrorObj(456,
            "[SUNAT-CONNECTOR] No se pudo configurar el WS Consumidor.");
    public ErrorObj ERROR_457 = new ErrorObj(457,
            "[SUNAT-CONNECTOR] Hubo un problema al comprimir el documento.");
    public ErrorObj ERROR_458 = new ErrorObj(458,
            "[SUNAT-CONNECTOR] La constancia CDR retornada por Sunat es nula.");
    public ErrorObj ERROR_459 = new ErrorObj(
            459,
            "[SUNAT-CONNECTOR] Ocurrio un problema al convertir el documento UBL en DISCO a bytes.");
    public ErrorObj ERROR_460 = new ErrorObj(
            460,
            "[SUNAT-CONNECTOR] No es posible generar la representacion impresa porque el tipo de documento no es valido.");
    public ErrorObj ERROR_461 = new ErrorObj(
            461,
            "[SUNAT-CONNECTOR] No es posible guardar el documento PDF en DISCO porque el PDF en bytes es nulo.");
    public ErrorObj ERROR_462 = new ErrorObj(462,
            "[SUNAT-CONNECTOR] Hubo un problema al extraer un tipo de impuesto.");
    public ErrorObj ERROR_463 = new ErrorObj(
            463,
            "[SUNAT-CONNECTOR] El tipo de documento en la transaccion DOC_Codigo no es valido.");
    public ErrorObj ERROR_464 = new ErrorObj(464,
            "[SUNAT-CONNECTOR] No se pudo configurar el WS Consumidor de Consultas.");
    public ErrorObj ERROR_465 = new ErrorObj(
            465,
            "[SUNAT-CONNECTOR] El objeto StatusResponse retornado por el WS de Consulta es nulo.");

    /**
     * Seccion: CHECK
     * <p>
     * Disponible: 501 - 550
     */
    public ErrorObj ERROR_501 = new ErrorObj(501,
            "[CHECK] Ocurrio un problema al extraer uno de los valores TOTALES.");
    public ErrorObj ERROR_502 = new ErrorObj(
            502,
            "[CHECK] No se encontro el TAG de totales de la OP.GRAVADA, pero existen items con dicho tipo de operacion.");
    public ErrorObj ERROR_503 = new ErrorObj(
            503,
            "[CHECK] No se encontro items con OP.GRAVADA, pero existe el TAG de totales de este tipo.");
    public ErrorObj ERROR_504 = new ErrorObj(
            504,
            "[CHECK] No se encontro el TAG de totales de la OP.INAFECTA, pero existen items con dicho tipo de operacion.");
    public ErrorObj ERROR_505 = new ErrorObj(
            505,
            "[CHECK] No se encontro items con OP.INAFECTA, pero existe el TAG de totales de este tipo.");
    public ErrorObj ERROR_506 = new ErrorObj(
            506,
            "[CHECK] No se encontro el TAG de totales de la OP.EXONERADA, pero existen items con dicho tipo de operacion.");
    public ErrorObj ERROR_507 = new ErrorObj(
            507,
            "[CHECK] No se encontro items con OP.EXONERADA, pero existe el TAG de totales de este tipo.");
    public ErrorObj ERROR_508 = new ErrorObj(
            508,
            "[CHECK] No se encontro el TAG de totales de la OP.GRATUITA, pero existen items con dicho tipo de operacion.");
    public ErrorObj ERROR_509 = new ErrorObj(
            509,
            "[CHECK] No se encontro items con OP.GRATUITA, pero existe el TAG de totales de este tipo.");
    public ErrorObj ERROR_510 = new ErrorObj(
            510,
            "[CHECK] No se encontro el TAG de impuesto de IGV TOTAL, pero existen items con dicho tipo de impuesto.");
    public ErrorObj ERROR_511 = new ErrorObj(
            511,
            "[CHECK] No se encontro items con impuesto de tipo IGV, pero existe el TAG de impuesto de IGV TOTAL.");
    public ErrorObj ERROR_512 = new ErrorObj(
            512,
            "[CHECK] No se encontro el TAG de impuesto de ISC TOTAL, pero existen items con dicho tipo de impuesto.");
    public ErrorObj ERROR_513 = new ErrorObj(
            513,
            "[CHECK] No se encontro items con impuesto de tipo ISC, pero existe el TAG de impuesto de ISC TOTAL.");
    public ErrorObj ERROR_514 = new ErrorObj(514,
            "[CHECK] La SERIE-CORRELATIVO del documento es nulo.");
    public ErrorObj ERROR_515 = new ErrorObj(
            515,
            "[CHECK] La SERIE-CORRELATIVO tiene mas caracteres de lo permitido. (Caracteres permitidos="
                    + IUBLConfig.SERIE_CORRELATIVE_LENGTH + ")");
    public ErrorObj ERROR_516 = new ErrorObj(516,
            "[CHECK] La SERIE-CORRELATIVO no tiene el formato correcto.");
    public ErrorObj ERROR_517 = new ErrorObj(
            517,
            "[CHECK] La SERIE-CORRELATIVO no tiene el formato correcto. Debe iniciar con F o P o B.");
    public ErrorObj ERROR_518 = new ErrorObj(
            518,
            "[CHECK] El CORRELATIVO del identificador del documento no tiene el formato correcto.");
    public ErrorObj ERROR_519 = new ErrorObj(519,
            "[CHECK] El Numero RUC del emisor debe contener 11 caracteres.");
    public ErrorObj ERROR_520 = new ErrorObj(520,
            "[CHECK] El Numero RUC no contiene el formato correcto.");
    public ErrorObj ERROR_521 = new ErrorObj(521,
            "[CHECK] La fecha de emision es nula.");
    public ErrorObj ERROR_522 = new ErrorObj(
            522,
            "[CHECK] No se encontro el TAG de DESCUENTO TOTAL, pero existen items con DESCUENTO DE LINEA.");
    public ErrorObj ERROR_523 = new ErrorObj(
            523,
            "[CHECK] No se encontro el TAG de DESCUENTO TOTAL, pero existe descuento a nivel GLOBAL.");
    public ErrorObj ERROR_524 = new ErrorObj(524,
            "[CHECK] El codigo [{0}] de TAG de TOTALES no es valido.");
    public ErrorObj ERROR_525 = new ErrorObj(525,
            "[CHECK] El codigo de tipo de nota de credito no es valido.");
    public ErrorObj ERROR_526 = new ErrorObj(526,
            "[CHECK] El codigo de tipo de nota de debito no es valido.");
    public ErrorObj ERROR_527 = new ErrorObj(
            527,
            "[CHECK] La lista de documentos referenciados a la nota electronica esta vacia.");
    public ErrorObj ERROR_528 = new ErrorObj(
            528,
            "[CHECK] El codigo de documento referenciado a la nota electronica no es valido.");
    public ErrorObj ERROR_529 = new ErrorObj(529,
            "[CHECK] El IDENTIFICADOR del documento es nulo.");
    public ErrorObj ERROR_530 = new ErrorObj(
            530,
            "[CHECK] El IDENTIFICADOR no tiene el formato correcto. Debe iniciar con RA o RC.");
    public ErrorObj ERROR_549 = new ErrorObj(549,
            "[CHECK] El Cliente debe tener un correo electronico.");
    public ErrorObj ERROR_550 = new ErrorObj(550,
            "[CHECK] La sociedad debe tener un correo electronico.");

    /**
     * PERCEPCION CHECK
     */

    public ErrorObj ERROR_531 = new ErrorObj(531,
            "[CHECK] El TAG SunatTotalCashed debe de ser mayor a CERO.");
    public ErrorObj ERROR_532 = new ErrorObj(532,
            "[CHECK] El TAG TotalInvoiceAmount debe de ser mayor a CERO.");
    public ErrorObj ERROR_533 = new ErrorObj(533,
            "[CHECK] Se encontro items TAG PaidAmount que son menores a CERO.");
    public ErrorObj ERROR_534 = new ErrorObj(534,
            "[CHECK] Se encontro items TAG TotalInvoiceAmount que son menores a CERO.");
    public ErrorObj ERROR_535 = new ErrorObj(535,
            "[CHECK] Se encontro items TAG PerceptionAmount que son menores a CERO.");
    public ErrorObj ERROR_536 = new ErrorObj(536,
            "[CHECK] Se encontro items TAG SunatNetTotalCashed que son menores a CERO.");
    public ErrorObj ERROR_537 = new ErrorObj(537,
            "[CHECK] El TAG SUNATPerceptionSystemCode debe contener solo DOS caracteres.");
    public ErrorObj ERROR_538 = new ErrorObj(538,
            "[CHECK] El TAG SunatPerceptionSystemCode debe contener solo UN caracter.");
    public ErrorObj ERROR_539 = new ErrorObj(539,
            "[CHECK] El formato ID de la referencia no tiene el formato adecuado.");
    public ErrorObj ERROR_1000 = new ErrorObj(1000,
            "[CHECK] El ubigeo del cliente no cumple con el formato establecido .");
    public ErrorObj ERROR_1001 = new ErrorObj(1001,
            "[CHECK] El ubigeo del emisor no cumple con el formato establecido .");

    /****
     * RETENCION CHECK
     *
     * ****/

    public ErrorObj ERROR_540 = new ErrorObj(540,
            "[CHECK] El TAG SunatRetentionSystemCode debe contener solo DOS caracteres.");
    public ErrorObj ERROR_541 = new ErrorObj(541,
            "[CHECK] El TAG SunatRetentionPercent debe contener solo UN caracter.");
    public ErrorObj ERROR_542 = new ErrorObj(542,
            "[CHECK] El TAG TotalInvoiceAmount debe ser mayor a CERO.");
    public ErrorObj ERROR_543 = new ErrorObj(
            543,
            "[CHECK] El TAG El TAG TotalPaid debe contener solo UN caracter debe ser mayor a CERO.");
    public ErrorObj ERROR_544 = new ErrorObj(544,
            "[CHECK] Se encontro items TAG PaidAmount que son menores a CERO.");
    public ErrorObj ERROR_545 = new ErrorObj(545,
            "[CHECK] Se encontro items TAG TotalInvoiceAmount que son menores a CERO.");
    public ErrorObj ERROR_546 = new ErrorObj(546,
            "[CHECK] Se encontro items TAG SunatRetentionAmount que son menores a CERO.");
    public ErrorObj ERROR_547 = new ErrorObj(547,
            "[CHECK] Se encontro items TAG SunatNetTotalCashed que son menores a CERO.");
    public ErrorObj ERROR_548 = new ErrorObj(548,
            "[CHECK] El formato ID de la referencia no tiene el formato adecuado.");
    public ErrorObj ERROR_1100 = new ErrorObj(1100,
            "[CHECK] El ubigeo del cliente no cumple con el formato establecido .");
    public ErrorObj ERROR_1101 = new ErrorObj(1101,
            "[CHECK] El ubigeo del emisor no cumple con el formato establecido .");

    /*
     * ERROR DE CONEXION A INTERNET
     */
    public ErrorObj ERROR_5000 = new ErrorObj(5000,
            "[CONNECTION] No se puede establecer conexion a internet.");

    public ErrorObj ERROR_5001 = new ErrorObj(5001,
            "[SUNAT] EL CDR devuelto se encuentra vacio.");

} 
