package io.github.cdgeass;

public class Test {

    private Integer id;

    public Integer getId<caret>() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}