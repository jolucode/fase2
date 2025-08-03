package org.ventura.soluciones.sunatws.gr.config.error;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase carga la lista de errores de Sunat en memoria, ademas
 * contiene metodos de extraccion de la informacion de los errores
 * de Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class WSSunatError {

    private final Logger logger = Logger.getLogger(WSSunatError.class);

    /* Identicador del objeto */
    private String identifier;

    /*
     * Map con los errores de Sunat
     */
    private Map<String, String[]> sunatErrorMap;

    private Map<String, String> sunatConnectorErrorMap;

    private static WSSunatError instance = null;

    private boolean isLoaded = false;


    /**
     * Constructor privado basico para evitar la creacion de
     * instancias usando el constructor.
     */
    private WSSunatError() {
    }

    /**
     * Constructor privado para evitar la creacion de instancias
     * usando el constructor.
     *
     * @param identifier Identificador del objeto WSSunatError.
     */
    private WSSunatError(String identifier) {
        this.identifier = identifier;
    } //WSSunatError


    /**
     * Este metodo crea una nueva instancia de la clase WSSunatError.
     *
     * @return Retorna una nueva instancia de la clase WSSunatError.
     */
    public static synchronized WSSunatError newInstance() {
        return new WSSunatError();
    } //newInstance

    /**
     * Este metodo crea una nueva instancia de la clase WSSunatError.
     *
     * @param identifier Identificador del objeto WSSunatError.
     * @return Retorna una nueva instancia de la clase WSSunatError.
     */
    public static synchronized WSSunatError newInstance(String identifier) {
        return new WSSunatError(identifier);
    } //newInstance

    /**
     * Este metodo obtiene la instancia actual de la clase WSSunatError.
     *
     * @return Retorna la instancia actual de la clase WSSunatError.
     */
    public static synchronized WSSunatError getInstance() {
        if (null == instance) {
            instance = new WSSunatError();
        }
        return instance;
    } //getInstance


    /**
     * Este metodo carga la lista de errores de Sunat.
     */
    public void loadSunatErrors() {
        if (logger.isDebugEnabled()) {
            logger.debug("+loadSunatErrors()" + (null != this.identifier ? " [" + this.identifier + "]" : ""));
        }

        /* Creando objeto MAP */
        sunatErrorMap = new HashMap<String, String[]>();

        /*
         * Agregando lista de errores de Sunat al objeto MAP.
         */
        sunatErrorMap.put(ISunatError.ERR_0100_CODE, new String[]{ISunatError.ERR_0100_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0101_CODE, new String[]{ISunatError.ERR_0101_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0102_CODE, new String[]{ISunatError.ERR_0102_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0103_CODE, new String[]{ISunatError.ERR_0103_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0104_CODE, new String[]{ISunatError.ERR_0104_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0105_CODE, new String[]{ISunatError.ERR_0105_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0106_CODE, new String[]{ISunatError.ERR_0106_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0109_CODE, new String[]{ISunatError.ERR_0109_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0110_CODE, new String[]{ISunatError.ERR_0110_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0111_CODE, new String[]{ISunatError.ERR_0111_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0112_CODE, new String[]{ISunatError.ERR_0112_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0113_CODE, new String[]{ISunatError.ERR_0113_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0125_CODE, new String[]{ISunatError.ERR_0125_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0126_CODE, new String[]{ISunatError.ERR_0126_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0127_CODE, new String[]{ISunatError.ERR_0127_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0130_CODE, new String[]{ISunatError.ERR_0130_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0131_CODE, new String[]{ISunatError.ERR_0131_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0132_CODE, new String[]{ISunatError.ERR_0132_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0133_CODE, new String[]{ISunatError.ERR_0133_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0134_CODE, new String[]{ISunatError.ERR_0134_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0135_CODE, new String[]{ISunatError.ERR_0135_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0136_CODE, new String[]{ISunatError.ERR_0136_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0137_CODE, new String[]{ISunatError.ERR_0137_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0138_CODE, new String[]{ISunatError.ERR_0138_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0151_CODE, new String[]{ISunatError.ERR_0151_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0152_CODE, new String[]{ISunatError.ERR_0152_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0153_CODE, new String[]{ISunatError.ERR_0153_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0154_CODE, new String[]{ISunatError.ERR_0154_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0155_CODE, new String[]{ISunatError.ERR_0155_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0156_CODE, new String[]{ISunatError.ERR_0156_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0157_CODE, new String[]{ISunatError.ERR_0157_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0158_CODE, new String[]{ISunatError.ERR_0158_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0159_CODE, new String[]{ISunatError.ERR_0159_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0160_CODE, new String[]{ISunatError.ERR_0160_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0161_CODE, new String[]{ISunatError.ERR_0161_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0200_CODE, new String[]{ISunatError.ERR_0200_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0201_CODE, new String[]{ISunatError.ERR_0201_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0202_CODE, new String[]{ISunatError.ERR_0202_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0203_CODE, new String[]{ISunatError.ERR_0203_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0204_CODE, new String[]{ISunatError.ERR_0204_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0250_CODE, new String[]{ISunatError.ERR_0250_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0251_CODE, new String[]{ISunatError.ERR_0251_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0252_CODE, new String[]{ISunatError.ERR_0252_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0253_CODE, new String[]{ISunatError.ERR_0253_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0300_CODE, new String[]{ISunatError.ERR_0300_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0301_CODE, new String[]{ISunatError.ERR_0301_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0302_CODE, new String[]{ISunatError.ERR_0302_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0303_CODE, new String[]{ISunatError.ERR_0303_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0304_CODE, new String[]{ISunatError.ERR_0304_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0305_CODE, new String[]{ISunatError.ERR_0305_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0306_CODE, new String[]{ISunatError.ERR_0306_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0307_CODE, new String[]{ISunatError.ERR_0307_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0400_CODE, new String[]{ISunatError.ERR_0400_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0401_CODE, new String[]{ISunatError.ERR_0401_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0402_CODE, new String[]{ISunatError.ERR_0402_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_0403_CODE, new String[]{ISunatError.ERR_0403_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_0404_CODE, new String[]{ISunatError.ERR_0404_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_1001_CODE, new String[]{ISunatError.ERR_1001_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1002_CODE, new String[]{ISunatError.ERR_1002_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1003_CODE, new String[]{ISunatError.ERR_1003_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1004_CODE, new String[]{ISunatError.ERR_1004_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1005_CODE, new String[]{ISunatError.ERR_1005_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1006_CODE, new String[]{ISunatError.ERR_1006_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1007_CODE, new String[]{ISunatError.ERR_1007_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1008_CODE, new String[]{ISunatError.ERR_1008_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1009_CODE, new String[]{ISunatError.ERR_1009_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1010_CODE, new String[]{ISunatError.ERR_1010_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1011_CODE, new String[]{ISunatError.ERR_1011_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1012_CODE, new String[]{ISunatError.ERR_1012_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1013_CODE, new String[]{ISunatError.ERR_1013_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1014_CODE, new String[]{ISunatError.ERR_1014_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1015_CODE, new String[]{ISunatError.ERR_1015_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1016_CODE, new String[]{ISunatError.ERR_1016_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1017_CODE, new String[]{ISunatError.ERR_1017_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1018_CODE, new String[]{ISunatError.ERR_1018_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1019_CODE, new String[]{ISunatError.ERR_1019_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1020_CODE, new String[]{ISunatError.ERR_1020_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1021_CODE, new String[]{ISunatError.ERR_1021_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1022_CODE, new String[]{ISunatError.ERR_1022_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1023_CODE, new String[]{ISunatError.ERR_1023_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1024_CODE, new String[]{ISunatError.ERR_1024_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1025_CODE, new String[]{ISunatError.ERR_1025_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1026_CODE, new String[]{ISunatError.ERR_1026_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1027_CODE, new String[]{ISunatError.ERR_1027_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1028_CODE, new String[]{ISunatError.ERR_1028_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1029_CODE, new String[]{ISunatError.ERR_1029_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1030_CODE, new String[]{ISunatError.ERR_1030_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1031_CODE, new String[]{ISunatError.ERR_1031_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1032_CODE, new String[]{ISunatError.ERR_1032_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_1033_CODE, new String[]{ISunatError.ERR_1033_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_1034_CODE, new String[]{ISunatError.ERR_1034_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1035_CODE, new String[]{ISunatError.ERR_1035_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1036_CODE, new String[]{ISunatError.ERR_1036_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1037_CODE, new String[]{ISunatError.ERR_1037_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1038_CODE, new String[]{ISunatError.ERR_1038_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_1039_CODE, new String[]{ISunatError.ERR_1039_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_1040_CODE, new String[]{ISunatError.ERR_1040_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2010_CODE, new String[]{ISunatError.ERR_2010_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2011_CODE, new String[]{ISunatError.ERR_2011_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2012_CODE, new String[]{ISunatError.ERR_2012_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2013_CODE, new String[]{ISunatError.ERR_2013_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2014_CODE, new String[]{ISunatError.ERR_2014_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2015_CODE, new String[]{ISunatError.ERR_2015_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2016_CODE, new String[]{ISunatError.ERR_2016_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2017_CODE, new String[]{ISunatError.ERR_2017_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2018_CODE, new String[]{ISunatError.ERR_2018_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2019_CODE, new String[]{ISunatError.ERR_2019_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2020_CODE, new String[]{ISunatError.ERR_2020_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2021_CODE, new String[]{ISunatError.ERR_2021_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2022_CODE, new String[]{ISunatError.ERR_2022_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2023_CODE, new String[]{ISunatError.ERR_2023_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2024_CODE, new String[]{ISunatError.ERR_2024_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2025_CODE, new String[]{ISunatError.ERR_2025_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2026_CODE, new String[]{ISunatError.ERR_2026_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2027_CODE, new String[]{ISunatError.ERR_2027_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2028_CODE, new String[]{ISunatError.ERR_2028_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2029_CODE, new String[]{ISunatError.ERR_2029_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2030_CODE, new String[]{ISunatError.ERR_2030_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2031_CODE, new String[]{ISunatError.ERR_2031_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2032_CODE, new String[]{ISunatError.ERR_2032_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2033_CODE, new String[]{ISunatError.ERR_2033_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2034_CODE, new String[]{ISunatError.ERR_2034_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2035_CODE, new String[]{ISunatError.ERR_2035_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2036_CODE, new String[]{ISunatError.ERR_2036_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2037_CODE, new String[]{ISunatError.ERR_2037_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2038_CODE, new String[]{ISunatError.ERR_2038_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2039_CODE, new String[]{ISunatError.ERR_2039_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2040_CODE, new String[]{ISunatError.ERR_2040_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2041_CODE, new String[]{ISunatError.ERR_2041_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2042_CODE, new String[]{ISunatError.ERR_2042_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2043_CODE, new String[]{ISunatError.ERR_2043_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2044_CODE, new String[]{ISunatError.ERR_2044_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2045_CODE, new String[]{ISunatError.ERR_2045_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2046_CODE, new String[]{ISunatError.ERR_2046_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2047_CODE, new String[]{ISunatError.ERR_2047_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2048_CODE, new String[]{ISunatError.ERR_2048_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2049_CODE, new String[]{ISunatError.ERR_2049_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2050_CODE, new String[]{ISunatError.ERR_2050_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2051_CODE, new String[]{ISunatError.ERR_2051_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2052_CODE, new String[]{ISunatError.ERR_2052_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2053_CODE, new String[]{ISunatError.ERR_2053_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2054_CODE, new String[]{ISunatError.ERR_2054_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2055_CODE, new String[]{ISunatError.ERR_2055_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2056_CODE, new String[]{ISunatError.ERR_2056_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2057_CODE, new String[]{ISunatError.ERR_2057_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2058_CODE, new String[]{ISunatError.ERR_2058_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2059_CODE, new String[]{ISunatError.ERR_2059_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2060_CODE, new String[]{ISunatError.ERR_2060_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2061_CODE, new String[]{ISunatError.ERR_2061_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2062_CODE, new String[]{ISunatError.ERR_2062_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2063_CODE, new String[]{ISunatError.ERR_2063_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2064_CODE, new String[]{ISunatError.ERR_2064_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2065_CODE, new String[]{ISunatError.ERR_2065_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2066_CODE, new String[]{ISunatError.ERR_2066_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2067_CODE, new String[]{ISunatError.ERR_2067_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2068_CODE, new String[]{ISunatError.ERR_2068_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2069_CODE, new String[]{ISunatError.ERR_2069_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2070_CODE, new String[]{ISunatError.ERR_2070_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2071_CODE, new String[]{ISunatError.ERR_2071_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2072_CODE, new String[]{ISunatError.ERR_2072_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2073_CODE, new String[]{ISunatError.ERR_2073_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2074_CODE, new String[]{ISunatError.ERR_2074_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2075_CODE, new String[]{ISunatError.ERR_2075_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2076_CODE, new String[]{ISunatError.ERR_2076_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2077_CODE, new String[]{ISunatError.ERR_2077_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2078_CODE, new String[]{ISunatError.ERR_2078_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2079_CODE, new String[]{ISunatError.ERR_2079_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2080_CODE, new String[]{ISunatError.ERR_2080_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2081_CODE, new String[]{ISunatError.ERR_2081_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2082_CODE, new String[]{ISunatError.ERR_2082_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2083_CODE, new String[]{ISunatError.ERR_2083_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2084_CODE, new String[]{ISunatError.ERR_2084_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2085_CODE, new String[]{ISunatError.ERR_2085_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2086_CODE, new String[]{ISunatError.ERR_2086_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2087_CODE, new String[]{ISunatError.ERR_2087_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2088_CODE, new String[]{ISunatError.ERR_2088_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2089_CODE, new String[]{ISunatError.ERR_2089_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2090_CODE, new String[]{ISunatError.ERR_2090_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2091_CODE, new String[]{ISunatError.ERR_2091_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2092_CODE, new String[]{ISunatError.ERR_2092_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2093_CODE, new String[]{ISunatError.ERR_2093_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2094_CODE, new String[]{ISunatError.ERR_2094_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2095_CODE, new String[]{ISunatError.ERR_2095_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2096_CODE, new String[]{ISunatError.ERR_2096_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2097_CODE, new String[]{ISunatError.ERR_2097_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2098_CODE, new String[]{ISunatError.ERR_2098_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2099_CODE, new String[]{ISunatError.ERR_2099_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2100_CODE, new String[]{ISunatError.ERR_2100_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2101_CODE, new String[]{ISunatError.ERR_2101_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2102_CODE, new String[]{ISunatError.ERR_2102_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2103_CODE, new String[]{ISunatError.ERR_2103_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2104_CODE, new String[]{ISunatError.ERR_2104_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2105_CODE, new String[]{ISunatError.ERR_2105_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2106_CODE, new String[]{ISunatError.ERR_2106_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2107_CODE, new String[]{ISunatError.ERR_2107_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2108_CODE, new String[]{ISunatError.ERR_2108_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2109_CODE, new String[]{ISunatError.ERR_2109_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2110_CODE, new String[]{ISunatError.ERR_2110_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2111_CODE, new String[]{ISunatError.ERR_2111_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2112_CODE, new String[]{ISunatError.ERR_2112_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2113_CODE, new String[]{ISunatError.ERR_2113_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2114_CODE, new String[]{ISunatError.ERR_2114_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2115_CODE, new String[]{ISunatError.ERR_2115_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2116_CODE, new String[]{ISunatError.ERR_2116_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2117_CODE, new String[]{ISunatError.ERR_2117_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2118_CODE, new String[]{ISunatError.ERR_2118_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2119_CODE, new String[]{ISunatError.ERR_2119_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2120_CODE, new String[]{ISunatError.ERR_2120_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2121_CODE, new String[]{ISunatError.ERR_2121_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2122_CODE, new String[]{ISunatError.ERR_2122_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2123_CODE, new String[]{ISunatError.ERR_2123_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2124_CODE, new String[]{ISunatError.ERR_2124_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2125_CODE, new String[]{ISunatError.ERR_2125_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2126_CODE, new String[]{ISunatError.ERR_2126_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2127_CODE, new String[]{ISunatError.ERR_2127_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2128_CODE, new String[]{ISunatError.ERR_2128_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2129_CODE, new String[]{ISunatError.ERR_2129_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2130_CODE, new String[]{ISunatError.ERR_2130_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2131_CODE, new String[]{ISunatError.ERR_2131_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2132_CODE, new String[]{ISunatError.ERR_2132_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2133_CODE, new String[]{ISunatError.ERR_2133_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2134_CODE, new String[]{ISunatError.ERR_2134_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2135_CODE, new String[]{ISunatError.ERR_2135_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2136_CODE, new String[]{ISunatError.ERR_2136_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2137_CODE, new String[]{ISunatError.ERR_2137_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2138_CODE, new String[]{ISunatError.ERR_2138_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2139_CODE, new String[]{ISunatError.ERR_2139_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2140_CODE, new String[]{ISunatError.ERR_2140_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2141_CODE, new String[]{ISunatError.ERR_2141_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2142_CODE, new String[]{ISunatError.ERR_2142_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2143_CODE, new String[]{ISunatError.ERR_2143_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2144_CODE, new String[]{ISunatError.ERR_2144_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2145_CODE, new String[]{ISunatError.ERR_2145_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2146_CODE, new String[]{ISunatError.ERR_2146_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2147_CODE, new String[]{ISunatError.ERR_2147_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2148_CODE, new String[]{ISunatError.ERR_2148_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2149_CODE, new String[]{ISunatError.ERR_2149_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2150_CODE, new String[]{ISunatError.ERR_2150_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2151_CODE, new String[]{ISunatError.ERR_2151_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2152_CODE, new String[]{ISunatError.ERR_2152_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2153_CODE, new String[]{ISunatError.ERR_2153_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2154_CODE, new String[]{ISunatError.ERR_2154_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2155_CODE, new String[]{ISunatError.ERR_2155_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2156_CODE, new String[]{ISunatError.ERR_2156_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2157_CODE, new String[]{ISunatError.ERR_2157_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2158_CODE, new String[]{ISunatError.ERR_2158_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2159_CODE, new String[]{ISunatError.ERR_2159_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2160_CODE, new String[]{ISunatError.ERR_2160_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2161_CODE, new String[]{ISunatError.ERR_2161_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2162_CODE, new String[]{ISunatError.ERR_2162_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2163_CODE, new String[]{ISunatError.ERR_2163_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2164_CODE, new String[]{ISunatError.ERR_2164_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2165_CODE, new String[]{ISunatError.ERR_2165_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2166_CODE, new String[]{ISunatError.ERR_2166_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2167_CODE, new String[]{ISunatError.ERR_2167_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2168_CODE, new String[]{ISunatError.ERR_2168_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2169_CODE, new String[]{ISunatError.ERR_2169_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2170_CODE, new String[]{ISunatError.ERR_2170_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2171_CODE, new String[]{ISunatError.ERR_2171_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2172_CODE, new String[]{ISunatError.ERR_2172_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2173_CODE, new String[]{ISunatError.ERR_2173_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2174_CODE, new String[]{ISunatError.ERR_2174_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2175_CODE, new String[]{ISunatError.ERR_2175_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2176_CODE, new String[]{ISunatError.ERR_2176_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2177_CODE, new String[]{ISunatError.ERR_2177_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2178_CODE, new String[]{ISunatError.ERR_2178_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2179_CODE, new String[]{ISunatError.ERR_2179_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2180_CODE, new String[]{ISunatError.ERR_2180_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2181_CODE, new String[]{ISunatError.ERR_2181_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2182_CODE, new String[]{ISunatError.ERR_2182_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2183_CODE, new String[]{ISunatError.ERR_2183_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2184_CODE, new String[]{ISunatError.ERR_2184_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2185_CODE, new String[]{ISunatError.ERR_2185_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2186_CODE, new String[]{ISunatError.ERR_2186_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2187_CODE, new String[]{ISunatError.ERR_2187_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2188_CODE, new String[]{ISunatError.ERR_2188_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2189_CODE, new String[]{ISunatError.ERR_2189_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2190_CODE, new String[]{ISunatError.ERR_2190_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2191_CODE, new String[]{ISunatError.ERR_2191_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2192_CODE, new String[]{ISunatError.ERR_2192_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2193_CODE, new String[]{ISunatError.ERR_2193_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2194_CODE, new String[]{ISunatError.ERR_2194_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2195_CODE, new String[]{ISunatError.ERR_2195_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2196_CODE, new String[]{ISunatError.ERR_2196_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2197_CODE, new String[]{ISunatError.ERR_2197_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2198_CODE, new String[]{ISunatError.ERR_2198_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2199_CODE, new String[]{ISunatError.ERR_2199_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2200_CODE, new String[]{ISunatError.ERR_2200_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2201_CODE, new String[]{ISunatError.ERR_2201_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2202_CODE, new String[]{ISunatError.ERR_2202_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2203_CODE, new String[]{ISunatError.ERR_2203_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2204_CODE, new String[]{ISunatError.ERR_2204_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2205_CODE, new String[]{ISunatError.ERR_2205_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2206_CODE, new String[]{ISunatError.ERR_2206_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2207_CODE, new String[]{ISunatError.ERR_2207_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2208_CODE, new String[]{ISunatError.ERR_2208_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2209_CODE, new String[]{ISunatError.ERR_2209_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2210_CODE, new String[]{ISunatError.ERR_2210_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2211_CODE, new String[]{ISunatError.ERR_2211_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2212_CODE, new String[]{ISunatError.ERR_2212_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2213_CODE, new String[]{ISunatError.ERR_2213_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2214_CODE, new String[]{ISunatError.ERR_2214_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2215_CODE, new String[]{ISunatError.ERR_2215_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2216_CODE, new String[]{ISunatError.ERR_2216_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2217_CODE, new String[]{ISunatError.ERR_2217_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2218_CODE, new String[]{ISunatError.ERR_2218_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2219_CODE, new String[]{ISunatError.ERR_2219_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2220_CODE, new String[]{ISunatError.ERR_2220_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2221_CODE, new String[]{ISunatError.ERR_2221_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2222_CODE, new String[]{ISunatError.ERR_2222_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2223_CODE, new String[]{ISunatError.ERR_2223_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2224_CODE, new String[]{ISunatError.ERR_2224_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2225_CODE, new String[]{ISunatError.ERR_2225_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2226_CODE, new String[]{ISunatError.ERR_2226_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2227_CODE, new String[]{ISunatError.ERR_2227_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2228_CODE, new String[]{ISunatError.ERR_2228_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2229_CODE, new String[]{ISunatError.ERR_2229_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2230_CODE, new String[]{ISunatError.ERR_2230_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2231_CODE, new String[]{ISunatError.ERR_2231_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2232_CODE, new String[]{ISunatError.ERR_2232_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2233_CODE, new String[]{ISunatError.ERR_2233_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2234_CODE, new String[]{ISunatError.ERR_2234_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2235_CODE, new String[]{ISunatError.ERR_2235_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2236_CODE, new String[]{ISunatError.ERR_2236_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2237_CODE, new String[]{ISunatError.ERR_2237_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2238_CODE, new String[]{ISunatError.ERR_2238_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2239_CODE, new String[]{ISunatError.ERR_2239_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2240_CODE, new String[]{ISunatError.ERR_2240_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2241_CODE, new String[]{ISunatError.ERR_2241_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2242_CODE, new String[]{ISunatError.ERR_2242_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2243_CODE, new String[]{ISunatError.ERR_2243_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2244_CODE, new String[]{ISunatError.ERR_2244_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2245_CODE, new String[]{ISunatError.ERR_2245_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2246_CODE, new String[]{ISunatError.ERR_2246_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2247_CODE, new String[]{ISunatError.ERR_2247_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2248_CODE, new String[]{ISunatError.ERR_2248_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2249_CODE, new String[]{ISunatError.ERR_2249_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2250_CODE, new String[]{ISunatError.ERR_2250_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2251_CODE, new String[]{ISunatError.ERR_2251_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2252_CODE, new String[]{ISunatError.ERR_2252_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2253_CODE, new String[]{ISunatError.ERR_2253_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2254_CODE, new String[]{ISunatError.ERR_2254_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2255_CODE, new String[]{ISunatError.ERR_2255_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2256_CODE, new String[]{ISunatError.ERR_2256_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2257_CODE, new String[]{ISunatError.ERR_2257_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2258_CODE, new String[]{ISunatError.ERR_2258_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2259_CODE, new String[]{ISunatError.ERR_2259_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2260_CODE, new String[]{ISunatError.ERR_2260_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2261_CODE, new String[]{ISunatError.ERR_2261_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2262_CODE, new String[]{ISunatError.ERR_2262_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2263_CODE, new String[]{ISunatError.ERR_2263_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2264_CODE, new String[]{ISunatError.ERR_2264_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2265_CODE, new String[]{ISunatError.ERR_2265_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2266_CODE, new String[]{ISunatError.ERR_2266_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2267_CODE, new String[]{ISunatError.ERR_2267_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2268_CODE, new String[]{ISunatError.ERR_2268_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2269_CODE, new String[]{ISunatError.ERR_2269_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2270_CODE, new String[]{ISunatError.ERR_2270_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2271_CODE, new String[]{ISunatError.ERR_2271_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2272_CODE, new String[]{ISunatError.ERR_2272_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2273_CODE, new String[]{ISunatError.ERR_2273_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2274_CODE, new String[]{ISunatError.ERR_2274_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2275_CODE, new String[]{ISunatError.ERR_2275_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2276_CODE, new String[]{ISunatError.ERR_2276_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2277_CODE, new String[]{ISunatError.ERR_2277_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2278_CODE, new String[]{ISunatError.ERR_2278_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2279_CODE, new String[]{ISunatError.ERR_2279_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2280_CODE, new String[]{ISunatError.ERR_2280_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2281_CODE, new String[]{ISunatError.ERR_2281_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2282_CODE, new String[]{ISunatError.ERR_2282_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2283_CODE, new String[]{ISunatError.ERR_2283_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2284_CODE, new String[]{ISunatError.ERR_2284_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2285_CODE, new String[]{ISunatError.ERR_2285_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2286_CODE, new String[]{ISunatError.ERR_2286_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2287_CODE, new String[]{ISunatError.ERR_2287_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2288_CODE, new String[]{ISunatError.ERR_2288_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2289_CODE, new String[]{ISunatError.ERR_2289_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2290_CODE, new String[]{ISunatError.ERR_2290_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2291_CODE, new String[]{ISunatError.ERR_2291_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2292_CODE, new String[]{ISunatError.ERR_2292_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2293_CODE, new String[]{ISunatError.ERR_2293_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2294_CODE, new String[]{ISunatError.ERR_2294_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2295_CODE, new String[]{ISunatError.ERR_2295_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2296_CODE, new String[]{ISunatError.ERR_2296_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2297_CODE, new String[]{ISunatError.ERR_2297_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2298_CODE, new String[]{ISunatError.ERR_2298_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2299_CODE, new String[]{ISunatError.ERR_2299_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2300_CODE, new String[]{ISunatError.ERR_2300_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2301_CODE, new String[]{ISunatError.ERR_2301_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2302_CODE, new String[]{ISunatError.ERR_2302_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2303_CODE, new String[]{ISunatError.ERR_2303_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2304_CODE, new String[]{ISunatError.ERR_2304_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2305_CODE, new String[]{ISunatError.ERR_2305_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2306_CODE, new String[]{ISunatError.ERR_2306_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2307_CODE, new String[]{ISunatError.ERR_2307_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2308_CODE, new String[]{ISunatError.ERR_2308_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2309_CODE, new String[]{ISunatError.ERR_2309_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2310_CODE, new String[]{ISunatError.ERR_2310_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2311_CODE, new String[]{ISunatError.ERR_2311_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2312_CODE, new String[]{ISunatError.ERR_2312_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2313_CODE, new String[]{ISunatError.ERR_2313_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2314_CODE, new String[]{ISunatError.ERR_2314_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2315_CODE, new String[]{ISunatError.ERR_2315_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2316_CODE, new String[]{ISunatError.ERR_2316_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2317_CODE, new String[]{ISunatError.ERR_2317_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2318_CODE, new String[]{ISunatError.ERR_2318_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2319_CODE, new String[]{ISunatError.ERR_2319_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2320_CODE, new String[]{ISunatError.ERR_2320_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2321_CODE, new String[]{ISunatError.ERR_2321_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2322_CODE, new String[]{ISunatError.ERR_2322_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2323_CODE, new String[]{ISunatError.ERR_2323_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2324_CODE, new String[]{ISunatError.ERR_2324_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2325_CODE, new String[]{ISunatError.ERR_2325_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2326_CODE, new String[]{ISunatError.ERR_2326_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2327_CODE, new String[]{ISunatError.ERR_2327_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2328_CODE, new String[]{ISunatError.ERR_2328_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2329_CODE, new String[]{ISunatError.ERR_2329_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2330_CODE, new String[]{ISunatError.ERR_2330_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2331_CODE, new String[]{ISunatError.ERR_2331_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2332_CODE, new String[]{ISunatError.ERR_2332_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2333_CODE, new String[]{ISunatError.ERR_2333_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2334_CODE, new String[]{ISunatError.ERR_2334_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2335_CODE, new String[]{ISunatError.ERR_2335_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2336_CODE, new String[]{ISunatError.ERR_2336_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2337_CODE, new String[]{ISunatError.ERR_2337_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2338_CODE, new String[]{ISunatError.ERR_2338_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2339_CODE, new String[]{ISunatError.ERR_2339_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2340_CODE, new String[]{ISunatError.ERR_2340_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2341_CODE, new String[]{ISunatError.ERR_2341_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2342_CODE, new String[]{ISunatError.ERR_2342_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2343_CODE, new String[]{ISunatError.ERR_2343_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2344_CODE, new String[]{ISunatError.ERR_2344_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2345_CODE, new String[]{ISunatError.ERR_2345_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2346_CODE, new String[]{ISunatError.ERR_2346_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2347_CODE, new String[]{ISunatError.ERR_2347_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2348_CODE, new String[]{ISunatError.ERR_2348_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2349_CODE, new String[]{ISunatError.ERR_2349_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2350_CODE, new String[]{ISunatError.ERR_2350_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2351_CODE, new String[]{ISunatError.ERR_2351_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2352_CODE, new String[]{ISunatError.ERR_2352_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2353_CODE, new String[]{ISunatError.ERR_2353_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2354_CODE, new String[]{ISunatError.ERR_2354_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2355_CODE, new String[]{ISunatError.ERR_2355_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2356_CODE, new String[]{ISunatError.ERR_2356_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2357_CODE, new String[]{ISunatError.ERR_2357_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2358_CODE, new String[]{ISunatError.ERR_2358_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2359_CODE, new String[]{ISunatError.ERR_2359_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2360_CODE, new String[]{ISunatError.ERR_2360_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2361_CODE, new String[]{ISunatError.ERR_2361_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2362_CODE, new String[]{ISunatError.ERR_2362_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2363_CODE, new String[]{ISunatError.ERR_2363_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2364_CODE, new String[]{ISunatError.ERR_2364_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2365_CODE, new String[]{ISunatError.ERR_2365_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2366_CODE, new String[]{ISunatError.ERR_2366_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2367_CODE, new String[]{ISunatError.ERR_2367_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2368_CODE, new String[]{ISunatError.ERR_2368_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2369_CODE, new String[]{ISunatError.ERR_2369_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2370_CODE, new String[]{ISunatError.ERR_2370_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2371_CODE, new String[]{ISunatError.ERR_2371_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2372_CODE, new String[]{ISunatError.ERR_2372_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2373_CODE, new String[]{ISunatError.ERR_2373_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2374_CODE, new String[]{ISunatError.ERR_2374_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2375_CODE, new String[]{ISunatError.ERR_2375_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2376_CODE, new String[]{ISunatError.ERR_2376_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2377_CODE, new String[]{ISunatError.ERR_2377_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2378_CODE, new String[]{ISunatError.ERR_2378_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2379_CODE, new String[]{ISunatError.ERR_2379_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2380_CODE, new String[]{ISunatError.ERR_2380_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2381_CODE, new String[]{ISunatError.ERR_2381_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2382_CODE, new String[]{ISunatError.ERR_2382_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2383_CODE, new String[]{ISunatError.ERR_2383_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2384_CODE, new String[]{ISunatError.ERR_2384_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2385_CODE, new String[]{ISunatError.ERR_2385_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2386_CODE, new String[]{ISunatError.ERR_2386_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2387_CODE, new String[]{ISunatError.ERR_2387_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2388_CODE, new String[]{ISunatError.ERR_2388_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2389_CODE, new String[]{ISunatError.ERR_2389_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2390_CODE, new String[]{ISunatError.ERR_2390_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2391_CODE, new String[]{ISunatError.ERR_2391_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2392_CODE, new String[]{ISunatError.ERR_2392_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2393_CODE, new String[]{ISunatError.ERR_2393_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2394_CODE, new String[]{ISunatError.ERR_2394_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2395_CODE, new String[]{ISunatError.ERR_2395_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2396_CODE, new String[]{ISunatError.ERR_2396_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2397_CODE, new String[]{ISunatError.ERR_2397_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2398_CODE, new String[]{ISunatError.ERR_2398_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2399_CODE, new String[]{ISunatError.ERR_2399_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2400_CODE, new String[]{ISunatError.ERR_2400_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2401_CODE, new String[]{ISunatError.ERR_2401_MESSAGE, ISunatError.SUNAT_ERROR_INTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2402_CODE, new String[]{ISunatError.ERR_2402_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2403_CODE, new String[]{ISunatError.ERR_2403_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2404_CODE, new String[]{ISunatError.ERR_2404_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2405_CODE, new String[]{ISunatError.ERR_2405_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2406_CODE, new String[]{ISunatError.ERR_2406_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2407_CODE, new String[]{ISunatError.ERR_2407_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2408_CODE, new String[]{ISunatError.ERR_2408_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2409_CODE, new String[]{ISunatError.ERR_2409_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2410_CODE, new String[]{ISunatError.ERR_2410_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2411_CODE, new String[]{ISunatError.ERR_2411_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2412_CODE, new String[]{ISunatError.ERR_2412_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2413_CODE, new String[]{ISunatError.ERR_2413_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2414_CODE, new String[]{ISunatError.ERR_2414_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2415_CODE, new String[]{ISunatError.ERR_2415_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2416_CODE, new String[]{ISunatError.ERR_2416_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2417_CODE, new String[]{ISunatError.ERR_2417_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2418_CODE, new String[]{ISunatError.ERR_2418_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_2419_CODE, new String[]{ISunatError.ERR_2419_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_2420_CODE, new String[]{ISunatError.ERR_2420_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4000_CODE, new String[]{ISunatError.ERR_4000_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4001_CODE, new String[]{ISunatError.ERR_4001_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4002_CODE, new String[]{ISunatError.ERR_4002_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4003_CODE, new String[]{ISunatError.ERR_4003_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4004_CODE, new String[]{ISunatError.ERR_4004_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4005_CODE, new String[]{ISunatError.ERR_4005_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4006_CODE, new String[]{ISunatError.ERR_4006_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4007_CODE, new String[]{ISunatError.ERR_4007_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4008_CODE, new String[]{ISunatError.ERR_4008_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4009_CODE, new String[]{ISunatError.ERR_4009_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4010_CODE, new String[]{ISunatError.ERR_4010_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4011_CODE, new String[]{ISunatError.ERR_4011_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4012_CODE, new String[]{ISunatError.ERR_4012_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4013_CODE, new String[]{ISunatError.ERR_4013_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4014_CODE, new String[]{ISunatError.ERR_4014_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4015_CODE, new String[]{ISunatError.ERR_4015_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4016_CODE, new String[]{ISunatError.ERR_4016_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4017_CODE, new String[]{ISunatError.ERR_4017_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4018_CODE, new String[]{ISunatError.ERR_4018_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4019_CODE, new String[]{ISunatError.ERR_4019_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4020_CODE, new String[]{ISunatError.ERR_4020_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4021_CODE, new String[]{ISunatError.ERR_4021_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4022_CODE, new String[]{ISunatError.ERR_4022_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4023_CODE, new String[]{ISunatError.ERR_4023_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4024_CODE, new String[]{ISunatError.ERR_4024_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4025_CODE, new String[]{ISunatError.ERR_4025_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4026_CODE, new String[]{ISunatError.ERR_4026_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4027_CODE, new String[]{ISunatError.ERR_4027_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4028_CODE, new String[]{ISunatError.ERR_4028_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4029_CODE, new String[]{ISunatError.ERR_4029_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4030_CODE, new String[]{ISunatError.ERR_4030_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4031_CODE, new String[]{ISunatError.ERR_4031_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4032_CODE, new String[]{ISunatError.ERR_4032_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4033_CODE, new String[]{ISunatError.ERR_4033_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4034_CODE, new String[]{ISunatError.ERR_4034_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4035_CODE, new String[]{ISunatError.ERR_4035_MESSAGE, ISunatError.SUNAT_ERROR_USER});
        sunatErrorMap.put(ISunatError.ERR_4036_CODE, new String[]{ISunatError.ERR_4036_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4037_CODE, new String[]{ISunatError.ERR_4037_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4038_CODE, new String[]{ISunatError.ERR_4038_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4039_CODE, new String[]{ISunatError.ERR_4039_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4040_CODE, new String[]{ISunatError.ERR_4040_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});
        sunatErrorMap.put(ISunatError.ERR_4041_CODE, new String[]{ISunatError.ERR_4041_MESSAGE, ISunatError.SUNAT_ERROR_EXTERNAL});

        if (logger.isDebugEnabled()) {
            logger.debug("-loadSunatErrors()" + (null != this.identifier ? " [" + this.identifier + "]" : ""));
        }
    } //loadSunatErrors

    /**
     * Este metodo carga la lista de mensajes de errores de Sunat.
     */
    public void loadSunatConnectorErrors() {
        if (logger.isDebugEnabled()) {
            logger.debug("+loadSunatConnectorErrors()");
        }
        if (!isLoaded) {
            /* Creando objeto MAP */
            sunatConnectorErrorMap = new HashMap<String, String>();

            /*
             * Agregando los mensajes de error al objeto MAP.
             */
            sunatConnectorErrorMap.put(ISunatError.ERR_0100_CODE, ISunatError.ERR_0100_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0101_CODE, ISunatError.ERR_0101_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0102_CODE, ISunatError.ERR_0102_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0103_CODE, ISunatError.ERR_0103_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0104_CODE, ISunatError.ERR_0104_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0105_CODE, ISunatError.ERR_0105_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0106_CODE, ISunatError.ERR_0106_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0109_CODE, ISunatError.ERR_0109_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0110_CODE, ISunatError.ERR_0110_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0111_CODE, ISunatError.ERR_0111_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0112_CODE, ISunatError.ERR_0112_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0113_CODE, ISunatError.ERR_0113_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0125_CODE, ISunatError.ERR_0125_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0126_CODE, ISunatError.ERR_0126_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0127_CODE, ISunatError.ERR_0127_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0130_CODE, ISunatError.ERR_0130_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0131_CODE, ISunatError.ERR_0131_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0132_CODE, ISunatError.ERR_0132_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0133_CODE, ISunatError.ERR_0133_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0134_CODE, ISunatError.ERR_0134_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0135_CODE, ISunatError.ERR_0135_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0136_CODE, ISunatError.ERR_0136_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0137_CODE, ISunatError.ERR_0137_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0138_CODE, ISunatError.ERR_0138_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0151_CODE, ISunatError.ERR_0151_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0152_CODE, ISunatError.ERR_0152_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0153_CODE, ISunatError.ERR_0153_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0154_CODE, ISunatError.ERR_0154_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0155_CODE, ISunatError.ERR_0155_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0156_CODE, ISunatError.ERR_0156_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0157_CODE, ISunatError.ERR_0157_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0158_CODE, ISunatError.ERR_0158_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0159_CODE, ISunatError.ERR_0159_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0160_CODE, ISunatError.ERR_0160_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0161_CODE, ISunatError.ERR_0161_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0200_CODE, ISunatError.ERR_0200_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0201_CODE, ISunatError.ERR_0201_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0202_CODE, ISunatError.ERR_0202_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0203_CODE, ISunatError.ERR_0203_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0204_CODE, ISunatError.ERR_0204_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0250_CODE, ISunatError.ERR_0250_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0251_CODE, ISunatError.ERR_0251_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0252_CODE, ISunatError.ERR_0252_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0253_CODE, ISunatError.ERR_0253_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0300_CODE, ISunatError.ERR_0300_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0301_CODE, ISunatError.ERR_0301_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0302_CODE, ISunatError.ERR_0302_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0303_CODE, ISunatError.ERR_0303_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0304_CODE, ISunatError.ERR_0304_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0305_CODE, ISunatError.ERR_0305_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0306_CODE, ISunatError.ERR_0306_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0307_CODE, ISunatError.ERR_0307_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0400_CODE, ISunatError.ERR_0400_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0401_CODE, ISunatError.ERR_0401_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0402_CODE, ISunatError.ERR_0402_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0403_CODE, ISunatError.ERR_0403_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_0404_CODE, ISunatError.ERR_0404_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1001_CODE, ISunatError.ERR_1001_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1002_CODE, ISunatError.ERR_1002_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1003_CODE, ISunatError.ERR_1003_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1004_CODE, ISunatError.ERR_1004_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1005_CODE, ISunatError.ERR_1005_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1006_CODE, ISunatError.ERR_1006_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1007_CODE, ISunatError.ERR_1007_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1008_CODE, ISunatError.ERR_1008_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1009_CODE, ISunatError.ERR_1009_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1010_CODE, ISunatError.ERR_1010_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1011_CODE, ISunatError.ERR_1011_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1012_CODE, ISunatError.ERR_1012_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1013_CODE, ISunatError.ERR_1013_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1014_CODE, ISunatError.ERR_1014_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1015_CODE, ISunatError.ERR_1015_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1016_CODE, ISunatError.ERR_1016_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1017_CODE, ISunatError.ERR_1017_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1018_CODE, ISunatError.ERR_1018_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1019_CODE, ISunatError.ERR_1019_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1020_CODE, ISunatError.ERR_1020_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1021_CODE, ISunatError.ERR_1021_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1022_CODE, ISunatError.ERR_1022_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1023_CODE, ISunatError.ERR_1023_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1024_CODE, ISunatError.ERR_1024_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1025_CODE, ISunatError.ERR_1025_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1026_CODE, ISunatError.ERR_1026_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1027_CODE, ISunatError.ERR_1027_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1028_CODE, ISunatError.ERR_1028_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1029_CODE, ISunatError.ERR_1029_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1030_CODE, ISunatError.ERR_1030_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1031_CODE, ISunatError.ERR_1031_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1032_CODE, ISunatError.ERR_1032_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1033_CODE, ISunatError.ERR_1033_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1034_CODE, ISunatError.ERR_1034_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1035_CODE, ISunatError.ERR_1035_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1036_CODE, ISunatError.ERR_1036_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1037_CODE, ISunatError.ERR_1037_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1038_CODE, ISunatError.ERR_1038_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1039_CODE, ISunatError.ERR_1039_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_1040_CODE, ISunatError.ERR_1040_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2010_CODE, ISunatError.ERR_2010_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2011_CODE, ISunatError.ERR_2011_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2012_CODE, ISunatError.ERR_2012_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2013_CODE, ISunatError.ERR_2013_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2014_CODE, ISunatError.ERR_2014_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2015_CODE, ISunatError.ERR_2015_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2016_CODE, ISunatError.ERR_2016_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2017_CODE, ISunatError.ERR_2017_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2018_CODE, ISunatError.ERR_2018_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2019_CODE, ISunatError.ERR_2019_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2020_CODE, ISunatError.ERR_2020_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2021_CODE, ISunatError.ERR_2021_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2022_CODE, ISunatError.ERR_2022_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2023_CODE, ISunatError.ERR_2023_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2024_CODE, ISunatError.ERR_2024_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2025_CODE, ISunatError.ERR_2025_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2026_CODE, ISunatError.ERR_2026_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2027_CODE, ISunatError.ERR_2027_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2028_CODE, ISunatError.ERR_2028_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2029_CODE, ISunatError.ERR_2029_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2030_CODE, ISunatError.ERR_2030_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2031_CODE, ISunatError.ERR_2031_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2032_CODE, ISunatError.ERR_2032_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2033_CODE, ISunatError.ERR_2033_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2034_CODE, ISunatError.ERR_2034_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2035_CODE, ISunatError.ERR_2035_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2036_CODE, ISunatError.ERR_2036_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2037_CODE, ISunatError.ERR_2037_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2038_CODE, ISunatError.ERR_2038_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2039_CODE, ISunatError.ERR_2039_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2040_CODE, ISunatError.ERR_2040_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2041_CODE, ISunatError.ERR_2041_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2042_CODE, ISunatError.ERR_2042_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2043_CODE, ISunatError.ERR_2043_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2044_CODE, ISunatError.ERR_2044_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2045_CODE, ISunatError.ERR_2045_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2046_CODE, ISunatError.ERR_2046_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2047_CODE, ISunatError.ERR_2047_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2048_CODE, ISunatError.ERR_2048_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2049_CODE, ISunatError.ERR_2049_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2050_CODE, ISunatError.ERR_2050_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2051_CODE, ISunatError.ERR_2051_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2052_CODE, ISunatError.ERR_2052_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2053_CODE, ISunatError.ERR_2053_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2054_CODE, ISunatError.ERR_2054_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2055_CODE, ISunatError.ERR_2055_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2056_CODE, ISunatError.ERR_2056_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2057_CODE, ISunatError.ERR_2057_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2058_CODE, ISunatError.ERR_2058_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2059_CODE, ISunatError.ERR_2059_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2060_CODE, ISunatError.ERR_2060_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2061_CODE, ISunatError.ERR_2061_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2062_CODE, ISunatError.ERR_2062_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2063_CODE, ISunatError.ERR_2063_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2064_CODE, ISunatError.ERR_2064_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2065_CODE, ISunatError.ERR_2065_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2066_CODE, ISunatError.ERR_2066_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2067_CODE, ISunatError.ERR_2067_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2068_CODE, ISunatError.ERR_2068_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2069_CODE, ISunatError.ERR_2069_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2070_CODE, ISunatError.ERR_2070_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2071_CODE, ISunatError.ERR_2071_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2072_CODE, ISunatError.ERR_2072_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2073_CODE, ISunatError.ERR_2073_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2074_CODE, ISunatError.ERR_2074_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2075_CODE, ISunatError.ERR_2075_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2076_CODE, ISunatError.ERR_2076_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2077_CODE, ISunatError.ERR_2077_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2078_CODE, ISunatError.ERR_2078_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2079_CODE, ISunatError.ERR_2079_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2080_CODE, ISunatError.ERR_2080_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2081_CODE, ISunatError.ERR_2081_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2082_CODE, ISunatError.ERR_2082_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2083_CODE, ISunatError.ERR_2083_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2084_CODE, ISunatError.ERR_2084_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2085_CODE, ISunatError.ERR_2085_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2086_CODE, ISunatError.ERR_2086_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2087_CODE, ISunatError.ERR_2087_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2088_CODE, ISunatError.ERR_2088_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2089_CODE, ISunatError.ERR_2089_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2090_CODE, ISunatError.ERR_2090_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2091_CODE, ISunatError.ERR_2091_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2092_CODE, ISunatError.ERR_2092_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2093_CODE, ISunatError.ERR_2093_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2094_CODE, ISunatError.ERR_2094_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2095_CODE, ISunatError.ERR_2095_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2096_CODE, ISunatError.ERR_2096_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2097_CODE, ISunatError.ERR_2097_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2098_CODE, ISunatError.ERR_2098_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2099_CODE, ISunatError.ERR_2099_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2100_CODE, ISunatError.ERR_2100_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2101_CODE, ISunatError.ERR_2101_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2102_CODE, ISunatError.ERR_2102_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2103_CODE, ISunatError.ERR_2103_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2104_CODE, ISunatError.ERR_2104_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2105_CODE, ISunatError.ERR_2105_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2106_CODE, ISunatError.ERR_2106_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2107_CODE, ISunatError.ERR_2107_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2108_CODE, ISunatError.ERR_2108_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2109_CODE, ISunatError.ERR_2109_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2110_CODE, ISunatError.ERR_2110_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2111_CODE, ISunatError.ERR_2111_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2112_CODE, ISunatError.ERR_2112_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2113_CODE, ISunatError.ERR_2113_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2114_CODE, ISunatError.ERR_2114_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2115_CODE, ISunatError.ERR_2115_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2116_CODE, ISunatError.ERR_2116_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2117_CODE, ISunatError.ERR_2117_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2118_CODE, ISunatError.ERR_2118_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2119_CODE, ISunatError.ERR_2119_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2120_CODE, ISunatError.ERR_2120_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2121_CODE, ISunatError.ERR_2121_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2122_CODE, ISunatError.ERR_2122_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2123_CODE, ISunatError.ERR_2123_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2124_CODE, ISunatError.ERR_2124_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2125_CODE, ISunatError.ERR_2125_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2126_CODE, ISunatError.ERR_2126_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2127_CODE, ISunatError.ERR_2127_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2128_CODE, ISunatError.ERR_2128_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2129_CODE, ISunatError.ERR_2129_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2130_CODE, ISunatError.ERR_2130_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2131_CODE, ISunatError.ERR_2131_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2132_CODE, ISunatError.ERR_2132_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2133_CODE, ISunatError.ERR_2133_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2134_CODE, ISunatError.ERR_2134_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2135_CODE, ISunatError.ERR_2135_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2136_CODE, ISunatError.ERR_2136_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2137_CODE, ISunatError.ERR_2137_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2138_CODE, ISunatError.ERR_2138_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2139_CODE, ISunatError.ERR_2139_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2140_CODE, ISunatError.ERR_2140_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2141_CODE, ISunatError.ERR_2141_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2142_CODE, ISunatError.ERR_2142_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2143_CODE, ISunatError.ERR_2143_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2144_CODE, ISunatError.ERR_2144_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2145_CODE, ISunatError.ERR_2145_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2146_CODE, ISunatError.ERR_2146_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2147_CODE, ISunatError.ERR_2147_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2148_CODE, ISunatError.ERR_2148_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2149_CODE, ISunatError.ERR_2149_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2150_CODE, ISunatError.ERR_2150_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2151_CODE, ISunatError.ERR_2151_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2152_CODE, ISunatError.ERR_2152_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2153_CODE, ISunatError.ERR_2153_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2154_CODE, ISunatError.ERR_2154_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2155_CODE, ISunatError.ERR_2155_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2156_CODE, ISunatError.ERR_2156_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2157_CODE, ISunatError.ERR_2157_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2158_CODE, ISunatError.ERR_2158_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2159_CODE, ISunatError.ERR_2159_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2160_CODE, ISunatError.ERR_2160_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2161_CODE, ISunatError.ERR_2161_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2162_CODE, ISunatError.ERR_2162_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2163_CODE, ISunatError.ERR_2163_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2164_CODE, ISunatError.ERR_2164_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2165_CODE, ISunatError.ERR_2165_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2166_CODE, ISunatError.ERR_2166_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2167_CODE, ISunatError.ERR_2167_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2168_CODE, ISunatError.ERR_2168_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2169_CODE, ISunatError.ERR_2169_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2170_CODE, ISunatError.ERR_2170_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2171_CODE, ISunatError.ERR_2171_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2172_CODE, ISunatError.ERR_2172_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2173_CODE, ISunatError.ERR_2173_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2174_CODE, ISunatError.ERR_2174_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2175_CODE, ISunatError.ERR_2175_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2176_CODE, ISunatError.ERR_2176_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2177_CODE, ISunatError.ERR_2177_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2178_CODE, ISunatError.ERR_2178_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2179_CODE, ISunatError.ERR_2179_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2180_CODE, ISunatError.ERR_2180_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2181_CODE, ISunatError.ERR_2181_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2182_CODE, ISunatError.ERR_2182_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2183_CODE, ISunatError.ERR_2183_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2184_CODE, ISunatError.ERR_2184_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2185_CODE, ISunatError.ERR_2185_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2186_CODE, ISunatError.ERR_2186_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2187_CODE, ISunatError.ERR_2187_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2188_CODE, ISunatError.ERR_2188_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2189_CODE, ISunatError.ERR_2189_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2190_CODE, ISunatError.ERR_2190_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2191_CODE, ISunatError.ERR_2191_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2192_CODE, ISunatError.ERR_2192_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2193_CODE, ISunatError.ERR_2193_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2194_CODE, ISunatError.ERR_2194_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2195_CODE, ISunatError.ERR_2195_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2196_CODE, ISunatError.ERR_2196_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2197_CODE, ISunatError.ERR_2197_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2198_CODE, ISunatError.ERR_2198_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2199_CODE, ISunatError.ERR_2199_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2200_CODE, ISunatError.ERR_2200_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2201_CODE, ISunatError.ERR_2201_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2202_CODE, ISunatError.ERR_2202_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2203_CODE, ISunatError.ERR_2203_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2204_CODE, ISunatError.ERR_2204_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2205_CODE, ISunatError.ERR_2205_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2206_CODE, ISunatError.ERR_2206_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2207_CODE, ISunatError.ERR_2207_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2208_CODE, ISunatError.ERR_2208_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2209_CODE, ISunatError.ERR_2209_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2210_CODE, ISunatError.ERR_2210_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2211_CODE, ISunatError.ERR_2211_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2212_CODE, ISunatError.ERR_2212_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2213_CODE, ISunatError.ERR_2213_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2214_CODE, ISunatError.ERR_2214_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2215_CODE, ISunatError.ERR_2215_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2216_CODE, ISunatError.ERR_2216_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2217_CODE, ISunatError.ERR_2217_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2218_CODE, ISunatError.ERR_2218_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2219_CODE, ISunatError.ERR_2219_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2220_CODE, ISunatError.ERR_2220_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2221_CODE, ISunatError.ERR_2221_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2222_CODE, ISunatError.ERR_2222_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2223_CODE, ISunatError.ERR_2223_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2224_CODE, ISunatError.ERR_2224_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2225_CODE, ISunatError.ERR_2225_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2226_CODE, ISunatError.ERR_2226_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2227_CODE, ISunatError.ERR_2227_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2228_CODE, ISunatError.ERR_2228_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2229_CODE, ISunatError.ERR_2229_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2230_CODE, ISunatError.ERR_2230_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2231_CODE, ISunatError.ERR_2231_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2232_CODE, ISunatError.ERR_2232_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2233_CODE, ISunatError.ERR_2233_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2234_CODE, ISunatError.ERR_2234_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2235_CODE, ISunatError.ERR_2235_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2236_CODE, ISunatError.ERR_2236_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2237_CODE, ISunatError.ERR_2237_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2238_CODE, ISunatError.ERR_2238_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2239_CODE, ISunatError.ERR_2239_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2240_CODE, ISunatError.ERR_2240_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2241_CODE, ISunatError.ERR_2241_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2242_CODE, ISunatError.ERR_2242_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2243_CODE, ISunatError.ERR_2243_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2244_CODE, ISunatError.ERR_2244_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2245_CODE, ISunatError.ERR_2245_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2246_CODE, ISunatError.ERR_2246_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2247_CODE, ISunatError.ERR_2247_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2248_CODE, ISunatError.ERR_2248_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2249_CODE, ISunatError.ERR_2249_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2250_CODE, ISunatError.ERR_2250_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2251_CODE, ISunatError.ERR_2251_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2252_CODE, ISunatError.ERR_2252_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2253_CODE, ISunatError.ERR_2253_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2254_CODE, ISunatError.ERR_2254_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2255_CODE, ISunatError.ERR_2255_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2256_CODE, ISunatError.ERR_2256_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2257_CODE, ISunatError.ERR_2257_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2258_CODE, ISunatError.ERR_2258_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2259_CODE, ISunatError.ERR_2259_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2260_CODE, ISunatError.ERR_2260_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2261_CODE, ISunatError.ERR_2261_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2262_CODE, ISunatError.ERR_2262_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2263_CODE, ISunatError.ERR_2263_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2264_CODE, ISunatError.ERR_2264_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2265_CODE, ISunatError.ERR_2265_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2266_CODE, ISunatError.ERR_2266_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2267_CODE, ISunatError.ERR_2267_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2268_CODE, ISunatError.ERR_2268_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2269_CODE, ISunatError.ERR_2269_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2270_CODE, ISunatError.ERR_2270_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2271_CODE, ISunatError.ERR_2271_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2272_CODE, ISunatError.ERR_2272_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2273_CODE, ISunatError.ERR_2273_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2274_CODE, ISunatError.ERR_2274_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2275_CODE, ISunatError.ERR_2275_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2276_CODE, ISunatError.ERR_2276_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2277_CODE, ISunatError.ERR_2277_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2278_CODE, ISunatError.ERR_2278_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2279_CODE, ISunatError.ERR_2279_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2280_CODE, ISunatError.ERR_2280_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2281_CODE, ISunatError.ERR_2281_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2282_CODE, ISunatError.ERR_2282_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2283_CODE, ISunatError.ERR_2283_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2284_CODE, ISunatError.ERR_2284_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2285_CODE, ISunatError.ERR_2285_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2286_CODE, ISunatError.ERR_2286_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2287_CODE, ISunatError.ERR_2287_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2288_CODE, ISunatError.ERR_2288_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2289_CODE, ISunatError.ERR_2289_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2290_CODE, ISunatError.ERR_2290_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2291_CODE, ISunatError.ERR_2291_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2292_CODE, ISunatError.ERR_2292_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2293_CODE, ISunatError.ERR_2293_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2294_CODE, ISunatError.ERR_2294_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2295_CODE, ISunatError.ERR_2295_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2296_CODE, ISunatError.ERR_2296_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2297_CODE, ISunatError.ERR_2297_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2298_CODE, ISunatError.ERR_2298_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2299_CODE, ISunatError.ERR_2299_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2300_CODE, ISunatError.ERR_2300_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2301_CODE, ISunatError.ERR_2301_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2302_CODE, ISunatError.ERR_2302_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2303_CODE, ISunatError.ERR_2303_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2304_CODE, ISunatError.ERR_2304_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2305_CODE, ISunatError.ERR_2305_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2306_CODE, ISunatError.ERR_2306_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2307_CODE, ISunatError.ERR_2307_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2308_CODE, ISunatError.ERR_2308_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2309_CODE, ISunatError.ERR_2309_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2310_CODE, ISunatError.ERR_2310_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2311_CODE, ISunatError.ERR_2311_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2312_CODE, ISunatError.ERR_2312_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2313_CODE, ISunatError.ERR_2313_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2314_CODE, ISunatError.ERR_2314_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2315_CODE, ISunatError.ERR_2315_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2316_CODE, ISunatError.ERR_2316_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2317_CODE, ISunatError.ERR_2317_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2318_CODE, ISunatError.ERR_2318_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2319_CODE, ISunatError.ERR_2319_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2320_CODE, ISunatError.ERR_2320_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2321_CODE, ISunatError.ERR_2321_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2322_CODE, ISunatError.ERR_2322_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2323_CODE, ISunatError.ERR_2323_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2324_CODE, ISunatError.ERR_2324_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2325_CODE, ISunatError.ERR_2325_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2326_CODE, ISunatError.ERR_2326_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2327_CODE, ISunatError.ERR_2327_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2328_CODE, ISunatError.ERR_2328_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2329_CODE, ISunatError.ERR_2329_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2330_CODE, ISunatError.ERR_2330_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2331_CODE, ISunatError.ERR_2331_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2332_CODE, ISunatError.ERR_2332_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2333_CODE, ISunatError.ERR_2333_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2334_CODE, ISunatError.ERR_2334_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2335_CODE, ISunatError.ERR_2335_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2336_CODE, ISunatError.ERR_2336_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2337_CODE, ISunatError.ERR_2337_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2338_CODE, ISunatError.ERR_2338_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2339_CODE, ISunatError.ERR_2339_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2340_CODE, ISunatError.ERR_2340_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2341_CODE, ISunatError.ERR_2341_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2342_CODE, ISunatError.ERR_2342_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2343_CODE, ISunatError.ERR_2343_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2344_CODE, ISunatError.ERR_2344_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2345_CODE, ISunatError.ERR_2345_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2346_CODE, ISunatError.ERR_2346_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2347_CODE, ISunatError.ERR_2347_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2348_CODE, ISunatError.ERR_2348_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2349_CODE, ISunatError.ERR_2349_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2350_CODE, ISunatError.ERR_2350_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2351_CODE, ISunatError.ERR_2351_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2352_CODE, ISunatError.ERR_2352_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2353_CODE, ISunatError.ERR_2353_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2354_CODE, ISunatError.ERR_2354_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2355_CODE, ISunatError.ERR_2355_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2356_CODE, ISunatError.ERR_2356_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2357_CODE, ISunatError.ERR_2357_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2358_CODE, ISunatError.ERR_2358_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2359_CODE, ISunatError.ERR_2359_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2360_CODE, ISunatError.ERR_2360_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2361_CODE, ISunatError.ERR_2361_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2362_CODE, ISunatError.ERR_2362_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2363_CODE, ISunatError.ERR_2363_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2364_CODE, ISunatError.ERR_2364_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2365_CODE, ISunatError.ERR_2365_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2366_CODE, ISunatError.ERR_2366_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2367_CODE, ISunatError.ERR_2367_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2368_CODE, ISunatError.ERR_2368_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2369_CODE, ISunatError.ERR_2369_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2370_CODE, ISunatError.ERR_2370_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2371_CODE, ISunatError.ERR_2371_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2372_CODE, ISunatError.ERR_2372_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2373_CODE, ISunatError.ERR_2373_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2374_CODE, ISunatError.ERR_2374_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2375_CODE, ISunatError.ERR_2375_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2376_CODE, ISunatError.ERR_2376_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2377_CODE, ISunatError.ERR_2377_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2378_CODE, ISunatError.ERR_2378_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2379_CODE, ISunatError.ERR_2379_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2380_CODE, ISunatError.ERR_2380_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2381_CODE, ISunatError.ERR_2381_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2382_CODE, ISunatError.ERR_2382_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2383_CODE, ISunatError.ERR_2383_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2384_CODE, ISunatError.ERR_2384_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2385_CODE, ISunatError.ERR_2385_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2386_CODE, ISunatError.ERR_2386_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2387_CODE, ISunatError.ERR_2387_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2388_CODE, ISunatError.ERR_2388_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2389_CODE, ISunatError.ERR_2389_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2390_CODE, ISunatError.ERR_2390_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2391_CODE, ISunatError.ERR_2391_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2392_CODE, ISunatError.ERR_2392_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2393_CODE, ISunatError.ERR_2393_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2394_CODE, ISunatError.ERR_2394_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2395_CODE, ISunatError.ERR_2395_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2396_CODE, ISunatError.ERR_2396_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2397_CODE, ISunatError.ERR_2397_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2398_CODE, ISunatError.ERR_2398_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2399_CODE, ISunatError.ERR_2399_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2400_CODE, ISunatError.ERR_2400_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2401_CODE, ISunatError.ERR_2401_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2402_CODE, ISunatError.ERR_2402_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2403_CODE, ISunatError.ERR_2403_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2404_CODE, ISunatError.ERR_2404_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2405_CODE, ISunatError.ERR_2405_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2406_CODE, ISunatError.ERR_2406_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2407_CODE, ISunatError.ERR_2407_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2408_CODE, ISunatError.ERR_2408_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2409_CODE, ISunatError.ERR_2409_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2410_CODE, ISunatError.ERR_2410_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2411_CODE, ISunatError.ERR_2411_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2412_CODE, ISunatError.ERR_2412_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2413_CODE, ISunatError.ERR_2413_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2414_CODE, ISunatError.ERR_2414_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2415_CODE, ISunatError.ERR_2415_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2416_CODE, ISunatError.ERR_2416_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2417_CODE, ISunatError.ERR_2417_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2418_CODE, ISunatError.ERR_2418_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2419_CODE, ISunatError.ERR_2419_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_2420_CODE, ISunatError.ERR_2420_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4000_CODE, ISunatError.ERR_4000_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4001_CODE, ISunatError.ERR_4001_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4002_CODE, ISunatError.ERR_4002_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4003_CODE, ISunatError.ERR_4003_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4004_CODE, ISunatError.ERR_4004_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4005_CODE, ISunatError.ERR_4005_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4006_CODE, ISunatError.ERR_4006_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4007_CODE, ISunatError.ERR_4007_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4008_CODE, ISunatError.ERR_4008_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4009_CODE, ISunatError.ERR_4009_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4010_CODE, ISunatError.ERR_4010_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4011_CODE, ISunatError.ERR_4011_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4012_CODE, ISunatError.ERR_4012_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4013_CODE, ISunatError.ERR_4013_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4014_CODE, ISunatError.ERR_4014_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4015_CODE, ISunatError.ERR_4015_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4016_CODE, ISunatError.ERR_4016_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4017_CODE, ISunatError.ERR_4017_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4018_CODE, ISunatError.ERR_4018_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4019_CODE, ISunatError.ERR_4019_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4020_CODE, ISunatError.ERR_4020_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4021_CODE, ISunatError.ERR_4021_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4022_CODE, ISunatError.ERR_4022_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4023_CODE, ISunatError.ERR_4023_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4024_CODE, ISunatError.ERR_4024_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4025_CODE, ISunatError.ERR_4025_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4026_CODE, ISunatError.ERR_4026_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4027_CODE, ISunatError.ERR_4027_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4028_CODE, ISunatError.ERR_4028_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4029_CODE, ISunatError.ERR_4029_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4030_CODE, ISunatError.ERR_4030_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4031_CODE, ISunatError.ERR_4031_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4032_CODE, ISunatError.ERR_4032_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4033_CODE, ISunatError.ERR_4033_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4034_CODE, ISunatError.ERR_4034_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4035_CODE, ISunatError.ERR_4035_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4036_CODE, ISunatError.ERR_4036_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4037_CODE, ISunatError.ERR_4037_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4038_CODE, ISunatError.ERR_4038_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4039_CODE, ISunatError.ERR_4039_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4040_CODE, ISunatError.ERR_4040_MESSAGE);
            sunatConnectorErrorMap.put(ISunatError.ERR_4041_CODE, ISunatError.ERR_4041_MESSAGE);

            isLoaded = true;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-loadSunatConnectorErrors()");
        }
    } //loadSunatConnectorErrors

    /**
     * Este metodo extrae la informacion del error (tipo de error e descripcion del error)
     * del objeto MAP sunatErrorMap.
     *
     * @param faultCode Codigo de error de la excepcion de tipo SOAPFaultException.
     * @return Retorna la informacion del error en un array.
     */
    public String[] getSunatErrorInformation(String faultCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("+getSunatErrorInformation() faultCode=" + faultCode);
        }
        faultCode = getSunatFaultCode(faultCode);

        String[] sunatError = null;
        for (Map.Entry<String, String[]> entry : sunatErrorMap.entrySet()) {
            String key = entry.getKey();
            if (key.equals(faultCode.toString())) {
                sunatError = entry.getValue();
                break;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getSunatErrorInformation() sunatError: " + sunatError);
        }
        return sunatError;
    } //getSunatError

    /**
     * Este metodo extrae la informacion del error.
     *
     * @param faultCode Codigo de error de la excepcion de tipo SOAPFaultException.
     * @return Retorna la informacion del error en un array.
     */
    public String getSunatConnectorErrorInfo(String faultCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("+getSunatConnectorErrorInfo() faultCode=" + faultCode);
        }
        faultCode = getSunatFaultCode(faultCode);

        String sunatError = null;
        for (Map.Entry<String, String> entry : sunatConnectorErrorMap.entrySet()) {
            String key = entry.getKey();
            if (key.equals(faultCode.toString())) {
                sunatError = entry.getValue();
                break;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getSunatConnectorErrorInfo() sunatError: " + sunatError);
        }
        return sunatError;
    } //getSunatError

    /**
     * Este metodo extrae una sub cadena del codigo fault de la excepcion del
     * servicio web de Sunat.
     *
     * @param faultCode Codigo fault de la excepcion del servicio web de Sunat.
     * @return Retorna una sub cadena del codigo fault.
     */
    public String getSunatFaultCode(String faultCode) {
        /*
         * Verificando si el codigo de error esta con formato.
         *
         * Manual del Programador - Sunat (C3)
         * 	- soap-env:Server.0835
         * 	- soap-env:Client.0835
         */
        if (faultCode.contains(".")) {
            faultCode = faultCode.substring(faultCode.indexOf(".") + 1, faultCode.length());
        }

        return faultCode;
    } //getSunatFaultCode

} //WSSunatError
