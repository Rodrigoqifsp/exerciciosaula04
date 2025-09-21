package br.ifsp.contacts.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByContactId(Long contactId);
    Page<Address> findByContact(Contact contact, Pageable pageable);
}