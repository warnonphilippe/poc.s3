package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.utils.CustomFileDecoder;
import be.civadis.poc.s3.utils.CustomFormEncoder;
import be.civadis.poc.s3.utils.CustomGsonDecoder;
import feign.Contract;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import java.util.Arrays;

public class S3FeignConfiguration {

    private final String URL = "http://localhost:9000";
    private final String ACCESS_KEY = "M63XZJiTCspKzus1";
    private final String SECRET_KEY = "GkXYxPkcVXfHT6SZJhjozv2LnbBvesj6";

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    @Bean
    public Encoder feignEncoder() {
        return new CustomFormEncoder(new GsonEncoder());
    }

    /*
    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new CustomFileDecoder(new CustomGsonDecoder()));
    }
*/

public Decoder feignDecoder() {
    MappingJackson2XmlHttpMessageConverter c = new MappingJackson2XmlHttpMessageConverter();
    ObjectFactory<HttpMessageConverters> objectFactory = () ->new HttpMessageConverters(c);
    return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
}
/*
    @Bean
    public RequestInterceptor getRequestInterceptor() {
        return new BasicAuthRequestInterceptor(ACCESS_KEY, SECRET_KEY);
    }
*/
}
