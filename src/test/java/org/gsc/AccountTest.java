package org.gsc;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.gsc.client.WalletGrpcClient;
import org.gsc.protos.Protocol;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

/**
 * @Auther: kay
 * @Date: 12/11/18 20:12
 * @Description:
 */
@Slf4j
public class AccountTest {

    @Test
    public void getBalance(){
        WalletGrpcClient walletGrpcClient = new WalletGrpcClient("39.105.18.104", 50051);
        Protocol.Account accountBalance = walletGrpcClient.queryAccount(Hex.decode("262daebb11f20b68a2035519a8553b597bb7dbbfa4"));
        System.out.println(accountBalance.toString());
    }
}
