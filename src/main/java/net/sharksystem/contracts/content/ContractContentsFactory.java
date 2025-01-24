package net.sharksystem.contracts.content;

import net.sharksystem.*;
import net.sharksystem.contracts.Contract;

/**
 * Factory to create an instance of ContractContents
 */
public class ContractContentsFactory implements SharkComponentFactory {

    @Override
    public SharkComponent getComponent(SharkPeer sharkPeer) throws SharkException {
        return new ContractContentsImpl();
    }
}
