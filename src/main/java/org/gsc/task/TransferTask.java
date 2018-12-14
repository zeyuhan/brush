package org.gsc.task;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.gsc.Entity.Account;
import org.gsc.client.WalletGrpcClient;
import org.gsc.config.Args;
import org.gsc.protos.Protocol;
import org.gsc.schedule.TransferSchedule;

import java.util.*;

import org.spongycastle.util.encoders.Hex;

/**
 * @Auther: kay
 * @Date: 12/11/18 19:56
 * @Description:
 */
@Slf4j
public class TransferTask implements Task {

    private List<WalletGrpcClient> clientList = new ArrayList<>();
    private List<Account> accountList = new ArrayList<>();
    private List<Thread> threadList = new ArrayList<>();

    private String ip;
    private int port;
    private int rpcThread;

    public TransferTask() {

        logger.info("TransferTask");
    }

    @Override
    public synchronized void init(Args args) {

        logger.info("TransferTask init");
        this.ip = args.getIp();
        this.port = args.getPort();
        this.rpcThread = args.getThread();

        // init account
        args.getAccounts().forEach(account -> {
            logger.info("add account: " + account.getAddress());
            accountList.add(account);
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("------ refresh Account Cache Task -------");
                accountList.forEach(account -> {
                    System.out.println(account.getAddress());
                    WalletGrpcClient walletGrpcClient = new WalletGrpcClient(ip, port);
                    clientList.add(walletGrpcClient);
                    Protocol.Account accountBalance = walletGrpcClient.queryAccount(ByteString
                            .copyFrom(Hex.decode(account.getAddress())));
                    if (accountBalance != null) {
                        logger.info("balance: " + accountBalance.getBalance());
                        System.out.println("balance: " + accountBalance.getBalance());
                        account.setBalance(accountBalance.getBalance());
                    }
                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 10000 * (accountList.size() + 1));
    }

    @Override
    public synchronized void start() {
        logger.info("Transfer start");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < rpcThread; i++) {
                    WalletGrpcClient walletGrpcClient = new WalletGrpcClient(ip, port);
                    clientList.add(walletGrpcClient);
                    TransferSchedule transferSchedule = new TransferSchedule(walletGrpcClient, accountList);
                    new Thread(transferSchedule).start();
                }
            }
        }, 0, 3000);
    }

    @Override
    public void shutdown() {
        System.out.println("Client list size: " + clientList.size());
        clientList.forEach(client -> {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
