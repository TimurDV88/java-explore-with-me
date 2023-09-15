package ru.practicum.ewm;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class StatClient {

    protected final RestTemplate rest;

    public StatClient(RestTemplate rest) {

        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(T body) {

        String path = StatEndPoints.POST_RECORD_PATH;

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statServiceResponse;

        try {

            statServiceResponse = rest.exchange(path, HttpMethod.POST, requestEntity, Object.class);

        } catch (HttpStatusCodeException e) {

            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareStatResponse(statServiceResponse);
    }

    protected ResponseEntity<Object> get(String start, String end, String[] uris, boolean unique) {

        String path = StatEndPoints.GET_STAT_PATH;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(path)
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<Object> statServiceResponse;

        try {

            statServiceResponse = rest.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, Object.class);

        } catch (HttpStatusCodeException e) {

            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return statServiceResponse;
    }

    private HttpHeaders defaultHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
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
