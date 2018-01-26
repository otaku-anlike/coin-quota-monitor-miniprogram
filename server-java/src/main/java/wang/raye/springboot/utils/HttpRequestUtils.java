package wang.raye.springboot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

@Component("httpRequestUtils")
public class HttpRequestUtils {

    @Autowired
    private RestOperations restOperations;

    /**
     * @param url 请求url
     * @param method HttpMethod
     * @param params maybe null
     * @return 请求结果
     */
    public Object request(String url, HttpMethod method, MultiValueMap<String, String> params) {

//        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext()
//                .getAuthentication();
//        OAuth2AuthenticationDetails oauthDetail = (OAuth2AuthenticationDetails) auth.getDetails();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", oauthDetail.getTokenType() + " " + oauthDetail.getTokenValue());
        headers.set("Accept", "application/json");

//        params.add("cid", "psmis_admin");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(params);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<Object> response = restOperations.exchange(
                builder.build().encode().toUri(),
                method,
                entity,
                Object.class);

        return response;
    }

    /**
     * @param url 请求url
     * @param params maybe null
     * @return 请求结果
     */
    public String get(String url,  MultiValueMap<String, String> params) {
        url = url + "?text=={text}&desp={desp}";
        String response = restOperations.getForObject(url, String.class, params);
        return response;
    }
}
