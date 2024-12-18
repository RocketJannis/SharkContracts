package net.sharksystem.contracts;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.asap.apps.testsupport.ASAPTestPeerFS;
import net.sharksystem.contracts.storage.TemporaryInMemoryStorage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ContractsInitTest {

    @Test
    public void testContractsInit() throws IOException, SharkException, InterruptedException {
        ASAPTestPeerFS aliceASAP = new ASAPTestPeerFS(TestUtils.ALICE, TestUtils.supportedFormats);
        SharkPeer sharkPeer = new SharkTestPeerFS(TestUtils.ALICE, TestUtils.ALICE);

        // Add PKI
        SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
        sharkPeer.addComponent(certificateComponentFactory, SharkPKIComponent.class);
        SharkPKIComponent pki = (SharkPKIComponent) sharkPeer.getComponent(SharkPKIComponent.class);

        // Add contracts
        SharkContractsFactory contractsFactory = new SharkContractsFactory(pki, new TemporaryInMemoryStorage());
        sharkPeer.addComponent(contractsFactory, SharkContracts.class);

        sharkPeer.start(aliceASAP);

        Thread.sleep(1000);

        sharkPeer.stop();
    }

}
