package br.ifsp.contacts.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.ifsp.contacts.dto.AddressDTO;
import br.ifsp.contacts.dto.ContactDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contacts")
@Validated
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    @Operation(summary = "Lista todos os contatos")
    public Page<ContactDTO> getAllContacts(Pageable pageable) {
        Page<Contact> contactsPage = contactRepository.findAll(pageable);
        return contactsPage.map(this::convertToDto);
    }

    @GetMapping("{id}")
    @Operation(summary = "Busca contato por ID")
    public ContactDTO getContactById(@PathVariable Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado: " + id));
        return convertToDto(contact);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um contato")
    public ContactDTO createContact(@Valid @RequestBody ContactDTO contactDTO) {
        Contact contact = convertToEntity(contactDTO);
        Contact savedContact = contactRepository.save(contact);
        return convertToDto(savedContact);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza todos os dados de um contato")
    public ContactDTO updateContact(@PathVariable Long id, @Valid @RequestBody ContactDTO updatedContactDTO) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado: " + id));

        existingContact.setNome(updatedContactDTO.getNome());
        existingContact.setEmail(updatedContactDTO.getEmail());
        existingContact.setTelefone(updatedContactDTO.getTelefone());
        existingContact.setAddresses(
                updatedContactDTO.getAddresses().stream()
                        .map(this::convertToAddressEntity)
                        .collect(Collectors.toList())
        );

        Contact savedContact = contactRepository.save(existingContact);
        return convertToDto(savedContact);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente os dados do contato")
    public ContactDTO updateContactPartial(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "nome" -> contact.setNome(value);
                case "telefone" -> contact.setTelefone(value);
                case "email" -> contact.setEmail(value);
            }
        });

        Contact savedContact = contactRepository.save(contact);
        return convertToDto(savedContact);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um contato")
    public void deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Busca contato por nome")
    public Page<ContactDTO> searchContactsByName(@RequestParam String name, Pageable pageable) {
        Page<Contact> contactsPage = contactRepository.findByNomeContainingIgnoreCase(name, pageable);
        return contactsPage.map(this::convertToDto);
    }

    // Métodos de conversão
    private ContactDTO convertToDto(Contact contact) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(contact.getId());
        contactDTO.setNome(contact.getNome());
        contactDTO.setEmail(contact.getEmail());
        contactDTO.setTelefone(contact.getTelefone());
        contactDTO.setAddresses(
                contact.getAddresses().stream()
                        .map(this::convertToAddressDto)
                        .collect(Collectors.toList())
        );
        return contactDTO;
    }

    private Contact convertToEntity(ContactDTO contactDTO) {
        Contact contact = new Contact();
        contact.setNome(contactDTO.getNome());
        contact.setEmail(contactDTO.getEmail());
        contact.setTelefone(contactDTO.getTelefone());

        if (contactDTO.getAddresses() != null) {
            contact.setAddresses(
                    contactDTO.getAddresses().stream()
                            .map(this::convertToAddressEntity)
                            .collect(Collectors.toList())
            );
        }
        return contact;
    }

    private AddressDTO convertToAddressDto(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setRua(address.getRua());
        addressDTO.setCidade(address.getCidade());
        addressDTO.setEstado(address.getEstado());
        addressDTO.setCep(address.getCep());
        return addressDTO;
    }

    private Address convertToAddressEntity(AddressDTO addressDTO) {
        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setRua(addressDTO.getRua());
        address.setCidade(addressDTO.getCidade());
        address.setEstado(addressDTO.getEstado());
        address.setCep(addressDTO.getCep());
        return address;
    }
}