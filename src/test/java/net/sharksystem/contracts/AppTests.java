package net.sharksystem.contracts;

import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPPeer;
import net.sharksystem.asap.apps.testsupport.ASAPTestPeerFS;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class AppTests {

    private static final String YOUR_APP_NAME = "Contracts";
    private static final String YOUR_URI = "contract://data";

    private static final String ALICE = "alice";
    private static final String BOB = "bob";

    private static final int PORT = 7777;

    private static int port = 0;
    static int getPortNumber() {
        if(port == 0) {
            port = PORT;
        } else {
            port++;
        }

        return port;
    }

    @Test
    public void asapTestExample() throws IOException, ASAPException, InterruptedException {
        ///////////////// ALICE //////////////////////////////////////////////////////////////
        // setup mocked peer / asap application and activity in android
        Collection<CharSequence> formats = new ArrayList<>();
        formats.add(YOUR_APP_NAME);

        ASAPTestPeerFS aliceSimplePeer = new ASAPTestPeerFS(ALICE, formats);
        ASAPTestPeerFS bobSimplePeer = new ASAPTestPeerFS(BOB, formats);

        // 1st encounter
        this.scenarioPart1(aliceSimplePeer, bobSimplePeer);

        aliceSimplePeer.startEncounter(getPortNumber(), bobSimplePeer);
        // give your app a moment to process
        Thread.sleep(1000);
        // stop encounter
        bobSimplePeer.stopEncounter(aliceSimplePeer);
        // give your app a moment to process
        Thread.sleep(1000);

        // 2nd encounter
        this.scenarioPart2(aliceSimplePeer, bobSimplePeer);

        aliceSimplePeer.startEncounter(getPortNumber(), bobSimplePeer);
    }

    public void scenarioPart1(ASAPPeer alicePeer, ASAPPeer bobPeer)
            throws IOException, ASAPException, InterruptedException {
        // simulate ASAP first encounter with full ASAP protocol stack and engines
        System.out.println("+++++++++++++++++++ 1st encounter starts soon ++++++++++++++++++++");
        Thread.sleep(50);

        // setup message received listener - this should be replaced with your code - you implement a listener.
        ASAPMessageReceivedListenerExample aliceMessageReceivedListenerExample =
                new ASAPMessageReceivedListenerExample();

        alicePeer.addASAPMessageReceivedListener(YOUR_APP_NAME, aliceMessageReceivedListenerExample);

        // example - this should be produced by your application
        byte[] serializedData = TestUtils.serializeExample(42, "from alice", true);

        alicePeer.sendASAPMessage(YOUR_APP_NAME, YOUR_URI, serializedData);

        ///////////////// BOB //////////////////////////////////////////////////////////////

        // this should be replaced with your code - you must implement a listener.
        ASAPMessageReceivedListenerExample asapMessageReceivedListenerExample =
                new ASAPMessageReceivedListenerExample();

        // register your listener (or that mock) with asap connection mock
        bobPeer.addASAPMessageReceivedListener(YOUR_APP_NAME, asapMessageReceivedListenerExample);

        // bob writes something
        bobPeer.sendASAPMessage(YOUR_APP_NAME, YOUR_URI,
                TestUtils.serializeExample(43, "from bob", false));
        bobPeer.sendASAPMessage(YOUR_APP_NAME, YOUR_URI,
                TestUtils.serializeExample(44, "from bob again", false));


        // give your app a moment to process
        Thread.sleep(500);
    }

    public void scenarioPart2(ASAPPeer alicePeer, ASAPPeer bobPeer)
            throws IOException, ASAPException, InterruptedException {

        // bob writes something
        bobPeer.sendASAPMessage(YOUR_APP_NAME, YOUR_URI,
                TestUtils.serializeExample(43, "third message from bob", false));

        // simulate second encounter
        System.out.println("+++++++++++++++++++ 2nd encounter starts soon ++++++++++++++++++++");
        Thread.sleep(50);
    }

    public void testScenarioResults() {
        // TODO
    }

}
