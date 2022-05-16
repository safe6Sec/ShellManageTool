package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import util.http.Parameter;

public class ParameterOutputStream {
   private ByteArrayOutputStream out;

   public ParameterOutputStream() {
      this(new ByteArrayOutputStream());
   }

   public ParameterOutputStream(ByteArrayOutputStream outputStream) {
      this.out = outputStream;
   }

   public void writeParameter(Parameter parameter) throws IOException {
      parameter.serialize(this.out);
      this.out.write(3);
   }

   public byte[] getBuffer() {
      return this.out.toByteArray();
   }
}
