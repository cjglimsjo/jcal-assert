package se.omegapoint.hackathon.jcalassert.processor;

import java.lang.annotation.*;

@Repeatable(Assert.List.class)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Assert {

    boolean that();

    boolean returns();

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface List {

        Assert[] value();
    }
}
