package br.ifsp.contacts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.ifsp.contacts.dto.AddressDTO;
import br.ifsp.contacts.exception.ResourceNotFoundException;
import br.ifsp.contacts.model.Address;
import br.ifsp.contacts.model.Contact;
import br.ifsp.contacts.repository.AddressRepository;
import br.ifsp.contacts.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/contacts/{contactId}")
    @Operation(summary = "Lista os endereços de um contato")
    public Page<AddressDTO> getAddressesByContact(@PathVariable Long contactId, Pageable pageable) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado: " + contactId));

        Page<Address> addressesPage = addressRepository.findByContact(contact, pageable);
        return addressesPage.map(this::convertToDto);
    }

    @PostMapping("/contacts/{contactId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um endereço")
    public AddressDTO createAddress(@PathVariable Long contactId, @RequestBody @Valid AddressDTO addressDTO) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado: " + contactId));

        Address address = convertToEntity(addressDTO);
        address.setContact(contact);
        Address savedAddress = addressRepository.save(address);

        return convertToDto(savedAddress);
    }

    // Métodos de conversão
    private AddressDTO convertToDto(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setRua(address.getRua());
        addressDTO.setCidade(address.getCidade());
        addressDTO.setEstado(address.getEstado());
        addressDTO.setCep(address.getCep());
        return addressDTO;
    }

    private Address convertToEntity(AddressDTO addressDTO) {
        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setRua(addressDTO.getRua());
        address.setCidade(addressDTO.getCidade());
        address.setEstado(addressDTO.getEstado());
        address.setCep(addressDTO.getCep());
        return address;
    }
}