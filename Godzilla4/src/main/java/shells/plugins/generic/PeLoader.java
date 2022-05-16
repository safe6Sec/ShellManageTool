package shells.plugins.generic;

import com.kichik.pecoff4j.PE;
import com.kichik.pecoff4j.SectionData;
import com.kichik.pecoff4j.SectionHeader;
import com.kichik.pecoff4j.io.PEParser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import util.functions;

public class PeLoader {
   private static int IMAGE_DIRECTORY_ENTRY_BASERELOC = 5;
   private static int IMAGE_DIRECTORY_ENTRY_COM_DESCRIPTOR = 14;
   private static int IMAGE_DIRECTORY_ENTRY_TLS = 9;
   private static int SIZE_64_IMAGE_NT_HEADERS = 264;
   private static int SIZE_86_IMAGE_NT_HEADERS = 248;
   private static int SIZE_IMAGE_SECTION_HEADER = 40;

   public static byte[] peToShellcode(byte[] peBuffer, StringBuilder logBuffer) throws Exception {
      PE pe = PEParser.parse((InputStream)(new ByteArrayInputStream(peBuffer)));
      byte[] _peBuffer = new byte[pe.getOptionalHeader().getSizeOfImage()];

      byte[] newExeBuffer;
      for(int i = 0; i < pe.getSectionTable().getNumberOfSections(); ++i) {
         int index = pe.getSectionTable().getHeader(i).getVirtualAddress();
         SectionData section = pe.getSectionTable().getSection(i);
         if (section != null) {
            newExeBuffer = section.getData();
            System.arraycopy(newExeBuffer, 0, _peBuffer, index, newExeBuffer.length);
         }
      }

      System.arraycopy(peBuffer, 0, _peBuffer, 0, pe.getOptionalHeader().getSizeOfHeaders());
      byte[] _peBuffer = null;
      if (pe.getOptionalHeader().getDataDirectory(IMAGE_DIRECTORY_ENTRY_BASERELOC).getVirtualAddress() == 0) {
         logBuffer.append("[-] The PE must have relocations!\n");
         return null;
      } else if (pe.getOptionalHeader().getDataDirectory(IMAGE_DIRECTORY_ENTRY_COM_DESCRIPTOR).getVirtualAddress() != 0) {
         logBuffer.append("[-] .NET applications are not supported!\n");
         return null;
      } else {
         if (pe.getOptionalHeader().getDataDirectory(IMAGE_DIRECTORY_ENTRY_TLS).getVirtualAddress() != 0) {
            logBuffer.append("[WARNING] This application has TLS callbacks, which are not supported!\n");
         }

         if (pe.getOptionalHeader().getSubsystem() != 2) {
            logBuffer.append("[WARNING] This is a console application! The recommended subsystem is GUI.\n");
         }

         logBuffer.append(String.format("[*] This is a x%d exe\n", pe.is64() ? 64 : 32));
         String stubName = String.format("assets/stub%d.bin", pe.is64() ? 64 : 32);
         InputStream inputStream = PeLoader.class.getResource(stubName).openStream();
         byte[] stub = functions.readInputStreamAutoClose(inputStream);
         newExeBuffer = new byte[_peBuffer.length + stub.length];
         String redir_codeHex = "4D5A4552E8000000005B4883EB09534881C3" + functions.byteArrayToHex(functions.intToBytes(_peBuffer.length)) + "FFD3c3";
         byte[] redir_code = functions.hexToByte(redir_codeHex);
         System.arraycopy(_peBuffer, 0, newExeBuffer, 0, _peBuffer.length);
         System.arraycopy(stub, 0, newExeBuffer, _peBuffer.length, stub.length);
         System.arraycopy(redir_code, 0, newExeBuffer, 0, redir_code.length);

         for(int i = 0; i < pe.getSectionTable().getNumberOfSections(); ++i) {
            int secPtr = pe.getDosHeader().getAddressOfNewExeHeader() + (pe.is64() ? SIZE_64_IMAGE_NT_HEADERS : SIZE_86_IMAGE_NT_HEADERS) + SIZE_IMAGE_SECTION_HEADER * i;
            SectionHeader sectionHeader = pe.getSectionTable().getHeader(i);
            sectionHeader.setVirtualSize(get_virtual_sec_size(pe, i));
            sectionHeader.setSizeOfRawData(sectionHeader.getVirtualSize());
            sectionHeader.setPointerToRawData(sectionHeader.getVirtualAddress());
            byte[] sectionHeaderBytes = encodeSection(sectionHeader);
            System.arraycopy(sectionHeaderBytes, 0, newExeBuffer, secPtr, sectionHeaderBytes.length);
         }

         return newExeBuffer;
      }
   }

   private static byte[] encodeSection(SectionHeader sectionHeader) {
      byte[] sectionBytes = new byte[SIZE_IMAGE_SECTION_HEADER];
      byte[] name = sectionHeader.getName().getBytes();
      ByteBuffer byteBuffer = ByteBuffer.wrap(sectionBytes);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      byteBuffer.put(name);
      byteBuffer.put(new byte[8 - name.length]);
      byteBuffer.putInt(sectionHeader.getVirtualSize());
      byteBuffer.putInt(sectionHeader.getVirtualAddress());
      byteBuffer.putInt(sectionHeader.getSizeOfRawData());
      byteBuffer.putInt(sectionHeader.getPointerToRawData());
      byteBuffer.putInt(sectionHeader.getPointerToRelocations());
      byteBuffer.putInt(sectionHeader.getPointerToLineNumbers());
      byteBuffer.putShort((short)sectionHeader.getNumberOfRelocations());
      byteBuffer.putShort((short)sectionHeader.getNumberOfLineNumbers());
      byteBuffer.putInt(sectionHeader.getCharacteristics());
      return byteBuffer.array();
   }

   private static int get_virtual_sec_size(PE pe, int sectionIndex) {
      int alignment = pe.getOptionalHeader().getSectionAlignment();
      SectionHeader sectionHeader = pe.getSectionTable().getHeader(sectionIndex);
      int vsize = sectionHeader.getVirtualSize();
      int units = vsize / alignment;
      if (vsize % alignment > 0) {
         ++units;
      }

      vsize = units * alignment;
      int image_size = pe.getOptionalHeader().getSizeOfImage();
      if (sectionHeader.getVirtualAddress() + vsize > image_size) {
         vsize = sectionHeader.getVirtualSize();
      }

      return vsize;
   }
}
