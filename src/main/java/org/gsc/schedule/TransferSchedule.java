package org.gsc.schedule;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.gsc.Entity.Account;
import org.gsc.client.WalletGrpcClient;
import org.gsc.crypto.ECKey;
import org.gsc.protos.Contract;
import org.gsc.protos.Protocol;
import org.gsc.utils.TransactionUtils;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.*;

/**
 * @Auther: kay
 * @Date: 12/13/18 17:08
 * @Description:
 */
@Slf4j
public class TransferSchedule implements Runnable {

    private List<Account> accountList;
    private WalletGrpcClient client;

    public TransferSchedule(WalletGrpcClient walletGrpcClient, List<Account> accountList) {
        this.client = walletGrpcClient;
        this.accountList = accountList;
    }

    public int getRandom() {
        return new Random().nextInt(accountList.size());
    }

    public synchronized Account getRandomAccount() {
        int random = getRandom();
        return accountList.get(random);
    }

    public synchronized Account getBalanceAccount(long amount) {
        int random = getRandom();
        Account account = accountList.get(random);
        while (account.getBalance() <= amount) {
            random++;
            if (random >= accountList.size()) {
                random = (random % accountList.size());
            }
            account = accountList.get(random);
        }
        return account;
    }

    @Override
    public void run() {

        long amount = new Random().nextInt(100);

        Account fromAccount = getBalanceAccount(amount);
        Account toAccount = getRandomAccount();
        while (fromAccount.getAddress().equals(toAccount.getAddress())){
            toAccount = getRandomAccount();
        }


        //String privStr = "ad146374a75310b9666e834ee4ad0866d6f4035967bfc76217c5a495fff9f0d0";
        BigInteger privKey = new BigInteger(fromAccount.getPrivatekey(), 16);
        ECKey ecKey = ECKey.fromPrivate(privKey);

        Contract.TransferContract.Builder transferContract = Contract.TransferContract.newBuilder();
        transferContract.setOwnerAddress(ByteString.copyFrom(ecKey.getAddress()));
        transferContract.setToAddress(ByteString.copyFrom(Hex.decode(toAccount.getAddress())));
        transferContract.setAmount(amount);

        /*Contract.TransferAssetContract.Builder transferAssetContract = Contract.TransferAssetContract.newBuilder();
        transferAssetContract.setOwnerAddress();
        transferAssetContract.setToAddress();
        transferAssetContract.setAssetName();
        transferAssetContract.setAmount(amount);
        Protocol.Transaction assetTransaction = client.createTransferAssetTransaction(transferAssetContract.build());*/

        Protocol.Transaction transaction = client.createTransaction(transferContract.build());
        // System.out.println(transaction.toString());

        transaction = TransactionUtils.signTransaction(transaction, ecKey);

        boolean isSuccessed = client.broadcastTransaction(transaction);
        if (isSuccessed) {
            System.out.println("Transfer success: [from: " + Hex.toHexString(ecKey.getAddress()) + " to: " + toAccount.getAddress() + " amount: " + amount + "]");
            logger.info("Transfer success: [from: " + Hex.toHexString(ecKey.getAddress()) + " to: " + toAccount.getAddress() + " amount: " + amount + "]");
        }else {
            System.out.println("Transfer failure: [from: " + Hex.toHexString(ecKey.getAddress()) + " to: " + toAccount.getAddress() + " amount: " + amount + "]");
            logger.info("Transfer failure: [from: " + Hex.toHexString(ecKey.getAddress()) + " to: " + toAccount.getAddress() + " amount: " + amount + "]");
        }
    }
}
