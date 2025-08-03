CREATE PROCEDURE [dbo].[bpvs_FE_Correlativo_Baja]
 @Ultimo VARCHAR(20) OUTPUT
as
DECLARE @DateTimeInt BIGINT = convert(bigint,replace(replace(replace(replace(convert(char(23), getdate(), 112),'-',''),':',''),'.',''),' ',''))
DECLARE @Max BIGINT
select @Max=MAX(fecha) from TRANSACCION_BAJA  
DECLARE @id BIGINT = (select ISNULL(MAX(ID),0)+1 from TRANSACCION_BAJA where fecha=@Max)
DECLARE @serie varchar(50)
DECLARE @filas int =(select count(fecha) from TRANSACCION_BAJA)
if(@filas=0)
begin
select @serie = 'RA' + '-' + cast(@DateTimeInt as varchar) + '-' + RIGHT('00000' + Ltrim(Rtrim(1)),5)
INSERT INTO TRANSACCION_BAJA (fecha,ID,serie) VALUES(@DateTimeInt,1,@serie)
select @Ultimo=serie from TRANSACCION_BAJA WHERE fecha=@DateTimeInt
end
if(@Max!=@DateTimeInt)
begin
select @serie = 'RA' + '-' + cast(@DateTimeInt as varchar) + '-' + RIGHT('00000' + Ltrim(Rtrim(1)),5)
INSERT INTO TRANSACCION_BAJA (fecha,ID,serie) VALUES(@DateTimeInt,1,@serie)
select @Ultimo=serie from TRANSACCION_BAJA WHERE fecha=@DateTimeInt
end
else
begin
select @serie = 'RA' + '-' + cast(@DateTimeInt as varchar) + '-' + RIGHT('00000' + Ltrim(Rtrim(@id)),5)
UPDATE TRANSACCION_BAJA SET ID=@id, serie=@serie where fecha=@DateTimeInt
select @Max=MAX(fecha) from TRANSACCION_BAJA  
select @Ultimo=serie from TRANSACCION_BAJA WHERE fecha=@Max
end

GO