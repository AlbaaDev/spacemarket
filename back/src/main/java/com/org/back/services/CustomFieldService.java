package com.org.back.services;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.customfield.CustomFieldDto;
import com.org.back.dto.customfield.CustomFieldRequest;
import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.CustomFieldType;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.InvalidCustomFieldException;
import com.org.back.models.CustomField;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.CustomFieldRepository;
import com.org.back.repositories.OpportunityRepository;

@Service
public class CustomFieldService {

    static final int MAX_TEXT_LENGTH = 500;

    private final CustomFieldRepository customFieldRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;

    public CustomFieldService(CustomFieldRepository customFieldRepository, ContactRepository contactRepository,
            CompanyRepository companyRepository, OpportunityRepository opportunityRepository) {
        this.customFieldRepository = customFieldRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomFieldDto> getFields(Long userId, CustomFieldTarget target) {
        return customFieldRepository.findAllByUser_IdAndTargetOrderById(userId, target).stream()
                .map(CustomFieldService::toDto).toList();
    }

    @Transactional
    public CustomFieldDto createField(User user, CustomFieldRequest request) {
        String name = request.name().trim();
        if (customFieldRepository.existsByUser_IdAndTargetAndNameIgnoreCase(user.getId(), request.target(), name)) {
            throw new InvalidCustomFieldException("A field named \"" + name + "\" already exists.");
        }
        CustomField field = new CustomField();
        field.setUser(user);
        field.setTarget(request.target());
        field.setType(request.type());
        field.setName(name);
        field.setOptions(options(request.type(), request.options()));
        return toDto(customFieldRepository.save(field));
    }

    /** Renames a field or changes its choices; its type and target never change. */
    @Transactional
    public CustomFieldDto updateField(Long userId, Long fieldId, CustomFieldRequest request) throws EntityNotFoundException {
        CustomField field = findOwned(userId, fieldId);
        if (request.type() != field.getType() || request.target() != field.getTarget()) {
            throw new InvalidCustomFieldException("The type of a field cannot be changed.");
        }
        String name = request.name().trim();
        if (customFieldRepository.existsByUser_IdAndTargetAndNameIgnoreCaseAndIdNot(userId, field.getTarget(), name, fieldId)) {
            throw new InvalidCustomFieldException("A field named \"" + name + "\" already exists.");
        }
        field.setName(name);
        List<String> options = options(field.getType(), request.options());
        if (field.getType() == CustomFieldType.SINGLE_CHOICE && !options.containsAll(field.getOptions())) {
            // A removed choice leaves no stale value behind.
            List<String> removed = field.getOptions().stream().filter(o -> !options.contains(o)).toList();
            String key = field.getId().toString();
            forEachValues(userId, field.getTarget(), values -> {
                if (removed.contains(values.get(key))) {
                    values.remove(key);
                }
            });
        }
        field.setOptions(options);
        return toDto(field);
    }

    /** Deletes the field and every value recorded for it. */
    @Transactional
    public void deleteField(Long userId, Long fieldId) throws EntityNotFoundException {
        CustomField field = findOwned(userId, fieldId);
        String key = field.getId().toString();
        forEachValues(userId, field.getTarget(), values -> values.remove(key));
        customFieldRepository.delete(field);
    }

    /**
     * Checks submitted values against the user's field definitions and returns them normalised:
     * keys are field ids, empty values are dropped, numbers and dates are in canonical form.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> validateValues(Long userId, CustomFieldTarget target, Map<String, Object> submitted) {
        Map<String, Object> clean = new LinkedHashMap<>();
        if (submitted == null || submitted.isEmpty()) {
            return clean;
        }
        Map<String, CustomField> fields = customFieldRepository.findAllByUser_IdAndTargetOrderById(userId, target)
                .stream().collect(Collectors.toMap(f -> f.getId().toString(), Function.identity()));
        for (Map.Entry<String, Object> entry : submitted.entrySet()) {
            CustomField field = fields.get(entry.getKey());
            if (field == null) {
                throw new InvalidCustomFieldException("Unknown field: " + entry.getKey());
            }
            Object value = normalise(field, entry.getValue());
            if (value != null) {
                clean.put(entry.getKey(), value);
            }
        }
        return clean;
    }

    private static Object normalise(CustomField field, Object value) {
        if (value == null || (value instanceof String s && s.isBlank())) {
            return null;
        }
        String invalid = "Invalid value for \"" + field.getName() + "\"";
        return switch (field.getType()) {
            case TEXT -> {
                if (!(value instanceof String s) || s.length() > MAX_TEXT_LENGTH) throw new InvalidCustomFieldException(invalid);
                yield s.trim();
            }
            case NUMBER -> {
                if (value instanceof Number n) yield n;
                try {
                    yield Double.valueOf(value.toString().trim());
                } catch (NumberFormatException e) {
                    throw new InvalidCustomFieldException(invalid);
                }
            }
            case DATE -> {
                try {
                    yield LocalDate.parse(value.toString()).toString();
                } catch (DateTimeParseException e) {
                    throw new InvalidCustomFieldException(invalid);
                }
            }
            case SINGLE_CHOICE -> {
                if (!field.getOptions().contains(value)) throw new InvalidCustomFieldException(invalid);
                yield value;
            }
            case CHECKBOX -> {
                if (!(value instanceof Boolean b)) throw new InvalidCustomFieldException(invalid);
                // An unchecked box is the same as no value.
                yield b ? Boolean.TRUE : null;
            }
        };
    }

    private static List<String> options(CustomFieldType type, List<String> requested) {
        if (type != CustomFieldType.SINGLE_CHOICE) {
            return new ArrayList<>();
        }
        List<String> options = new ArrayList<>(new LinkedHashSet<>(
                (requested == null ? List.<String>of() : requested).stream().map(String::trim).filter(o -> !o.isEmpty()).toList()));
        if (options.isEmpty()) {
            throw new InvalidCustomFieldException("A single choice field needs at least one choice.");
        }
        return options;
    }

    private void forEachValues(Long userId, CustomFieldTarget target, java.util.function.Consumer<Map<String, Object>> change) {
        switch (target) {
            case CONTACT -> contactRepository.findAllByUser_Id(userId).forEach(c -> change.accept(c.getCustomValues()));
            case COMPANY -> companyRepository.findAllByUser_Id(userId).forEach(c -> change.accept(c.getCustomValues()));
            case OPPORTUNITY -> opportunityRepository.findAllOwnedBy(userId).forEach(o -> change.accept(o.getCustomValues()));
        }
    }

    private CustomField findOwned(Long userId, Long fieldId) throws EntityNotFoundException {
        return customFieldRepository.findByIdAndUser_Id(fieldId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Custom field not found with id: " + fieldId));
    }

    private static CustomFieldDto toDto(CustomField field) {
        return new CustomFieldDto(field.getId(), field.getName(), field.getTarget(), field.getType(), List.copyOf(field.getOptions()));
    }
}
