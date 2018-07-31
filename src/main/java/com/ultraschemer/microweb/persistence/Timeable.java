package com.ultraschemer.microweb.persistence;

import javax.persistence.*;
import java.util.Date;

/**
 * This class is subclassed by all classes which have its update and creation times tracked.
 */
@MappedSuperclass
public abstract class Timeable extends Createable {
    @Column(name="updated_at")
    private Date updatedAt;

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    @PreUpdate
    public void setUpdateDate() {
        this.updatedAt = new Date();
    }
}
