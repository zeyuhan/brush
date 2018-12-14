package org.gsc.config;

public interface Parameter {

  interface CommonConstant {
    byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address  ,a0 is version
    byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x26;   //a0 + address  ,a0 is version
    String ADD_PRE_FIX_STRING_TESTNET = "a0";
    String ADD_PRE_FIX_STRING_MAINNET = "26";
    int ADDRESS_SIZE = 42;

    long TRANSFER_AMOUNT = 100L;
  }
}
