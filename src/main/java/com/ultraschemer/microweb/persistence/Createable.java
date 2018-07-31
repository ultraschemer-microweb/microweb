package com.ultraschemer.microweb.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.Date;

/**
 * This class represent entities which the creation time is tracked.
 */
@MappedSuperclass
public abstract class Createable extends Identifiable {
    /**
     * The field detailing when the entity is created.
     */
    @Column(name="created_at", updatable=false)
    private Date createdAt;


    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * The creation date is set by a callback, and never should be called in another context, since these
     * calls will be ignored, because "createdAt" field is not updatable.
     */
    @PrePersist
    public void setCreationDate() {
        this.createdAt = new Date();
    }
}
