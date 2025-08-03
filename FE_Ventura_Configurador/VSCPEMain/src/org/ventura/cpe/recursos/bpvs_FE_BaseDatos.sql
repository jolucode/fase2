USE MASTER
GO

SET NOCOUNT ON
DECLARE @DBName varchar(50)
DECLARE @spidstr varchar(8000)
DECLARE @ConnKilled smallint
SET @ConnKilled=0
SET @spidstr = ''
 
Set @DBName = 'VSCPEBD'
IF db_id(@DBName) < 4
BEGIN
PRINT 'Connections to system databases cannot be killed'
RETURN
END
SELECT @spidstr=coalesce(@spidstr,',' )+'kill '+convert(varchar, spid)+ '; '
FROM master..sysprocesses WHERE dbid=db_id(@DBName)
 
IF LEN(@spidstr) > 0
BEGIN
EXEC(@spidstr)
SELECT @ConnKilled = COUNT(1)
FROM master..sysprocesses WHERE dbid=db_id(@DBName)
END

IF  EXISTS (Select name from sys.databases WHERE name = N'VSCPEBD')
	DROP DATABASE VSCPEBD
GO

CREATE DATABASE VSCPEBD
GO

USE VSCPEBD
GO

CREATE TABLE [dbo].[GUIA_REMISION](
	[ID] [varchar](255) NOT NULL,
	[CTP_NumDocId] [varchar](255) NULL,
	[CTP_TipoDocId] [varchar](255) NULL,
	[DD_Denominacion] [varchar](255) NULL,
	[DD_NumDocId] [varchar](255) NULL,
	[DD_TipoDocId] [varchar](255) NULL,
	[DE_DesMotv] [varchar](255) NULL,
	[DE_FecIniTras] [varchar](255) NULL,
	[DE_IndTransb] [smallint] NULL,
	[DE_ModalidadTras] [varchar](255) NULL,
	[DE_MotivTras] [varchar](255) NULL,
	[DE_NumBultos] [numeric](28, 0) NULL,
	[DE_PesoBTotal] [numeric](28, 0) NULL,
	[DE_UnidMedida] [varchar](255) NULL,
	[DOCR_CodTipoDoc] [varchar](255) NULL,
	[DOCR_NumDoc] [varchar](255) NULL,
	[DPL_Direccion] [varchar](255) NULL,
	[DPL_Ubigeo] [varchar](255) NULL,
	[DPP_Direccion] [varchar](255) NULL,
	[DPP_Ubigeo] [varchar](255) NULL,
	[DR_Denominacion] [varchar](255) NULL,
	[DR_NumDocId] [varchar](255) NULL,
	[DR_TipoDocId] [varchar](255) NULL,
	[DT_Denominacion] [varchar](255) NULL,
	[DT_NumDocId] [varchar](255) NULL,
	[DT_TipoDocId] [varchar](255) NULL,
	[GRB_CodTipoId] [varchar](255) NULL,
	[GRB_Correlativo] [varchar](255) NULL,
	[GRB_Numeracion] [varchar](255) NULL,
	[GRB_Serie] [varchar](255) NULL,
	[GRB_TipoDoc] [varchar](255) NULL,
	[GR_Correlativo] [varchar](255) NULL,
	[GR_FecEmision] [varchar](255) NULL,
	[GR_Numeracion] [varchar](255) NULL,
	[GR_Obervación] [varchar](255) NULL,
	[GR_Serie] [varchar](255) NULL,
	[GR_TipoDoc] [varchar](255) NULL,
	[MI_NumContenedor] [varchar](255) NULL,
	[PED_CodPuerto] [varchar](255) NULL,
	[TTP_Denominacion] [varchar](255) NULL,
	[TTP_NumRuc] [varchar](255) NULL,
	[TTP_TipoDocId] [varchar](255) NULL,
	[VTP_NumPlaca] [varchar](255) NULL
	primary key ([ID])
)	

GO

CREATE TABLE [dbo].[GUIA_REMISION_LINEA](
	[BT_Cantidad] [numeric](28, 0) NULL,
	[BT_Codigo] [varchar](255) NULL,
	[BT_Descripcion] [varchar](255) NULL,
	[BT_NumOrden] [int] NULL,
	[BT_UnidMedida] [varchar](255) NULL,
	[ID] [varchar](255) FOREIGN KEY REFERENCES GUIA_REMISION(ID),
	[ID_Linea] [int] NOT NULL
	primary key ([ID],[ID_Linea])
)

GO

CREATE TABLE [dbo].[TRANSACCION](
	[DocIdentidad_Nro] [varchar](32) NULL,
	[DocIdentidad_Tipo] [varchar](2) NULL,
	[RazonSocial] [varchar](200) NULL,
	[NombreComercial] [varchar](200) NULL,
	[PersonContacto] [varchar](150) NULL,
	[EMail] [varchar](150) NULL,
	[Telefono] [varchar](20) NULL,
	[Telefono_1] [varchar](20) NULL,
	[Web] [varchar](50) NULL,
	[DIR_Pais] [varchar](2) NULL,
	[DIR_Departamento] [varchar](100) NULL,
	[DIR_Provincia] [varchar](100) NULL,
	[DIR_Distrito] [varchar](100) NULL,
	[DIR_Direccion] [varchar](500) NULL,
	[DIR_NomCalle] [varchar](100) NULL,
	[DIR_NroCasa] [varchar](20) NULL,
	[DIR_Ubigeo] [int] NULL,
	[DIR_Urbanizacion] [varchar](30) NULL,
	[SN_DocIdentidad_Nro] [varchar](15) NULL,
	[SN_DocIdentidad_Tipo] [varchar](2) NULL,
	[SN_RazonSocial] [varchar](200) NULL,
	[SN_NombreComercial] [varchar](200) NULL,
	[SN_EMail] [varchar](100) NULL,
	[SN_SegundoNombre] [varchar](50) NULL,
	[SN_DIR_Pais] [varchar](2) NULL,
	[SN_DIR_Departamento] [varchar](100) NULL,
	[SN_DIR_Provincia] [varchar](100) NULL,
	[SN_DIR_Distrito] [varchar](100) NULL,
	[SN_DIR_Direccion] [varchar](500) NULL,
	[SN_DIR_NomCalle] [varchar](100) NULL,
	[SN_DIR_NroCasa] [varchar](20) NULL,
	[SN_DIR_Ubigeo] [int] NULL,
	[SN_DIR_Urbanizacion] [varchar](30) NULL,
	[DOC_Serie] [varchar](4) NULL,
	[DOC_Numero] [varchar](15) NULL,
	[DOC_Id] [varchar](20) NULL,
	[DOC_FechaEmision] [datetime] NULL,
	[DOC_FechaVencimiento] [datetime] NULL,
	[DOC_Dscrpcion] [varchar](50) NULL,
	[DOC_Codigo] [varchar](2) NULL,
	[DOC_MON_Nombre] [varchar](20) NULL,
	[DOC_MON_Codigo] [varchar](3) NULL,
	[DOC_Descuento] [numeric](19, 6) NULL,
	[DOC_MontoTotal] [numeric](19, 6) NULL,
	[DOC_DescuentoTotal] [numeric](19, 6) NULL,
	[DOC_Importe] [numeric](19, 6) NULL,
	[DOC_PorcImpuesto] varchar(100) NULL,
	[ANTICIPO_Id] [varchar](20) NULL,
	[ANTICIPO_Tipo] [varchar](2) NULL,
	[ANTICIPO_Monto] [numeric](19, 6) NULL,
	[ANTCIPO_Tipo_Doc_ID] [varchar](50) NULL,
	[ANTICIPO_Nro_Doc_ID] [varchar](50) NULL,
	[SUNAT_Transact] [varchar](2) NULL,
	[FE_DocEntry] [int] NULL,
	[FE_ObjectType] [varchar](30) NULL,
	[FE_Estado] [varchar](1) NULL,
	[FE_TipoTrans] [varchar](1) NULL,
	[FE_Id] [varchar](20) NOT NULL,
	[FE_DocNum] [int] NULL,
	[FE_FormSAP] [varchar](50) NULL,
	[REFDOC_Tipo] [varchar](2) NULL,
	[REFDOC_Id] [varchar](50) NULL,
	[REFDOC_MotivCode] [varchar](30) NULL,
	[REFDOC_MotivDesc] [varchar](50) NULL,
	[FE_Comentario] [varchar](254) NULL,
	[FE_ErrCod] [varchar](8) NULL,
	[FE_ErrMsj] [varchar](254) NULL,
	[FE_Errores] [int] NULL,
	[FE_MaxSalto] [int] NULL,
	[FE_Saltos] [int] NULL,
	[DOC_CondPago] [varchar](100) NULL,
	[RET_Regimen] [varchar](2) NULL,
	[RET_Tasa] [varchar](4) NULL,
	[Observaciones] [varchar](250) NULL,
	[ImportePagado] [decimal](19,6) NULL,
	[MonedaPagado] [varchar](3) NULL
	primary key ([FE_Id])
) 

GO

CREATE TABLE [dbo].[TRANSACCION_IMPUESTOS](
	[FE_Id] [varchar](20)  FOREIGN KEY REFERENCES TRANSACCION(FE_Id),
	[LineId] [int] NOT NULL,
	[Moneda] [varchar](10) NULL,
	[Monto] [numeric](19, 6) NULL,
	[Porcentaje] [numeric](19, 6) NULL,
	[TipoTributo] [varchar](30) NULL,
	[TipoAfectacion] [varchar](30) NULL,
	[TierRange] [varchar](30) NULL,
	primary key ([FE_Id],[LineId])
)

GO

CREATE TABLE [dbo].[TRANSACCION_BAJA](
	[fecha] [bigint] NOT NULL,
	[ID] [bigint] NOT NULL,
	[serie] [varchar](20) NULL
	primary key ([fecha])
)


GO

CREATE TABLE [dbo].[RESUMENDIARIO_CONFIG](
	[fecha] [datetime] NOT NULL,
	[hora] [varchar](20) NULL
	primary key ([fecha])
)


GO


CREATE TABLE [dbo].[TRANSACCION_LINEAS](
	[FE_Id] [varchar](20)  FOREIGN KEY REFERENCES TRANSACCION(FE_Id),
	[NroOrden] [int] NOT NULL,
	[DSCTO_Porcentaje] [numeric](19, 6) NULL,
	[DSCTO_Monto] [numeric](19, 6) NULL,
	[Descripcion] [varchar](100) NULL,
	[PrecioDscto] [numeric](19, 6) NULL,
	[PrecioIGV] [numeric](19, 6) NULL,
	[TotalLineaSinIGV] [numeric](19, 6) NULL,
	[TotalLineaConIGV] [numeric](19, 6) NULL,
	[PrecioRef_Monto] [numeric](19, 6) NULL,
	[PrecioRef_Codigo] [varchar](2) NULL,
	[Cantidad] [numeric](19, 6) NULL,
	[Unidad] [varchar](100) NULL,
	[UnidadSunat] [varchar](100) NULL,
	[CodArticulo] [varchar](20) NULL
	primary key ([FE_Id],[NroOrden])
)

GO

CREATE TABLE [dbo].[TRANSACCION_LINEAS_BILLREF](
	[FE_Id] [varchar](20) NOT NULL,
	[NroOrden] [int] NOT NULL,
	[LineId] [int] NOT NULL,
	[AdtDocRef_Id] [varchar](50) NULL,
	[AdtDocRef_SchemaId] [varchar](30) NULL,
	[InvDocRef_DocTypeCode] [varchar](10) NULL,
	[InvDocRef_Id] [varchar](5) NULL
	primary key ([FE_Id],[LineId],[NroOrden]),
	foreign key ([FE_Id],[NroOrden]) REFERENCES TRANSACCION_LINEAS(FE_Id,NroOrden)
)

GO

CREATE TABLE [dbo].[TRANSACCION_LINEA_IMPUESTOS](
	[FE_Id] [varchar](20) NOT NULL,
	[LineId] [int] NOT NULL,
	[NroOrden] [int] NOT NULL,
	[Moneda] [varchar](10) NULL,
	[Monto] [numeric](19, 6) NULL,
	[Porcentaje] [numeric](19, 6) NULL,
	[TipoTributo] [varchar](30) NULL,
	[TipoAfectacion] [varchar](30) NULL,
	[TierRange] [varchar](30) NULL
	primary key ([FE_Id],[LineId],[NroOrden]),
	foreign key ([FE_Id],[NroOrden]) REFERENCES TRANSACCION_LINEAS(FE_Id,NroOrden)
)

GO

CREATE TABLE [dbo].[USUARIOCAMPOS](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Nombre] [varchar](500) NULL
	primary key ([Id])
)

GO


CREATE TABLE [dbo].[TRANSACCION_USUCAMPOS](
	[FE_Id] [varchar](20) NOT NULL,
	[USUCMP_Id] [int] NOT NULL,
	[Valor] [varchar](255) NULL
	
	primary key ([FE_Id],[USUCMP_Id]),
	foreign key ([FE_Id]) REFERENCES TRANSACCION(FE_Id),
	foreign key ([USUCMP_Id]) REFERENCES USUARIOCAMPOS(Id),
)

GO

CREATE TABLE [dbo].[TRANSACCION_LINEAS_USUCAMPOS](
	[FE_Id] [varchar](20) NOT NULL,
	[NroOrden] [int] NOT NULL,
	[USUCMP_Id] [int] NOT NULL,
	[Valor] [varchar](255) NULL
	primary key ([FE_Id],[NroOrden],[USUCMP_Id]),
	foreign key ([FE_Id],[NroOrden]) REFERENCES TRANSACCION_LINEAS(FE_Id,NroOrden),
	foreign key ([USUCMP_Id]) REFERENCES USUARIOCAMPOS(Id)
)

GO
	
CREATE TABLE [dbo].[TRANSACCION_ERROR](
	[FE_Id] [varchar](25) NOT NULL,
	[Docentry] [int] NULL,
	[Docnum] [int] NULL,
	[FE_ObjectType] [varchar](15) NULL,
	[FE_FormSAP] [varchar](50) NULL,
	[FE_TipoTrans] [varchar](1) NULL,
	[Err_Codigo] [int] NULL,
	[Err_Mensaje] [nvarchar](255) NULL
	primary key ([FE_Id])
)

GO

CREATE TABLE [dbo].[TRANSACCION_CONTRACTDOCREF](
	[FE_Id] [varchar](20) NOT NULL,
	[USUCMP_Id] [int] NOT NULL,
	[Valor] [varchar](500) NULL
	primary key ([FE_Id],[USUCMP_Id]),
	foreign key ([FE_Id]) REFERENCES TRANSACCION(FE_Id),
	foreign key ([USUCMP_Id]) REFERENCES USUARIOCAMPOS(Id),
)

GO

CREATE TABLE [dbo].[TRANSACCION_TOTALES](
	[FE_Id] [varchar](20) NOT NULL,
	[Id] [varchar](4) NOT NULL,
	[Monto] [numeric](19, 6) NULL,
	[Prcnt] [numeric](19, 6) NULL
	primary key ([FE_Id],[Id]),
	foreign key ([FE_Id]) REFERENCES TRANSACCION(FE_Id)
)

GO	

CREATE TABLE [dbo].[TRANSACCION_PROPIEDADES](
	[FE_Id] [varchar](20) NOT NULL,
	[Id] [varchar](4) NOT NULL,
	[Valor] [varchar](300) NULL
	primary key ([FE_Id],[Id]),
	foreign key ([FE_Id]) REFERENCES TRANSACCION(FE_Id)
)

GO

CREATE TABLE [dbo].[TRANSACCION_RESUMEN](
	[Id_Transaccion] [varchar](25) NOT NULL,
	[Numero_Ruc] [varchar](11) NULL,
	[DocIdentidad_Tipo] [nchar](10) NULL,
	[RazonSocial] [varchar](100) NULL,
	[NombreComercial] [varchar](150) NULL,
	[PersonContacto] [varchar](100) NULL,
	[EMail] [varchar](50) NULL,
	[DIR_Pais] [varchar](50) NULL,
	[DIR_Departamento] [varchar](50) NULL,
	[DIR_Provincia] [varchar](50) NULL,
	[DIR_Distrito] [varchar](50) NULL,
	[DIR_Direccion] [varchar](max) NULL,
	[DIR_NomCalle] [varchar](50) NULL,
	[DIR_NroCasa] [varchar](50) NULL,
	[DIR_Ubigeo] [varchar](50) NULL,
	[Fecha_Emision] [datetime] NULL,
	[Fecha_Generacion] [datetime] NULL,
	[Estado] [varchar](20) NULL,
	[Numero_Ticket] [varchar](50) NULL
	primary key ([Id_Transaccion])
)

GO

CREATE TABLE [dbo].[TRANSACCION_RESUMEN_LINEA](
	[Id_Transaccion] [varchar](25) FOREIGN KEY REFERENCES TRANSACCION_RESUMEN(Id_Transaccion),
	[Id_Linea] [int] NOT NULL,
	[Tipo_Documento] [varchar](2) NULL,
	[Numero_Serie] [varchar](4) NULL,
	[Numero_Correlativo_Inicio] [varchar](8) NULL,
	[Numero_Correlativo_Fin] [varchar](8) NULL,
	[Total_OP_Gravadas] [decimal](17, 2) NULL,
	[Total_OP_Exoneradas] [decimal](17, 2) NULL,
	[Total_OP_Inafectas] [decimal](17, 2) NULL,
	[Importe_Otros_Cargos] [decimal](17, 2) NULL,
	[Total_ISC] [decimal](17, 2) NULL,
	[Tota_IGV] [decimal](17, 2) NULL,
	[Total_Otros_Tributos] [decimal](17, 2) NULL,
	[Importe_Total] [decimal](17, 2) NULL,
	[CodMoneda] [varchar](50) NULL
	primary key ([Id_Linea],[Id_Transaccion])
)


GO

CREATE TABLE [dbo].[TRANSACCION_RESUMEN_LINEA_ANEXO](
	[Id_Transaccion] [varchar](25) FOREIGN KEY REFERENCES TRANSACCION_RESUMEN(Id_Transaccion),
	[Id_Linea] [int] NOT NULL,
	[DocEntry] [varchar](50) NULL,
	[TipoTransaccion] [varchar](5) NULL,
	[SN] [varchar](50) NULL,
	[ObjcType] [varchar](4) NULL,
	[TipoDocumento] [varchar](4) NULL,
	[Serie] [varchar](10) NULL,
	[Correlativo] [varchar](10) NULL
	primary key ([Id_Linea],[Id_Transaccion])
)

GO

CREATE TABLE [dbo].[TRANSACCION_COMPROBANTE_PAGO](
	[FE_Id] [varchar](20) FOREIGN KEY REFERENCES TRANSACCION(FE_Id),
	[NroOrden] [int] NOT NULL,
	[DOC_Tipo] [varchar](2) NULL,
	[DOC_Numero] [varchar](13) NULL,
	[DOC_FechaEmision] [DateTime] NULL,
	[DOC_Importe] [decimal](19, 6) NULL,
	[DOC_Moneda] [varchar](3) NULL,
	[PagoFecha] [DateTime] NULL,
	[PagoNumero] [varchar](9) NULL,
	[PagoImporteSR] [decimal](19, 6) NULL,
	[PagoMoneda] [varchar](3) NULL,
	[CP_Importe] [decimal](19, 6) NULL,
	[CP_Moneda] [varchar](3) NULL,
	[CP_Fecha] [DateTime] NULL,
	[CP_ImporteTotal] [decimal](19, 6) NULL,
	[CP_MonedaMontoNeto] [varchar](3) NULL,
	[TC_MonedaRef] [varchar](3) NULL,
	[TC_MonedaObj] [varchar](3) NULL,
	[TC_Factor] [decimal](19, 6) NULL,
	[TC_Fecha] [DateTime] NULL
	primary key ([FE_Id],[NroOrden])
) 

GO

CREATE TABLE [dbo].[TRANSACCION_DOCREFERS](
	[FE_Id] [varchar](20) FOREIGN KEY REFERENCES TRANSACCION(FE_Id),
	[LineId] [int] NOT NULL,
	[Tipo] [varchar](2) NULL,
	[Id] [varchar](500) NULL
	primary key ([FE_Id],[LineId])
)

GO
