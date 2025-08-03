-- ----------------------------------------------------------------------------
-- MySQL Workbench Migration
-- Migrated Schemata: VSCPEBD
-- Source Schemata: VSCPEBD
-- Created: Fri Feb 12 09:53:45 2016
-- Workbench Version: 6.3.6
-- ----------------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- Schema VSCPEBD
-- ----------------------------------------------------------------------------
DROP SCHEMA IF EXISTS `VSCPEBD` ;
CREATE SCHEMA IF NOT EXISTS `VSCPEBD` ;

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.GUIA_REMISION
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`GUIA_REMISION` (
  `ID` VARCHAR(255) NOT NULL,
  `CTP_NumDocId` VARCHAR(255) NULL,
  `CTP_TipoDocId` VARCHAR(255) NULL,
  `DD_Denominacion` VARCHAR(255) NULL,
  `DD_NumDocId` VARCHAR(255) NULL,
  `DD_TipoDocId` VARCHAR(255) NULL,
  `DE_DesMotv` VARCHAR(255) NULL,
  `DE_FecIniTras` VARCHAR(255) NULL,
  `DE_IndTransb` SMALLINT NULL,
  `DE_ModalidadTras` VARCHAR(255) NULL,
  `DE_MotivTras` VARCHAR(255) NULL,
  `DE_NumBultos` BIGINT NULL,
  `DE_PesoBTotal` BIGINT NULL,
  `DE_UnidMedida` VARCHAR(255) NULL,
  `DOCR_CodTipoDoc` VARCHAR(255) NULL,
  `DOCR_NumDoc` VARCHAR(255) NULL,
  `DPL_Direccion` VARCHAR(255) NULL,
  `DPL_Ubigeo` VARCHAR(255) NULL,
  `DPP_Direccion` VARCHAR(255) NULL,
  `DPP_Ubigeo` VARCHAR(255) NULL,
  `DR_Denominacion` VARCHAR(255) NULL,
  `DR_NumDocId` VARCHAR(255) NULL,
  `DR_TipoDocId` VARCHAR(255) NULL,
  `DT_Denominacion` VARCHAR(255) NULL,
  `DT_NumDocId` VARCHAR(255) NULL,
  `DT_TipoDocId` VARCHAR(255) NULL,
  `GRB_CodTipoId` VARCHAR(255) NULL,
  `GRB_Correlativo` VARCHAR(255) NULL,
  `GRB_Numeracion` VARCHAR(255) NULL,
  `GRB_Serie` VARCHAR(255) NULL,
  `GRB_TipoDoc` VARCHAR(255) NULL,
  `GR_Correlativo` VARCHAR(255) NULL,
  `GR_FecEmision` VARCHAR(255) NULL,
  `GR_Numeracion` VARCHAR(255) NULL,
  `GR_Obervación` VARCHAR(255) NULL,
  `GR_Serie` VARCHAR(255) NULL,
  `GR_TipoDoc` VARCHAR(255) NULL,
  `MI_NumContenedor` VARCHAR(255) NULL,
  `PED_CodPuerto` VARCHAR(255) NULL,
  `TTP_Denominacion` VARCHAR(255) NULL,
  `TTP_NumRuc` VARCHAR(255) NULL,
  `TTP_TipoDocId` VARCHAR(255) NULL,
  `VTP_NumPlaca` VARCHAR(255) NULL,
  PRIMARY KEY (`ID`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.GUIA_REMISION_LINEA
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`GUIA_REMISION_LINEA` (
  `BT_Cantidad` BIGINT NULL,
  `BT_Codigo` VARCHAR(255) NULL,
  `BT_Descripcion` VARCHAR(255) NULL,
  `BT_NumOrden` INT NULL,
  `BT_UnidMedida` VARCHAR(255) NULL,
  `ID` VARCHAR(255) NOT NULL,
  `ID_Linea` INT NOT NULL,
  PRIMARY KEY (`ID`, `ID_Linea`),
  CONSTRAINT `FK__GUIA_REMISIO__ID__1273C1CD`
    FOREIGN KEY (`ID`)
    REFERENCES `VSCPEBD`.`GUIA_REMISION` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION` (
  `DocIdentidad_Nro` VARCHAR(32) NULL,
  `DocIdentidad_Tipo` VARCHAR(2) NULL,
  `RazonSocial` VARCHAR(200) NULL,
  `NombreComercial` VARCHAR(200) NULL,
  `PersonContacto` VARCHAR(150) NULL,
  `EMail` VARCHAR(150) NULL,
  `Telefono` VARCHAR(20) NULL,
  `Telefono_1` VARCHAR(20) NULL,
  `Web` VARCHAR(50) NULL,
  `DIR_Pais` VARCHAR(2) NULL,
  `DIR_Departamento` VARCHAR(100) NULL,
  `DIR_Provincia` VARCHAR(100) NULL,
  `DIR_Distrito` VARCHAR(100) NULL,
  `DIR_Direccion` VARCHAR(500) NULL,
  `DIR_NomCalle` VARCHAR(100) NULL,
  `DIR_NroCasa` VARCHAR(20) NULL,
  `DIR_Ubigeo` INT NULL,
  `DIR_Urbanizacion` VARCHAR(30) NULL,
  `SN_DocIdentidad_Nro` VARCHAR(15) NULL,
  `SN_DocIdentidad_Tipo` VARCHAR(2) NULL,
  `SN_RazonSocial` VARCHAR(200) NULL,
  `SN_NombreComercial` VARCHAR(200) NULL,
  `SN_EMail` VARCHAR(100) NULL,
  `SN_SegundoNombre` VARCHAR(50) NULL,
  `SN_DIR_Pais` VARCHAR(2) NULL,
  `SN_DIR_Departamento` VARCHAR(100) NULL,
  `SN_DIR_Provincia` VARCHAR(100) NULL,
  `SN_DIR_Distrito` VARCHAR(100) NULL,
  `SN_DIR_Direccion` VARCHAR(500) NULL,
  `SN_DIR_NomCalle` VARCHAR(100) NULL,
  `SN_DIR_NroCasa` VARCHAR(20) NULL,
  `SN_DIR_Ubigeo` INT NULL,
  `SN_DIR_Urbanizacion` VARCHAR(30) NULL,
  `DOC_Serie` VARCHAR(4) NULL,
  `DOC_Numero` VARCHAR(15) NULL,
  `DOC_Id` VARCHAR(20) NULL,
  `DOC_FechaEmision` DATETIME(6) NULL,
  `DOC_FechaVencimiento` DATETIME(6) NULL,
  `DOC_Dscrpcion` VARCHAR(50) NULL,
  `DOC_Codigo` VARCHAR(2) NULL,
  `DOC_MON_Nombre` VARCHAR(20) NULL,
  `DOC_MON_Codigo` VARCHAR(3) NULL,
  `DOC_Descuento` DECIMAL(19,6) NULL,
  `DOC_MontoTotal` DECIMAL(19,6) NULL,
  `DOC_DescuentoTotal` DECIMAL(19,6) NULL,
  `DOC_Importe` DECIMAL(19,6) NULL,
  `DOC_PorcImpuesto` VARCHAR(100) NULL,
  `ANTICIPO_Id` VARCHAR(20) NULL,
  `ANTICIPO_Tipo` VARCHAR(2) NULL,
  `ANTICIPO_Monto` DECIMAL(19,6) NULL,
  `ANTCIPO_Tipo_Doc_ID` VARCHAR(50) NULL,
  `ANTICIPO_Nro_Doc_ID` VARCHAR(50) NULL,
  `SUNAT_Transact` VARCHAR(2) NULL,
  `FE_DocEntry` INT NULL,
  `FE_ObjectType` VARCHAR(30) NULL,
  `FE_Estado` VARCHAR(1) NULL,
  `FE_TipoTrans` VARCHAR(1) NULL,
  `FE_Id` VARCHAR(20) NOT NULL,
  `FE_DocNum` INT NULL,
  `FE_FormSAP` VARCHAR(50) NULL,
  `REFDOC_Tipo` VARCHAR(2) NULL,
  `REFDOC_Id` VARCHAR(50) NULL,
  `REFDOC_MotivCode` VARCHAR(30) NULL,
  `REFDOC_MotivDesc` VARCHAR(50) NULL,
  `FE_Comentario` VARCHAR(254) NULL,
  `FE_ErrCod` VARCHAR(8) NULL,
  `FE_ErrMsj` VARCHAR(254) NULL,
  `FE_Errores` INT NULL,
  `FE_MaxSalto` INT NULL,
  `FE_Saltos` INT NULL,
  `DOC_CondPago` VARCHAR(100) NULL,
  `RET_Regimen` VARCHAR(2) NULL,
  `RET_Tasa` VARCHAR(4) NULL,
  `Observaciones` VARCHAR(250) NULL,
  `ImportePagado` DECIMAL(19,6) NULL,
  `MonedaPagado` VARCHAR(3) NULL,
  PRIMARY KEY (`FE_Id`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_IMPUESTOS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_IMPUESTOS` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `LineId` INT NOT NULL,
  `Moneda` VARCHAR(10) NULL,
  `Monto` DECIMAL(19,6) NULL,
  `Porcentaje` DECIMAL(19,6) NULL,
  `TipoTributo` VARCHAR(30) NULL,
  `TipoAfectacion` VARCHAR(30) NULL,
  `TierRange` VARCHAR(30) NULL,
  PRIMARY KEY (`FE_Id`, `LineId`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__173876EA`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_BAJA
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_BAJA` (
  `fecha` BIGINT NOT NULL,
  `ID` BIGINT NOT NULL,
  `serie` VARCHAR(20) NULL,
  PRIMARY KEY (`fecha`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.RESUMENDIARIO_CONFIG
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`RESUMENDIARIO_CONFIG` (
  `fecha` DATETIME(6) NOT NULL,
  `hora` VARCHAR(20) NULL,
  PRIMARY KEY (`fecha`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_LINEAS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_LINEAS` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `NroOrden` INT NOT NULL,
  `DSCTO_Porcentaje` DECIMAL(19,6) NULL,
  `DSCTO_Monto` DECIMAL(19,6) NULL,
  `Descripcion` VARCHAR(100) NULL,
  `PrecioDscto` DECIMAL(19,6) NULL,
  `PrecioIGV` DECIMAL(19,6) NULL,
  `TotalLineaSinIGV` DECIMAL(19,6) NULL,
  `TotalLineaConIGV` DECIMAL(19,6) NULL,
  `PrecioRef_Monto` DECIMAL(19,6) NULL,
  `PrecioRef_Codigo` VARCHAR(2) NULL,
  `Cantidad` DECIMAL(19,6) NULL,
  `Unidad` VARCHAR(100) NULL,
  `UnidadSunat` VARCHAR(100) NULL,
  `CodArticulo` VARCHAR(20) NULL,
  PRIMARY KEY (`FE_Id`, `NroOrden`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__1DE57479`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_LINEAS_BILLREF
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_LINEAS_BILLREF` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `NroOrden` INT NOT NULL,
  `LineId` INT NOT NULL,
  `AdtDocRef_Id` VARCHAR(50) NULL,
  `AdtDocRef_SchemaId` VARCHAR(30) NULL,
  `InvDocRef_DocTypeCode` VARCHAR(10) NULL,
  `InvDocRef_Id` VARCHAR(5) NULL,
  PRIMARY KEY (`FE_Id`, `LineId`, `NroOrden`),
  CONSTRAINT `FK__TRANSACCION_LINE__20C1E124`
    FOREIGN KEY (`FE_Id` , `NroOrden`)
    REFERENCES `VSCPEBD`.`TRANSACCION_LINEAS` (`FE_Id` , `NroOrden`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_LINEA_IMPUESTOS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_LINEA_IMPUESTOS` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `LineId` INT NOT NULL,
  `NroOrden` INT NOT NULL,
  `Moneda` VARCHAR(10) NULL,
  `Monto` DECIMAL(19,6) NULL,
  `Porcentaje` DECIMAL(19,6) NULL,
  `TipoTributo` VARCHAR(30) NULL,
  `TipoAfectacion` VARCHAR(30) NULL,
  `TierRange` VARCHAR(30) NULL,
  PRIMARY KEY (`FE_Id`, `LineId`, `NroOrden`),
  CONSTRAINT `FK__TRANSACCION_LINE__239E4DCF`
    FOREIGN KEY (`FE_Id` , `NroOrden`)
    REFERENCES `VSCPEBD`.`TRANSACCION_LINEAS` (`FE_Id` , `NroOrden`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.USUARIOCAMPOS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`USUARIOCAMPOS` (
  `Id` INT NOT NULL AUTO_INCREMENT,
  `Nombre` VARCHAR(500) NULL,
  PRIMARY KEY (`Id`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_USUCAMPOS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_USUCAMPOS` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `USUCMP_Id` INT NOT NULL,
  `Valor` VARCHAR(255) NULL,
  PRIMARY KEY (`FE_Id`, `USUCMP_Id`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__286302EC`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK__TRANSACCI__USUCM__29572725`
    FOREIGN KEY (`USUCMP_Id`)
    REFERENCES `VSCPEBD`.`USUARIOCAMPOS` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_LINEAS_USUCAMPOS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_LINEAS_USUCAMPOS` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `NroOrden` INT NOT NULL,
  `USUCMP_Id` INT NOT NULL,
  `Valor` VARCHAR(255) NULL,
  PRIMARY KEY (`FE_Id`, `NroOrden`, `USUCMP_Id`),
  CONSTRAINT `FK__TRANSACCION_LINE__2C3393D0`
    FOREIGN KEY (`FE_Id` , `NroOrden`)
    REFERENCES `VSCPEBD`.`TRANSACCION_LINEAS` (`FE_Id` , `NroOrden`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK__TRANSACCI__USUCM__2D27B809`
    FOREIGN KEY (`USUCMP_Id`)
    REFERENCES `VSCPEBD`.`USUARIOCAMPOS` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_ERROR
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_ERROR` (
  `FE_Id` VARCHAR(25) NOT NULL,
  `Docentry` INT NULL,
  `Docnum` INT NULL,
  `FE_ObjectType` VARCHAR(15) NULL,
  `FE_FormSAP` VARCHAR(50) NULL,
  `FE_TipoTrans` VARCHAR(1) NULL,
  `Err_Codigo` INT NULL,
  `Err_Mensaje` VARCHAR(255) NULL,
  PRIMARY KEY (`FE_Id`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_CONTRACTDOCREF
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_CONTRACTDOCREF` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `USUCMP_Id` INT NOT NULL,
  `Valor` VARCHAR(500) NULL,
  PRIMARY KEY (`FE_Id`, `USUCMP_Id`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__31EC6D26`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK__TRANSACCI__USUCM__32E0915F`
    FOREIGN KEY (`USUCMP_Id`)
    REFERENCES `VSCPEBD`.`USUARIOCAMPOS` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_TOTALES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_TOTALES` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `Id` VARCHAR(4) NOT NULL,
  `Monto` DECIMAL(19,6) NULL,
  `Prcnt` DECIMAL(19,6) NULL,
  PRIMARY KEY (`FE_Id`, `Id`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__35BCFE0A`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_PROPIEDADES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_PROPIEDADES` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `Id` VARCHAR(4) NOT NULL,
  `Valor` VARCHAR(300) NULL,
  PRIMARY KEY (`FE_Id`, `Id`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__38996AB5`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_RESUMEN
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_RESUMEN` (
  `Id_Transaccion` VARCHAR(25) NOT NULL,
  `Numero_Ruc` VARCHAR(11) NULL,
  `DocIdentidad_Tipo` CHAR(10) NULL,
  `RazonSocial` VARCHAR(100) NULL,
  `NombreComercial` VARCHAR(150) NULL,
  `PersonContacto` VARCHAR(100) NULL,
  `EMail` VARCHAR(50) NULL,
  `DIR_Pais` VARCHAR(50) NULL,
  `DIR_Departamento` VARCHAR(50) NULL,
  `DIR_Provincia` VARCHAR(50) NULL,
  `DIR_Distrito` VARCHAR(50) NULL,
  `DIR_Direccion` LONGTEXT NULL,
  `DIR_NomCalle` VARCHAR(50) NULL,
  `DIR_NroCasa` VARCHAR(50) NULL,
  `DIR_Ubigeo` VARCHAR(50) NULL,
  `Fecha_Emision` VARCHAR(50) NULL,
  `Fecha_Generacion` VARCHAR(50) NULL,
  `Estado` VARCHAR(20) NULL,
  `Numero_Ticket` VARCHAR(50) NULL,
  PRIMARY KEY (`Id_Transaccion`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_RESUMEN_LINEA
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_RESUMEN_LINEA` (
  `Id_Transaccion` VARCHAR(25) NOT NULL,
  `Id_Linea` INT NOT NULL,
  `Tipo_Documento` VARCHAR(2) NULL,
  `Numero_Serie` VARCHAR(4) NULL,
  `Numero_Correlativo_Inicio` VARCHAR(8) NULL,
  `Numero_Correlativo_Fin` VARCHAR(8) NULL,
  `Total_OP_Gravadas` DECIMAL(17,2) NULL,
  `Total_OP_Exoneradas` DECIMAL(17,2) NULL,
  `Total_OP_Inafectas` DECIMAL(17,2) NULL,
  `Importe_Otros_Cargos` DECIMAL(17,2) NULL,
  `Total_ISC` DECIMAL(17,2) NULL,
  `Tota_IGV` DECIMAL(17,2) NULL,
  `Total_Otros_Tributos` DECIMAL(17,2) NULL,
  `Importe_Total` DECIMAL(17,2) NULL,
  `CodMoneda` VARCHAR(50) NULL,
  PRIMARY KEY (`Id_Linea`, `Id_Transaccion`),
  CONSTRAINT `FK__TRANSACCI__Id_Tr__3D5E1FD2`
    FOREIGN KEY (`Id_Transaccion`)
    REFERENCES `VSCPEBD`.`TRANSACCION_RESUMEN` (`Id_Transaccion`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_RESUMEN_LINEA_ANEXO
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_RESUMEN_LINEA_ANEXO` (
  `Id_Transaccion` VARCHAR(25) NOT NULL,
  `Id_Linea` INT NOT NULL,
  `DocEntry` VARCHAR(50) NULL,
  `TipoTransaccion` VARCHAR(5) NULL,
  `SN` VARCHAR(50) NULL,
  `ObjcType` VARCHAR(4) NULL,
  `TipoDocumento` VARCHAR(4) NULL,
  `Serie` VARCHAR(10) NULL,
  `Correlativo` VARCHAR(10) NULL,
  PRIMARY KEY (`Id_Linea`, `Id_Transaccion`),
  CONSTRAINT `FK__TRANSACCI__Id_Tr__403A8C7D`
    FOREIGN KEY (`Id_Transaccion`)
    REFERENCES `VSCPEBD`.`TRANSACCION_RESUMEN` (`Id_Transaccion`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_COMPROBANTE_PAGO
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_COMPROBANTE_PAGO` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `NroOrden` INT NOT NULL,
  `DOC_Tipo` VARCHAR(2) NULL,
  `DOC_Numero` VARCHAR(13) NULL,
  `DOC_FechaEmision` DATETIME(6) NULL,
  `DOC_Importe` DECIMAL(19,6) NULL,
  `DOC_Moneda` VARCHAR(3) NULL,
  `PagoFecha` DATETIME(6) NULL,
  `PagoNumero` VARCHAR(9) NULL,
  `PagoImporteSR` DECIMAL(19,6) NULL,
  `PagoMoneda` VARCHAR(3) NULL,
  `CP_Importe` DECIMAL(19,6) NULL,
  `CP_Moneda` VARCHAR(3) NULL,
  `CP_Fecha` DATETIME(6) NULL,
  `CP_ImporteTotal` DECIMAL(19,6) NULL,
  `CP_MonedaMontoNeto` VARCHAR(3) NULL,
  `TC_MonedaRef` VARCHAR(3) NULL,
  `TC_MonedaObj` VARCHAR(3) NULL,
  `TC_Factor` DECIMAL(19,6) NULL,
  `TC_Fecha` DATETIME(6) NULL,
  PRIMARY KEY (`FE_Id`, `NroOrden`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__4316F928`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_DOCREFERS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_DOCREFERS` (
  `FE_Id` VARCHAR(20) NOT NULL,
  `LineId` INT NOT NULL,
  `Tipo` VARCHAR(2) NULL,
  `Id` VARCHAR(500) NULL,
  PRIMARY KEY (`FE_Id`, `LineId`),
  CONSTRAINT `FK__TRANSACCI__FE_Id__45F365D3`
    FOREIGN KEY (`FE_Id`)
    REFERENCES `VSCPEBD`.`TRANSACCION` (`FE_Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.PUBLICACION_DOCUMENTO
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`PUBLICACION_DOCUMENTO` (
  `FE_Id` VARCHAR(255) NOT NULL,
  `emailConsumidor` VARCHAR(255) NULL,
  `emailEmisor` VARCHAR(255) NULL,
  `Estado` INT NULL,
  `estadoSunat` VARCHAR(255) NULL,
  `FechaEmision` VARCHAR(255) NULL,
  `moneda` VARCHAR(255) NULL,
  `Msj_Error` VARCHAR(255) NULL,
  `nombreConsumidor` VARCHAR(255) NULL,
  `numeroSerie` VARCHAR(255) NULL,
  `Ruc` VARCHAR(255) NULL,
  `Ruta_Cdr` VARCHAR(255) NULL,
  `Ruta_Pdf` VARCHAR(255) NULL,
  `Ruta_Xml` VARCHAR(255) NULL,
  `TipoDocumento` VARCHAR(255) NULL,
  `Total` BIGINT NULL,
  PRIMARY KEY (`FE_Id`));


-- ----------------------------------------------------------------------------
-- Table VSCPEBD.LOG_TRANS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`LOG_TRANS` (
  `Keylog` INT NOT NULL AUTO_INCREMENT,
  `Conector` VARCHAR(255) NULL,
  `Doc_Id` VARCHAR(255) NULL,
  `Fecha` DATETIME(6) NULL,
  `Hora` INT NULL,
  `Proceso` VARCHAR(255) NULL,
  `Tarea` VARCHAR(255) NULL,
  `TramaEnvio` LONGTEXT NULL,
  `TramaRespuesta` LONGTEXT NULL,
  PRIMARY KEY (`Keylog`));

-- ----------------------------------------------------------------------------
-- Table VSCPEBD.TRANSACCION_RESUMEN_COMPROBANTE
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `VSCPEBD`.`TRANSACCION_RESUMEN_COMPROBANTE` (
  `ID` INT NOT NULL,
  `DOC_Correlativo` VARCHAR(255) NULL,
  `DOC_Estado` VARCHAR(255) NULL,
  `DOC_FechaEmision` VARCHAR(255) NULL,
  `DOC_Ruc` VARCHAR(255) NULL,
  `DOC_Tipo` VARCHAR(255) NULL,
  PRIMARY KEY (`ID`));
SET FOREIGN_KEY_CHECKS = 1;
