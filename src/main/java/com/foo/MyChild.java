package com.foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("reproducer-child")
public class MyChild {

    @Id
    long id;
    String name;

    public MyChild() {

    }

    public MyChild(long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
}
