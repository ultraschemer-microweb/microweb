package com.ultraschemer.microweb.persistence;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * This class performs automatic logging on all actions on database entities, sending an entire register snapshot
 * to a MongoDB database instance, suitable to save all database logs, one collection per type of entity.
 */
@MappedSuperclass
public abstract class Logabble implements Serializable {
    // TODO: Implement MongoDB logging here.
}
