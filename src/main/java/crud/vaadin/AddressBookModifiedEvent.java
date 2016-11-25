package crud.vaadin;

import crud.backend.AddressBook;

import java.io.Serializable;

public class AddressBookModifiedEvent implements Serializable {

    private final AddressBook addressBook;

    public AddressBookModifiedEvent(AddressBook p) {
        this.addressBook = p;
    }

    public AddressBook getAddressBook() {
        return addressBook;
    }
    
}
