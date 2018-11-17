package com.ultraschemer.microweb.persistence;

import javax.persistence.*;
import java.util.UUID;

@MappedSuperclass
public abstract class Identifiable extends Loggable {
    /**
     * The primary key of Identifiable entities are always UUIDs, to maintain a secure uniqueness on them.
     */
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    public UUID getId() {
        return id;
    }
}

