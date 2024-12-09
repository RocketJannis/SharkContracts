package net.sharksystem.contracts;

import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.SharkTestPeerFS;
import net.sharksystem.SharkUnknownBehaviourException;
import net.sharksystem.asap.*;
import net.sharksystem.asap.apps.testsupport.ASAPTestPeerFS;
import net.sharksystem.asap.crypto.ASAPCryptoAlgorithms;
import net.sharksystem.asap.pki.ASAPCertificate;
import net.sharksystem.pki.CredentialMessage;
import net.sharksystem.pki.SharkCredentialReceivedListener;
import net.sharksystem.pki.SharkPKIComponent;
import net.sharksystem.pki.SharkPKIComponentFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PKITest {

    private static final String ALICE = "alice";
    private static final String BOB = "bob";
    private static final String APP_NAME = "contracts";
    private static final String APP_URI = "contract://data";
    private static final List<CharSequence> supportedFormats = List.of(APP_NAME);

    @Test
    public void testPKI() throws SharkException, IOException, SharkUnknownBehaviourException, InterruptedException {
        ASAPTestPeerFS aliceASAP = new ASAPTestPeerFS(ALICE, supportedFormats);
        ASAPTestPeerFS bobASAP = new ASAPTestPeerFS(BOB, supportedFormats);

        SharkPeer alice = createTestPeer(ALICE);
        SharkPeer bob = createTestPeer(BOB);

        alice.start(aliceASAP);
        bob.start(bobASAP);

        SharkPKIComponent alicePKI = (SharkPKIComponent) alice.getComponent(SharkPKIComponent.class);
        SharkPKIComponent bobPKI = (SharkPKIComponent) bob.getComponent(SharkPKIComponent.class);

        alicePKI.setBehaviour(SharkPKIComponent.BEHAVIOUR_SEND_CREDENTIAL_FIRST_ENCOUNTER, true);
        bobPKI.setBehaviour(SharkPKIComponent.BEHAVIOUR_SEND_CREDENTIAL_FIRST_ENCOUNTER, true);

        // auto accept credentials
        alicePKI.setSharkCredentialReceivedListener(credentialMessage -> {
            System.out.println("PKITEST - RECEIVED CREDENTIALS: " + credentialMessage);
            try {
                alicePKI.acceptAndSignCredential(credentialMessage);
            } catch (IOException | ASAPSecurityException e) {
                throw new RuntimeException(e);
            }
        });
        bobPKI.setSharkCredentialReceivedListener(credentialMessage -> {
            System.out.println("PKITEST - RECEIVED CREDENTIALS: " + credentialMessage);
            try {
                bobPKI.acceptAndSignCredential(credentialMessage);
            } catch (IOException | ASAPSecurityException e) {
                throw new RuntimeException(e);
            }
        });

        // first encounter -> exchange credentials
        System.out.println("PKITEST - START ENCOUNTER");
        aliceASAP.startEncounter(AppTests.getPortNumber(), bobASAP);
        Thread.sleep(1000);

        ASAPCertificate bobsCert = new ArrayList<>(alicePKI.getCertificates()).get(0);
        System.out.println(bobsCert);

        // PART 2 send encrypted message
        String message = "hello world!";
        byte[] encryptedMessage = ASAPCryptoAlgorithms.produceEncryptedMessagePackage(message.getBytes(StandardCharsets.UTF_8), BOB, alicePKI.getASAPKeyStore());
        aliceASAP.sendASAPMessage(APP_NAME, APP_URI, encryptedMessage);

        bobASAP.addASAPMessageReceivedListener(APP_NAME, (asapMessages, s, list) -> {
            // decrypt and print the message
            Iterator<byte[]> it = asapMessages.getMessages();
            while (it.hasNext()){
                try {
                    byte[] receivedMessage = it.next();
                    ASAPCryptoAlgorithms.EncryptedMessagePackage messagePackage = ASAPCryptoAlgorithms.parseEncryptedMessagePackage(receivedMessage);
                    byte[] decryptedMessage = ASAPCryptoAlgorithms.decryptPackage(messagePackage, bobPKI.getASAPKeyStore());
                    System.out.println("PKITEST - RECEIVED MESSAGE: " + new String(decryptedMessage, StandardCharsets.UTF_8));
                } catch (ASAPException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        System.out.println("PKITEST - START ENCOUNTER 2");
        aliceASAP.startEncounter(AppTests.getPortNumber(), bobASAP);
        Thread.sleep(1000);

        alice.stop();
        bob.stop();
    }


    private SharkPeer createTestPeer(String name) throws IOException, SharkException, SharkUnknownBehaviourException {
        SharkPeer sharkPeer = new SharkTestPeerFS(name, name);
        SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
        sharkPeer.addComponent(certificateComponentFactory, SharkPKIComponent.class);

        return sharkPeer;
    }

}
