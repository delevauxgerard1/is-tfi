package edu.spring.istfi.servicio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.spring.istfi.model.*;
import edu.spring.istfi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

@Service
public class VentaService {
    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private ComprobanteRepository comprobanteRepository;
    @Autowired
    private CondicionTributariaRepository condicionTributariaRepository;
    @Autowired
    private TipoComprobanteRepository tipoComprobanteRepository;

    public ResponseEntity<String> solicitarToken(@RequestBody String requestBody) {
        try {
            // Convertir el cuerpo de la solicitud a un objeto JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // Extraer los datos necesarios del JSON
            String cardNumber = jsonNode.get("datosTarjeta").get("numero").asText();
            String expirationMonth = jsonNode.get("datosTarjeta").get("vencimiento").get("mes").asText();
            String expirationYear = jsonNode.get("datosTarjeta").get("vencimiento").get("año").asText();
            expirationYear = expirationYear.length() >= 2 ? expirationYear.substring(expirationYear.length() - 2) : expirationYear;
            String securityCode = jsonNode.get("datosTarjeta").get("cvv").asText();
            String cardHolderName = jsonNode.get("datosCliente").get("nombreCliente").asText();
            String identificationType = "dni";
            String identificationNumber = jsonNode.get("datosTarjeta").get("dni").asText();

            // Construir el nuevo JSON con los datos necesarios
            String nuevoJson = "{"
                    + "\"card_number\": \"" + cardNumber + "\", "
                    + "\"card_expiration_month\": \"" + expirationMonth + "\", "
                    + "\"card_expiration_year\": \"" + expirationYear + "\", "
                    + "\"security_code\": \"" + securityCode + "\", "
                    + "\"card_holder_name\": \"" + cardHolderName + "\", "
                    + "\"card_holder_identification\": {"
                    + "  \"type\": \"" + identificationType + "\", "
                    + "  \"number\": \"" + identificationNumber + "\""
                    + "}"
                    + "}";

            // Construir el encabezado para la primera API externa
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            headers1.set("apikey", "b192e4cb99564b84bf5db5550112adea");

            // Construir la entidad HTTP con los datos y el encabezado para la primera API externa
            HttpEntity<String> externalApiRequestEntity1 = new HttpEntity<>(nuevoJson, headers1);

            // URL de la primera API externa
            String externalApiUrl1 = "https://developers.decidir.com/api/v2/tokens";

            // Realizar la petición a la primera API externa
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> externalApiResponseEntity1 = restTemplate.postForEntity(
                    externalApiUrl1,
                    externalApiRequestEntity1,
                    String.class
            );

            // Obtener la respuesta de la primera API
            String responseBody1 = externalApiResponseEntity1.getBody();
            //JsonNode respuestaJson1 = objectMapper.readTree(responseBody1);

            return ResponseEntity.ok(responseBody1);

        } catch (Exception e) {
            e.printStackTrace(); // Manejar las excepciones según tus necesidades
            return ResponseEntity.status(500).body("Error al procesar la solicitud");
        }

    }
    public ResponseEntity<String> confirmarPago(@RequestBody String requestBody, JsonNode respuestaJson1) {
        try {
            //comienza api 2
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            int longitudCodigo = 6;
            byte[] randomBytes = new byte[longitudCodigo];
            new SecureRandom().nextBytes(randomBytes);
            // Convertir los bytes a una cadena alfanumérica usando Base64
            String codigoPago = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

            // Extraer datos de la respuesta de la primera API

            String site_transaction_id = codigoPago;
            String token = respuestaJson1.get("id").asText();
            double amount = Double.parseDouble(jsonNode.get("montoTotal").asText());

            // Construir el nuevo JSON para la segunda API con datos de la respuesta de la primera API
            String nuevoJson2 = "{"
                    + "\"site_transaction_id\": \""+site_transaction_id+"\", "
                    + "\"payment_method_id\": 1, "
                    + "\"token\": \""+token+"\", "
                    + "\"bin\": \"450799\", "
                    + "\"amount\": " + amount + ", "
                    + "\"currency\": \"ARS\", "
                    + "\"installments\": 1, "
                    + "\"description\": \"\", "
                    + "\"payment_type\": \"single\", "
                    + "\"establishment_name\": \"single\", "
                    + "\"sub_payments\": [{"
                    + "    \"site_id\": \"\", "
                    + "    \"amount\": " + amount + ", "
                    + "    \"installments\": null "
                    + "}]"
                    + "}";
            // Construir el encabezado para la segunda API externa
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_JSON);
            headers2.set("apikey", "566f2c897b5e4bfaa0ec2452f5d67f13");

            // Construir la entidad HTTP con los datos y el encabezado para la segunda API externa
            HttpEntity<String> externalApiRequestEntity2 = new HttpEntity<>(nuevoJson2, headers2);

            // URL de la segunda API externa
            String externalApiUrl2 = "https://developers.decidir.com/api/v2/payments";
            RestTemplate restTemplate = new RestTemplate();
            // Realizar la petición a la segunda API externa
            ResponseEntity<String> externalApiResponseEntity2 = restTemplate.postForEntity(
                    externalApiUrl2,
                    externalApiRequestEntity2,
                    String.class
            );

            // Obtener la respuesta de la segunda API y devolverla al frontend
            String responseBody2 = externalApiResponseEntity2.getBody();
            JsonNode respuestaJson2 = objectMapper.readTree(responseBody2);

            return ResponseEntity.ok(responseBody2);

        } catch (Exception e) {
            e.printStackTrace(); // Manejar las excepciones según tus necesidades
            return ResponseEntity.status(500).body("Error al procesar la solicitud");
        }
    }

    public void procesarVenta(JsonNode jsonNode) {
        //monto
        double amount = Double.parseDouble(jsonNode.get("montoTotal").asText());
        //para cliente
        int clienteId = Integer.parseInt(jsonNode.get("datosCliente").get("idCliente").asText());
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
        Cliente cliente = clienteOptional.get();

        //para comprobante

        int condicionTributariaId = Integer.parseInt(jsonNode.get("condicionTributaria").get("id").asText());
        int tipoComprobanteId = Integer.parseInt(jsonNode.get("condicionTributaria").get("tipoComprobante").get("id").asText());
        Optional<CondicionTributaria> condicionTributariaOptional = condicionTributariaRepository.findById(condicionTributariaId);
        CondicionTributaria condicionTributaria = condicionTributariaOptional.get();
        Optional<TipoComprobante> tipoComprobanteOptional = tipoComprobanteRepository.findById(tipoComprobanteId);
        TipoComprobante tipoComprobante = tipoComprobanteOptional.get();
        Comprobante nuevoComprobante = new Comprobante();
        nuevoComprobante.setTipoComprobante(tipoComprobante);
        nuevoComprobante.setCondicionTributaria(condicionTributaria);
        comprobanteRepository.save(nuevoComprobante);
        //Pago
        String tipoPagoStr = jsonNode.get("tipoPago").asText();
        TipoPago tipoPago = TipoPago.fromDescripcion(tipoPagoStr);
        Pago nuevoPago = new Pago();
        nuevoPago.setFecha(LocalDate.now());
        nuevoPago.setMonto(amount);
        nuevoPago.setTipoPago(tipoPago);
        pagoRepository.save(nuevoPago);


        // Crear una nueva venta


        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDate.now());
        nuevaVenta.setTotal(amount);
        nuevaVenta.setCliente(cliente);
        nuevaVenta.setComprobante(nuevoComprobante);
        nuevaVenta.setPago(nuevoPago);
        ventaRepository.save(nuevaVenta);

    }

}