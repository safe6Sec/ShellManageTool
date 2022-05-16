package util;

class InvalidKeyException extends RuntimeException {
   private static final long serialVersionUID = -2412232436238451574L;

   public InvalidKeyException(String message) {
      super(message);
   }
}
