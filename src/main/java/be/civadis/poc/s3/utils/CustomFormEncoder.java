package be.civadis.poc.s3.utils;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.ContentProcessor;
import feign.form.ContentType;
import feign.form.UrlencodedFormContentProcessor;
import feign.form.util.CharsetUtil;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomFormEncoder implements Encoder {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?<=charset=)([\\w\\-]+)");
    private final Encoder delegate;
    private final EnumMap<ContentType, ContentProcessor> processors;

    public CustomFormEncoder() {
        this(new Default());
    }

    public CustomFormEncoder(Encoder delegate) {
        this.delegate = delegate;
        List<ContentProcessor> list = Arrays.asList(new CustomMultipartFormContentProcessor(delegate), new UrlencodedFormContentProcessor());
        this.processors = new EnumMap<>(ContentType.class);
        Iterator<ContentProcessor> var3 = list.iterator();

        while (var3.hasNext()) {
            ContentProcessor processor = var3.next();
            this.processors.put(processor.getSupportedContentType(), processor);
        }

    }

    public void encode(Object object, Type bodyType, RequestTemplate template) {
        String contentTypeValue = this.getContentTypeValue(template.headers());
        ContentType contentType = ContentType.of(contentTypeValue);
        if (MAP_STRING_WILDCARD.equals(bodyType) && this.processors.containsKey(contentType)) {
            Charset charset = this.getCharset(contentTypeValue);
            Map<String, Object> data = (Map) object;

            try {
                this.processors.get(contentType).process(template, charset, data);
            } catch (Exception var9) {
                throw new EncodeException(var9.getMessage());
            }
        } else {
            this.delegate.encode(object, bodyType, template);
        }
    }

    public final ContentProcessor getContentProcessor(ContentType type) {
        return this.processors.get(type);
    }

    private String getContentTypeValue(Map<String, Collection<String>> headers) {
        Iterator<Map.Entry<String, Collection<String>>> var2 = headers.entrySet().iterator();

        while (true) {
            Map.Entry<String, Collection<String>> entry;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                entry = var2.next();
            } while (!(entry.getKey()).equalsIgnoreCase(CONTENT_TYPE_HEADER));

            Iterator<String> var4 = ((Collection) entry.getValue()).iterator();

            while (var4.hasNext()) {
                String contentTypeValue = var4.next();
                if (contentTypeValue != null) {
                    return contentTypeValue;
                }
            }
        }
    }

    private Charset getCharset(String contentTypeValue) {
        Matcher matcher = CHARSET_PATTERN.matcher(contentTypeValue);
        return matcher.find() ? Charset.forName(matcher.group(1)) : CharsetUtil.UTF_8;
    }
}
