package com.foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;

@Entity("reproducer-parent")
public class MyParent {
    
    @Id
    long id;
    String name;
    
    @Reference
    MyChild child;
    public MyParent() {
        
    }
    public MyParent(long id, String name, MyChild child) {
        super();
        this.id = id;
        this.name = name;
        this.child = child;
    }
    
}
