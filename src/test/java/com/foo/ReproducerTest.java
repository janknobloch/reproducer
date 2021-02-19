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

    @Test
    public void reproduce() {
        
      //ADD PARENT
        MyParent p = new MyParent(0, "parentName", null);
        datastore.save(p);
        //ADD CHILD
        MyChild c = new MyChild(0, "childName");
        datastore.save(c);
        
        //INSERT CHILD TO PARENT WORKS FINE
        datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).update(UpdateOperators.set("child", c)).execute();
       
        //Child found
        MyChild child = datastore.find(MyChild.class).filter(Filters.eq("_id", 0L)).first();
       //Parent found
        MyParent parent = datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).first();
      System.out.println(child);
      System.out.println(parent);
      
      //Unset removes the referenced object(child in child collection) but keeps the reference intact or even throws an NPE 
      datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).update(UpdateOperators.unset("child")).execute();
      
      //Child should be still in its collection
      Assert.assertNotNull(datastore.find(MyChild.class).filter(Filters.eq("_id", 0L)).first());
      //But parent should have child removed.
      Assert.assertNull(datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).first().child);  
    }

    

}
