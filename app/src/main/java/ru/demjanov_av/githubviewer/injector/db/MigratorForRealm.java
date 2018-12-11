package ru.demjanov_av.githubviewer.injector.db;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import ru.demjanov_av.githubviewer.models.RealmModelRep;

public class MigratorForRealm implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema realmSchema = realm.getSchema();

        if(oldVersion == 0){
            realmSchema
                    .create("RealmModelRep");

            oldVersion += 1;
        }

        if(oldVersion == 1){
            realmSchema
                    .get("RealmModelRep")
                    .addField(RealmModelRep.REP_ID, String.class, FieldAttribute.PRIMARY_KEY)
                    .addField(RealmModelRep.USER_ID, String.class)
                    .addField(RealmModelRep.NAME_REP, String.class);
            oldVersion += 1;
        }

        if(oldVersion == 2){
            realmSchema.remove("RealmModelRep");

            realmSchema
                    .create("RealmModelRep")
                    .addField(RealmModelRep.REP_ID, String.class, FieldAttribute.PRIMARY_KEY)
                    .addField(RealmModelRep.USER_ID, String.class)
                    .addField(RealmModelRep.NAME_REP, String.class);
        }
    }
}
