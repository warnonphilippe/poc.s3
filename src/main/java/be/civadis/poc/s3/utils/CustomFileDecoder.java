package be.civadis.poc.s3.utils;

import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Decoder adds compatibility for File Decoder to any other
 * decoder via composition.
 *
 * @author phw
 */
public class CustomFileDecoder implements Decoder {

	private Decoder decoder;

	public CustomFileDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

  @Override
  public Object decode(Response response, Type type) throws IOException {
    if (byte[].class.equals(type)) {
      if (response.status() == 404) return Util.emptyValueOf(type);
      if (response.body() == null) return null;
      return Util.toByteArray(response.body().asInputStream());
    }
    return decoder.decode(response, type);
  }

}
