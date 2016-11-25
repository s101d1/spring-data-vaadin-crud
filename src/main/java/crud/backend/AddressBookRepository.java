
package crud.backend;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Empty JpaRepository is enough for a simple crud.
 */
public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
    
    /* A version to fetch List instead of Page to avoid extra count query. */
    List<AddressBook> findAllBy(Pageable pageable);
    
    List<AddressBook> findByFirstNameLikeIgnoreCaseOrLastNameLikeIgnoreCase(String firstName, String lastName);

}
