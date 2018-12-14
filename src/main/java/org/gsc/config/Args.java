package org.gsc.config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gsc.Entity.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: kay
 * @Date: 12/11/18 16:21
 * @Description:
 */
@Slf4j
public class Args {
    private static final String DEFAULT_CONFIG_FILE_PATH = "config.conf";
    private static final int THREAD_COUNT = 10;

    private static Args INSTANCE;

    @Getter
    @Setter
    @Parameter(names = {"-c", "--config"}, description = "Config File")
    private String config = "";

    @Getter
    @Parameter(names = {"--thread"})
    private int thread = THREAD_COUNT;

    @Getter
    private String ip;

    @Getter
    private int port;

    @Getter
    private List<Account> accounts;

    @Getter
    private boolean transfer;

    private Args() {

    }

    public static Args getInstance() {
        return INSTANCE;
    }

    public static Args getInstance(String[] args) {
        if (null == INSTANCE) {
            INSTANCE = new Args();
            JCommander.newBuilder().addObject(INSTANCE).build().parse(args);
            INSTANCE.initArgs();
        }
        return INSTANCE;
    }

    public static void initArgs() {
        Config config = Configuration.getByFileName(INSTANCE.config, DEFAULT_CONFIG_FILE_PATH);

        if (config.hasPath("brush.thread")) {
            INSTANCE.thread = config.getInt("brush.thread");
        }

        if (config.hasPath("rpc.ip")) {
            INSTANCE.ip = config.getString("rpc.ip");
        }

        if (config.hasPath("rpc.port")) {
            INSTANCE.port = config.getInt("rpc.port");
        }

        if (config.hasPath("account")) {
            INSTANCE.accounts = getAccountsFromConfig(config);
        }

        if (config.hasPath("brush.transfer")) {
            INSTANCE.transfer = config.getBoolean("brush.transfer");
        }

        logConfig();
    }

    private static List<Account> getAccountsFromConfig(final com.typesafe.config.Config config) {
        return config.getObjectList("account").stream()
                .map(Args::createAccount)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static Account createAccount(final ConfigObject asset) {
        final Account account = new Account();
        account.setAddress(asset.get("address").unwrapped().toString());
        account.setPrivatekey(asset.get("priKey").unwrapped().toString());
        return account;
    }

    private static void logConfig(){
        Args args = getInstance();
        logger.info("************************ Args config ***********************\n");
        logger.info("Port: {}", args.getPort());
        logger.info("Ip: {}", args.getIp());
        logger.info("Find account: {}", args.getAccounts().size());
        logger.info("************************************************************\n");
    }
}
