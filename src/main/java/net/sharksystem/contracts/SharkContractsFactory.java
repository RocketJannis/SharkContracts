package net.sharksystem.contracts;

import net.sharksystem.SharkComponent;
import net.sharksystem.SharkComponentFactory;
import net.sharksystem.SharkException;
import net.sharksystem.SharkPeer;
import net.sharksystem.contracts.storage.ContractStorage;
import net.sharksystem.pki.SharkPKIComponent;

public class SharkContractsFactory implements SharkComponentFactory {

    private final SharkPKIComponent pki;
    private final ContractStorage storage;

    public SharkContractsFactory(SharkPKIComponent pki, ContractStorage storage) {
        this.pki = pki;
        this.storage = storage;
    }

    @Override
    public SharkComponent getComponent(SharkPeer sharkPeer) throws SharkException {
        return new SharkContractsImpl(sharkPeer, storage, pki);
    }

}
