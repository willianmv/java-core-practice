package com.example.management.infrastructure.persistence.file;

public interface EntityDeletionListener {

    void onEntityDeleted(long deletedEntityId);

}
