package com.foo;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.UpdateResult;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;

public class Main {

    public static void main(String[] args) {

        MongoClient mongoClient = MongoClients.create("mongodb://localhost");
        Datastore datastore = Morphia.createDatastore(mongoClient, "reproducer");

        datastore.getMapper().map(MyParent.class);
        datastore.getMapper().map(MyChild.class);
        datastore.ensureIndexes();

        System.out.println("Datastore initialized");

        
        //ADD PARENT
        MyParent p = new MyParent(0, "parentName", null);
        datastore.save(p);
        //ADD CHILD
        MyChild c = new MyChild(0, "childName");
        datastore.save(c);
        
        //INSERT CHILD TO PARENT WORKS FINE
        UpdateResult childInsertResult = datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).update(UpdateOperators.set("child", c)).execute();
        System.out.println(childInsertResult);
        
        //FIND CHILD
        MyChild childFound = datastore.find(MyChild.class).filter(Filters.eq("_id", 0L)).first();
        System.out.println(childFound.name);
        //FIND PARENT
        MyParent parentFound = datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).first();
        System.out.println(parentFound.name);
        
        
      //UNSET CHILD CAUSES NPE USING THE SAME FILTER FIND AS ABOVE
        UpdateResult unsetChildResult = datastore.find(MyParent.class).filter(Filters.eq("_id", 0L)).update(UpdateOperators.unset("child")).execute();
        System.out.println(unsetChildResult);
    }

}
