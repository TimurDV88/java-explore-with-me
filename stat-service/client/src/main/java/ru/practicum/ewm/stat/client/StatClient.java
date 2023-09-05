package ru.practicum.ewm.stat.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stat.common.StatEndPoints;

import java.util.List;
import java.util.Map;

public class StatClient {

    protected final RestTemplate rest;

    public StatClient(RestTemplate rest) {

        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(T body) {

        String path = StatEndPoints.POST_RECORD_PATH;

        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    protected ResponseEntity<Object> get(String start, String end, String[] uris, boolean unique) {

        String path = StatEndPoints.GET_STAT_PATH;

        Map<String, Object> parameters = Map.of(

                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );

        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    private HttpHeaders defaultHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> shareItServerResponse;

        try {

            if (parameters != null) {
                shareItServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareItServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }

        } catch (HttpStatusCodeException e) {

            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareStatResponse(shareItServerResponse);
    }

    private static ResponseEntity<Object> prepareStatResponse(ResponseEntity<Object> response) {

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {

            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
