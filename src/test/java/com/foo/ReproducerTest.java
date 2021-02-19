package com.foo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import com.antwerkz.bottlerocket.BottleRocket;
import com.antwerkz.bottlerocket.BottleRocketTest;
import com.github.zafarkhaja.semver.Version;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;

public class ReproducerTest extends BottleRocketTest {
    private Datastore datastore;

    public ReproducerTest() {
        MongoClient mongo = getMongoClient();
        MongoDatabase database = getDatabase();
        database.drop();
        datastore = Morphia.createDatastore(mongo, getDatabase().getName());
        datastore.getMapper().map(MyParent.class);
        datastore.getMapper().map(MyChild.class);
        datastore.ensureIndexes();
    }

    @NotNull
    @Override
    public String databaseName() {
        return "morphia_repro";
    }

    @Nullable
    @Override
    public Version version() {
        return BottleRocket.DEFAULT_VERSION;
    }

    @Test(expected=NullPointerException.class)
    public void reproduce() {
        
      //ADD PARENT
        MyParent p = new MyParent(0, "parentName", null);
        datastore.save(p);
        //ADD CHILD
        MyChild c = new MyChild(0, "childName");
        datastore.save(c);
        
        //INSERT CHILD TO PARENT WORKS FINE
        UpdateResult childInsertResult = datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).update(UpdateOperators.set("child", c)).execute();
       
        //FIND CHILD
        MyChild childFound = datastore.find(MyChild.class).filter(Filters.eq("_id", 0L)).first();
       //FIND PARENT
        MyParent parentFound = datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).first();
      
        
      //UNSET CHILD CAUSES NPE USING THE SAME FILTER FIND AS ABOVE
        
        Assert.assertThrows(NullPointerException.class,
                ()->{
                    datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).update(UpdateOperators.unset("child")).execute();
                }); 
        
    }

}
