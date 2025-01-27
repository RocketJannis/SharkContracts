package net.sharksystem.contracts;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.apps.testsupport.ASAPTestPeerFS;
import net.sharksystem.contracts.storage.TemporaryInMemoryStorage;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ContractsSignSimpleTest {

    @Test
    void testContractsSign() throws IOException, SharkException, NoSuchAlgorithmException {
        // Init ASAP/Shark Setup with SharkContracts
        ASAPTestPeerFS asap = new ASAPTestPeerFS(TestUtils.ALICE, TestUtils.supportedFormats);
        SharkPeer peer = new SharkTestPeerFS(TestUtils.ALICE, TestUtils.ALICE);

        // Add PKI
        SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
        peer.addComponent(certificateComponentFactory, SharkPKIComponent.class);
        SharkPKIComponent pki = (SharkPKIComponent) peer.getComponent(SharkPKIComponent.class);

        // Add contracts
        SharkContractsFactory contractsFactory = new SharkContractsFactory(pki, new TemporaryInMemoryStorage());
        peer.addComponent(contractsFactory, SharkContracts.class);
        peer.start(asap);

        SharkContracts contracts = (SharkContracts) peer.getComponent(SharkContracts.class);

        // Create contract
        byte[] testContent = "Hello world2!".getBytes(StandardCharsets.UTF_8);
        Assertions.assertEquals(0, contracts.listContracts().size());
        contracts.createContract(testContent, new ArrayList<>(), false);
        Assertions.assertEquals(1, contracts.listContracts().size());

        // Check if contract is valid
        Contract contract2 = contracts.listContracts().get(0);
        Assertions.assertTrue(contracts.isSignedByAllParties(contract2));
    }


}