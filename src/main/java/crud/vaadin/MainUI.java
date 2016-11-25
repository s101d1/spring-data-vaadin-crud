package crud.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import crud.backend.AddressBook;
import crud.backend.AddressBookRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@Title("Address Book CRUD example")
@Theme("valo")
@SpringUI
public class MainUI extends UI {

    private static final long serialVersionUID = 1L;

    private AddressBookRepository repo;
    private AddressBookForm addressBookForm;
    private EventBus.UIEventBus eventBus;
    
    private MTable<AddressBook> addressBookTable = new MTable<>(AddressBook.class)
            .withProperties("id", "firstName", "lastName", "companyName", "phoneNumber", "email")
            .withColumnHeaders("id", "First Name", "Last Name", "Company", "Phone No.", "Email")
            .setSortableProperties("firstName", "lastName", "companyName", "email")
            .withFullWidth();
    
    private TextField filterByNameText = new MTextField().withInputPrompt("Filter by name");
    private Button addNewBtn = new MButton(FontAwesome.PLUS, this::add);
    private Button editBtn = new MButton(FontAwesome.PENCIL_SQUARE_O, this::edit);
    private Button deleteBtn = new ConfirmButton(FontAwesome.TRASH_O,
            "Are you sure you want to delete the entry?", this::remove);

    public MainUI(AddressBookRepository r, AddressBookForm f, EventBus.UIEventBus b) {
        this.repo = r;
        this.addressBookForm = f;
        this.eventBus = b;
    }
    
    @Override
    protected void init(VaadinRequest request) {
        DisclosurePanel aboutBox = new DisclosurePanel("Spring Boot JPA CRUD example with Vaadin UI", new RichText().withMarkDownResource("/welcome.md"));
        setContent(
                new MVerticalLayout(
                        aboutBox,
                        new MHorizontalLayout(filterByNameText, addNewBtn, editBtn, deleteBtn),
                        addressBookTable
                ).expand(addressBookTable)
        );
        listEntities();

        addressBookTable.addMValueChangeListener(e -> adjustActionButtonState());

        // Double-clicking the row should show the edit form
        addressBookTable.addItemClickListener(e -> {
            if (e.isDoubleClick() && e.getItem() != null) {
                edit((AddressBook) e.getItemId());
            }
        });

        filterByNameText.addTextChangeListener(e -> {
            listEntities(e.getText());
        });

        // Listen to change events emitted by AddressBookForm see onEvent method
        eventBus.subscribe(this);
    }
    
    protected void adjustActionButtonState() {
        boolean hasSelection = addressBookTable.getValue() != null;
        editBtn.setEnabled(hasSelection);
        deleteBtn.setEnabled(hasSelection);
    }
    
    static final int PAGESIZE = 45;
    
    private void listEntities() {
        listEntities(filterByNameText.getValue());
    }
    
    private void listEntities(String nameFilter) {
        // A dead simple in memory listing would be:
        // addressBookTable.setRows(repo.findAll());
        
        // But we want to support filtering, first add the % marks for SQL name query
        String likeFilter = "%" + nameFilter + "%";
        //addressBookTable.setRows(repo.findByFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(likeFilter, likeFilter));

        // Lazy binding for better optimized connection from the Vaadin Table to
        // Spring Repository. This approach uses less memory and database
        // resources. Use this approach if you expect you'll have lots of data 
        // in your table. There are simpler APIs if you don't need sorting.
        addressBookTable.lazyLoadFrom(
                // entity fetching strategy
                (firstRow, asc, sortProperty) -> repo.findByFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(
                        likeFilter,
                        likeFilter,
                        new PageRequest(
                                firstRow / PAGESIZE,
                                PAGESIZE,
                                asc ? Sort.Direction.ASC : Sort.Direction.DESC,
                                // fall back to id as "natural order"
                                sortProperty == null ? "id" : sortProperty
                        )
                ),
                // count fetching strategy
                () -> (int) repo.countByFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(likeFilter, likeFilter),
                PAGESIZE
        );
        adjustActionButtonState();
        
    }
    
    public void add(ClickEvent clickEvent) {
        edit(new AddressBook());
    }
    
    public void edit(ClickEvent e) {
        edit(addressBookTable.getValue());
    }
    
    public void remove(ClickEvent e) {
        repo.delete(addressBookTable.getValue());
        addressBookTable.setValue(null);
        listEntities();
    }
    
    protected void edit(final AddressBook phoneBookEntry) {
        addressBookForm.setEntity(phoneBookEntry);
        addressBookForm.openInModalPopup();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onAddressBookModified(AddressBookModifiedEvent event) {
        listEntities();
        addressBookForm.closePopup();
    }
    
}
