package it.unicam.hackhub.infrastructure.persistence;
import it.unicam.hackhub.domain.model.RichiestaSupporto;
import it.unicam.hackhub.domain.repository.RichiestaSupportoRepository;

public class InMemoryRichiestaSupportoRepository
        extends InMemoryRepository<RichiestaSupporto, String>
        implements RichiestaSupportoRepository {

    @Override
    protected String getId(RichiestaSupporto entity) {
        return entity.getId();
    }
}