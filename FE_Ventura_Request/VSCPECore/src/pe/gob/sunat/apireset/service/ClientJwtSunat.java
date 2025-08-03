package pe.gob.sunat.apireset.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.joshworks.restclient.http.HttpResponse;
import io.joshworks.restclient.http.Unirest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.dto.hb.BdsMaestras;
import pe.gob.sunat.apireset.model.ResponseDTO;
import pe.gob.sunat.apireset.model.ResponseDTOAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ClientJwtSunat {
    private final Logger logger = Logger.getLogger(ClientJwtSunat.class);

    public ResponseDTO getJwtSunat(BdsMaestras bdsMaestras) throws IOException {


        HttpResponse<String> response = Unirest.post("https://api-seguridad.sunat.gob.pe/v1/clientessol/"+bdsMaestras.getClientId()+"/oauth2/token/")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "TS019e7fc2=014dc399cbcad00473c65166bab99ef2e22263ae15b2a9e259ff0e5d972578fa54549fe8acccb61b76d241060b054cc73beff45ea3")
                .field("grant_type", "password")
                .field("scope", bdsMaestras.getScope())
                .field("client_id", bdsMaestras.getClientId())
                .field("client_secret", bdsMaestras.getClientSecret())
                .field("username", bdsMaestras.getUsuarioGuias())
                .field("password", bdsMaestras.getPasswordGuias())
                .field("", "")
                .asString();

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDTO = new ResponseDTO();
        ResponseDTOAuth responseDTO400 = new ResponseDTOAuth();

        if (response.getStatus() == 400) {
            responseDTO400 = objectMapper.readValue(response.body(), new TypeReference<ResponseDTOAuth>() {
            });
            responseDTO.setResponseDTO400(responseDTO400);
        }else{
            responseDTO = objectMapper.readValue(response.body(), new TypeReference<ResponseDTO>() {
            });
        }
        responseDTO.setStatusCode(response.getStatus());
        return responseDTO;

    }

    public ResponseDTO declareSunat(String documentName, String documentPath, String token) throws IOException {

        String fileTo64 = zipDocumentTo64(documentPath.replace("xml", "zip"));
        String fileTo256Sha = zipDocumentTo265Sha(documentPath.replace("xml", "zip"));
        String nombreArchivo = documentName + ".zip";

        HttpResponse<String> response = Unirest.post("https://api-cpe.sunat.gob.pe/v1/contribuyente/gem/comprobantes/" + documentName)
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .body("{\r\n    \"archivo\" : {\r\n        \"nomArchivo\" : \"" + nombreArchivo + "\",\r\n        \"arcGreZip\" : \"" + fileTo64 + "\",\r\n        \"hashZip\" : \"" + fileTo256Sha + "\"\r\n    }\r\n}")
                .asString();

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDTO = objectMapper.readValue(response.body(), new TypeReference<ResponseDTO>() {
        });
        responseDTO.setStatusCode(response.getStatus());
        return responseDTO;
    }

    public ResponseDTO consult(String numTicket, String token) throws IOException {

        HttpResponse<String> response = Unirest.get("https://api-cpe.sunat.gob.pe/v1/contribuyente/gem/comprobantes/envios/" + numTicket)
                .header("Authorization", "Bearer "+token)
                .asString();
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDTO responseDTO = objectMapper.readValue(response.body(), new TypeReference<ResponseDTO>() {
        });
        responseDTO.setStatusCode(response.getStatus());
        responseDTO.setNumTicket(numTicket);
        return responseDTO;

    }

    public String zipDocumentTo64(String documentPath) {
        File originalFile = new File(documentPath);
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int) originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encodeBase64(bytes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedBase64;
    }

    public String zipDocumentTo265Sha(String documentPath) {
        String checksumSHA256 = "";
        try {
            checksumSHA256 = DigestUtils.sha256Hex(new FileInputStream(documentPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checksumSHA256;
    }
}
