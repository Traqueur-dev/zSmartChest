package fr.traqueur.storageplus.api.storage;

public interface Repository<T, DTO> {

    T toEntity(DTO dto);

    DTO toDTO(T entity);
}