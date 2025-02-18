package net.sharksystem.contracts;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPSecurityException;
import net.sharksystem.asap.apps.testsupport.ASAPTestPeerFS;
import net.sharksystem.asap.pki.ASAPCertificate;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ContractsSignTest {

    @BeforeEach
    public void cleanDirectories() throws IOException {
        deleteDir("alice");
        deleteDir("bob");
        deleteDir("testPeerFS");
    }

    private void deleteDir(String dir) throws IOException {
        Path pathToBeDeleted = Paths.get(dir);
        if(!pathToBeDeleted.toFile().exists()) return;

        try (Stream<Path> paths = Files.walk(pathToBeDeleted)) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @Test
    public void testContractsSignUnencrypted() throws SharkException, SharkUnknownBehaviourException, IOException, NoSuchAlgorithmException, InterruptedException {
        testContractsSign(false);
    }

    @Test
    public void testContractsSignEncrypted() throws SharkException, SharkUnknownBehaviourException, IOException, NoSuchAlgorithmException, InterruptedException {
        testContractsSign(true);
    }

    private void testContractsSign(boolean encrypted) throws IOException, SharkException, InterruptedException, SharkUnknownBehaviourException, NoSuchAlgorithmException {
        // Init ASAP/Shark Setup with SharkContracts
        ASAPTestPeerFS aliceASAP = new ASAPTestPeerFS(TestUtils.ALICE, TestUtils.supportedFormats);
        ASAPTestPeerFS bobASAP = new ASAPTestPeerFS(TestUtils.BOB, TestUtils.supportedFormats);

        SharkPeer alicePeer = getPreparedPeer(TestUtils.ALICE);
        SharkPeer bobPeer = getPreparedPeer(TestUtils.BOB);

        alicePeer.start(aliceASAP);
        bobPeer.start(bobASAP);

        SharkContracts aliceContracts = (SharkContracts) alicePeer.getComponent(SharkContracts.class);
        SharkContracts bobContracts = (SharkContracts) bobPeer.getComponent(SharkContracts.class);

        SharkPKIComponent alicePKI = (SharkPKIComponent) alicePeer.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) bobPeer.getComponent(SharkPKIComponent.class);

        autoAcceptCerts(alicePKI);
        autoAcceptCerts(bobPKI);

        // Encounter so both parties know each other
        encounter(aliceASAP, bobASAP);

        // Create contract using alice
        List<String> knownPeers = aliceContracts.getKnownPeers();
        Assertions.assertEquals(1, knownPeers.size());
        Assertions.assertEquals(TestUtils.BOB, knownPeers.get(0));
        byte[] testContent = "Hello world!".getBytes(StandardCharsets.UTF_8);
        Assertions.assertEquals(0, aliceContracts.listContracts().size());
        aliceContracts.createContract(testContent, aliceContracts.getKnownPeers(), encrypted);
        Assertions.assertEquals(1, aliceContracts.listContracts().size());

        // Encounter so bob knows the contract
        encounter(aliceASAP, bobASAP);

        // Sign contract using bob
        Assertions.assertEquals(1, bobContracts.listContracts().size());
        Contract contract = bobContracts.listContracts().get(0);
        Assertions.assertArrayEquals(testContent, contract.getContent());
        bobContracts.signContract(contract);

        // Encounter so alice knows the signature
        encounter(aliceASAP, bobASAP);

        // Check if alice received the signature
        Contract contract2 = aliceContracts.listContracts().get(0);
        List<ContractSignature> signatures = aliceContracts.listSignatures(contract2);
        Assertions.assertFalse(signatures.isEmpty());
        Assertions.assertTrue(aliceContracts.verifySignature(signatures.get(0)));
        Assertions.assertTrue(aliceContracts.isSignedByAllParties(contract2));

        alicePeer.stop();
        bobPeer.stop();
    }

    private void encounter(ASAPTestPeerFS peer1, ASAPTestPeerFS peer2) throws IOException, InterruptedException {
        peer1.startEncounter(AppTests.getPortNumber(), peer2);
        Thread.sleep(1000);
        peer1.stopEncounter(peer2);
        Thread.sleep(500);
        peer2.startEncounter(AppTests.getPortNumber(), peer1);
        Thread.sleep(1000);
        peer2.stopEncounter(peer1);
        Thread.sleep(500);
    }

    private SharkPeer getPreparedPeer(String name) throws SharkException {
        SharkPeer peer = new SharkTestPeerFS(name, name);

        // Add PKI
        SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
        peer.addComponent(certificateComponentFactory, SharkPKIComponent.class);
        SharkPKIComponent pki = (SharkPKIComponent) peer.getComponent(SharkPKIComponent.class);

        // Add contracts
        SharkContractsFactory contractsFactory = new SharkContractsFactory(pki, new TemporaryInMemoryStorage());
        peer.addComponent(contractsFactory, SharkContracts.class);

        return peer;
    }

    private void autoAcceptCerts(SharkPKIComponent pki) throws SharkUnknownBehaviourException, IOException, ASAPException {
        pki.setBehaviour(SharkPKIComponent.BEHAVIOUR_SEND_CREDENTIAL_FIRST_ENCOUNTER, true);
        // auto accept credentials
        pki.setSharkCredentialReceivedListener(credentialMessage -> {
            System.out.println("ContractsSignTest - RECEIVED CREDENTIALS: " + credentialMessage);
            try {
                pki.acceptAndSignCredential(credentialMessage);
            } catch (IOException | ASAPSecurityException e) {
                throw new RuntimeException(e);
            }
        });
    }

}