package org.gsc.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: kay
 * @Date: 12/11/18 16:29
 * @Description:
 */
@Slf4j
public class Account {

    @Setter
    @Getter
    private String address;

    @Setter
    @Getter
    private String privatekey;

    @Setter
    @Getter
    private long balance;

    public Account() {
    }

    public Account(String address, String privatekey, long balance) {
        this.address = address;
        this.privatekey = privatekey;
        this.balance = balance;
    }
}
