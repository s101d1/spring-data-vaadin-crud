package crud.vaadin;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import crud.backend.AddressBook;
import crud.backend.AddressBookRepository;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@UIScope
@SpringComponent
public class AddressBookForm extends AbstractForm<AddressBook> {

    private static final long serialVersionUID = 1L;

    private EventBus.UIEventBus eventBus;
    private AddressBookRepository repo;

    private TextField firstName = new MTextField("First Name");
    private TextField lastName = new MTextField("Last Name");
    private TextField companyName = new MTextField("Company Name");
    private TextArea address = new MTextArea("Address");
    private TextField phoneNumber = new MTextField("Phone Number");
    private TextField email = new MTextField("Email");

    AddressBookForm(AddressBookRepository r, EventBus.UIEventBus b) {
        this.repo = r;
        this.eventBus = b;

        // On save & cancel, publish events that other parts of the UI can listen
        setSavedHandler(addressBook -> {
            // persist changes
            repo.save(addressBook);
            // send the event for other parts of the application
            eventBus.publish(this, new AddressBookModifiedEvent(addressBook));
        });
        setResetHandler(addressBook -> eventBus.publish(this, new AddressBookModifiedEvent(addressBook)));

        setSizeUndefined();
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        firstName,
                        lastName,
                        companyName,
                        address,
                        phoneNumber,
                        email
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }

}
