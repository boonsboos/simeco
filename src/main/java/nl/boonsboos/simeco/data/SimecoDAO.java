package nl.boonsboos.simeco.data;

import org.springframework.lang.Nullable;

public interface SimecoDAO<T> {

    /**
     * Attempts to get an item by its id
     * @param id the id of the item
     * @return the requested item or null
     */
    @Nullable T get(long id);

    /**
     * Saves item to permanent data store
     * @param item the item to save
     * @return true on success, false if failed.
     */
    boolean save(T item);
}
