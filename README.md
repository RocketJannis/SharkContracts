# SharkContracts

This library uses [ASAP](https://github.com/SharedKnowledge/ASAPJava) and [SharkPKI](https://github.com/SharedKnowledge/SharkPKI) to realize digital contracts and signatures.

## Usage

Examples for the usage of the library can be seen in the [ContractsSignSimpleTest](https://github.com/RocketJannis/SharkContracts/blob/master/src/test/java/net/sharksystem/contracts/ContractsSignSimpleTest.java), [ContractsSignTest](https://github.com/RocketJannis/SharkContracts/blob/master/src/test/java/net/sharksystem/contracts/ContractsSignTest.java) or the [sample android app](https://github.com/RocketJannis/SharkContractsSample).

The javadoc of [SharkContracts](https://github.com/RocketJannis/SharkContracts/blob/master/src/main/java/net/sharksystem/contracts/SharkContracts.java) explains the available methods in detail.

First, instantiate the component:

```java
// Init ASAP/Shark Setup with SharkContracts
ASAPTestPeerFS asap = new ASAPTestPeerFS(name);
SharkPeer peer = new SharkTestPeerFS(name, name);

// Add PKI
SharkPKIComponentFactory certificateComponentFactory = new SharkPKIComponentFactory();
peer.addComponent(certificateComponentFactory, SharkPKIComponent.class);
SharkPKIComponent pki = (SharkPKIComponent) peer.getComponent(SharkPKIComponent.class);

// Add contracts
SharkContractsFactory contractsFactory = new SharkContractsFactory(pki, new TemporaryInMemoryStorage());
peer.addComponent(contractsFactory, SharkContracts.class);

// Start peer
peer.start(asap);

SharkContracts contracts = (SharkContracts) peer.getComponent(SharkContracts.class);
```

With the instance of SharkContracts, we can create and sign contracts. If you created a contract, it doesn't have to be signed.

```java
// Create contract
byte[] testContent = "Hello world!".getBytes(StandardCharsets.UTF_8);
contracts.createContract(testContent, new ArrayList<>(), false);

// Sign contract
Contract incomingContract = ...;
contracts.signContract(contract);
```

Register for incoming contracts and signatures:

```java
contracts.registerListener(new ContractsListener() {
    @Override
    public void onContractReceived(Contract contract) {
        // do something, e.g. sign it
        contracts.signContract(contract);
    }

    @Override
    public void onSignatureReceived(ContractSignature signature) {
        // handle signature
    }
});
```

## Build jar

To build a jar, you can execute:

```bash
mvn package
```

The jar-file will be inside the `target` folder.

## License

Copyright (c) 2025 Jannis Scheibe

This library is distributed under the LPGLv3.0. See the `LICENSE.txt` document for more information.