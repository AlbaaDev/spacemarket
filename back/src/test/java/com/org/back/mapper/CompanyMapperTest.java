package com.org.back.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.org.back.dto.user.CompanyDto;
import com.org.back.dto.user.ContactDto;
import com.org.back.models.Company;
import com.org.back.models.Contact;

class CompanyMapperTest {
    private final CompanyMapper mapper = Mappers.getMapper(CompanyMapper.class);

    @Test
    void companyToDto_withContacts_shouldMapListAndIgnoreBackReference() {
        // prepare
        Company company = new Company();
        company.setId(123L);
        company.setName("Acme");
        Contact c1 = new Contact();
        c1.setId(1L);
        c1.setFirstName("Alice");
        c1.setLastName("A");
        c1.setCompany(company);

        Contact c2 = new Contact();
        c2.setId(2L);
        c2.setFirstName("Bob");
        c2.setLastName("B");
        c2.setCompany(company);

        company.setContacts(List.of(c1, c2));

        // when
        CompanyDto dto = mapper.toCompanyDTO(company);

        // then
        assertNotNull(dto);
        assertEquals(company.getName(), dto.name());
        assertNotNull(dto.contacts());
        assertEquals(2, dto.contacts().size());
        for (ContactDto cd : dto.contacts()) {
            assertNull(cd.company(), "company should have been ignored to avoid recursion");
        }
    }
}
