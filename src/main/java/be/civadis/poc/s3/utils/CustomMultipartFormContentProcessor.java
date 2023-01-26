package be.civadis.poc.s3.utils;

import feign.Request;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.ContentProcessor;
import feign.form.ContentType;
import feign.form.multipart.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class CustomMultipartFormContentProcessor implements ContentProcessor {
    private final List<Writer> writers = new ArrayList<>(6);
    private final Writer defaultPerocessor;

    public CustomMultipartFormContentProcessor(Encoder delegate) {
        this.addWriter(new ByteArrayWriter());
        this.addWriter(new FormDataWriter());
        this.addWriter(new SingleFileWriter());
        this.addWriter(new ManyFilesWriter());
        this.defaultPerocessor = new DelegateWriter(delegate);
    }

    public void process(RequestTemplate template, Charset charset, Map<String, Object> data) {


        try (Output output = new Output(charset)) {
            String boundary = Long.toHexString(System.currentTimeMillis());

            Iterator<Map.Entry<String, Object>> var6 = data.entrySet().iterator();

            while (var6.hasNext()) {
                Map.Entry<String, Object> entry = var6.next();
                Writer writer = this.findApplicableWriter(entry.getValue());
                writer.write(output, boundary, entry.getKey(), entry.getValue());
            }

            output.write("--").write(boundary).write("--").write("\r\n");
            String contentTypeHeaderValue = this.getSupportedContentType().getHeader() + "; charset=" + charset.name() + "; boundary=" + boundary;
            template.header("Content-Type", Collections.emptyList());
            template.header("Content-Type", contentTypeHeaderValue);
            byte[] bytes = output.toByteArray();
            Request.Body body = Request.Body.encoded(bytes, (Charset) null);
            template.body(body);

        } catch (IOException e) {
            throw new EncodeException(e.getMessage(), e);
        }

    }

    public ContentType getSupportedContentType() {
        return ContentType.MULTIPART;
    }

    public final void addWriter(Writer writer) {
        this.writers.add(writer);
    }

    public final List<Writer> getWriters() {
        return Collections.unmodifiableList(this.writers);
    }

    public final void setWriter(int index, Writer writer) {
        this.writers.set(index, writer);
    }

    private Writer findApplicableWriter(Object value) {
        Iterator<Writer> var2 = this.writers.iterator();

        Writer writer;
        do {
            if (!var2.hasNext()) {
                return this.defaultPerocessor;
            }

            writer = var2.next();
        } while (!writer.isApplicable(value));

        return writer;
    }
}
