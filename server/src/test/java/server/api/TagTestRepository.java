package server.api;

import commons.Tag;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TagTestRepository implements TagRepository {
    private ArrayList<Tag> tags = new ArrayList<>();

    /**
     *
     * @return
     */
    @Override
    public List<Tag> findAll() {
        return tags;
    }

    /**
     *
     * @param sort
     * @return
     */
    @Override
    public List<Tag> findAll(Sort sort) {
        return null;
    }

    /**
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return null;
    }

    /**
     *
     * @param longs must not be {@literal null} nor contain any {@literal null} values.
     * @return
     */
    @Override
    public List<Tag> findAllById(Iterable<Long> longs) {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public long count() {
        return 0;
    }

    /**
     *
     * @param aLong must not be {@literal null}.
     */
    @Override
    public void deleteById(Long aLong) {

    }

    /**
     *
     * @param entity must not be {@literal null}.
     */
    @Override
    public void delete(Tag entity) {
        Long idx = entity.getId();

        tags.set(idx.intValue(), null);
    }

    /**
     *
     * @param longs must not be {@literal null}. Must not contain {@literal null} elements.
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    /**
     *
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     */
    @Override
    public void deleteAll(Iterable<? extends Tag> entities) {

    }

    /**
     *
     */
    @Override
    public void deleteAll() {

    }

    /**
     *
     * @param entity must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> S save(S entity) {
        tags.add(entity);

        return entity;
    }

    /**
     *
     * @param entities must not be {@literal null} nor must it contain {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     *
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<Tag> findById(Long aLong) {
        if(!existsById(aLong))
            return Optional.empty();

        return Optional.of(tags.get(aLong.intValue()));
    }

    /**
     *
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public boolean existsById(Long aLong) {
        return aLong.intValue() <= tags.size() && tags.get(aLong.intValue()) != null;
    }

    /**
     *
     */
    @Override
    public void flush() {

    }

    /**
     *
     * @param entity entity to be saved. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> S saveAndFlush(S entity) {
        return null;
    }

    /**
     *
     * @param entities entities to be saved. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    /**
     *
     * @param entities entities to be deleted. Must not be {@literal null}.
     */
    @Override
    public void deleteAllInBatch(Iterable<Tag> entities) {

    }

    /**
     *
     * @param longs the ids of the entities to be deleted. Must not be {@literal null}.
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    /**
     *
     */
    @Override
    public void deleteAllInBatch() {

    }

    /**
     *
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public Tag getOne(Long aLong) {
        return null;
    }

    /**
     *
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public Tag getById(Long aLong) {
        return tags.get(aLong.intValue());
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> List<S> findAll(Example<S> example) {
        return null;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @param sort the {@link Sort} specification to
     *  sort the results by, must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    /**
     *
     * @param example the {@link Example} to count instances for. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> long count(Example<S> example) {
        return 0;
    }

    /**
     *
     * @param example the {@link Example} to use for
     *           the existence check. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends Tag> boolean exists(Example<S> example) {
        return false;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @param queryFunction the query function defining
     *             projection, sorting, and the result type
     * @return
     * @param <S>
     * @param <R>
     */

    @Override
    public <S extends Tag, R> R findBy(Example<S> example, Function<FluentQuery
            .FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
