package model_example;

import Annotations.NoNull;
import Annotations.PKey;

import Annotations.Unique;


public class Human {
    @PKey
    private int humanID;
    @Unique
    private String name;
    @NoNull
    private int age;
    @NoNull
    private String job;
    public Human(){

    }
    public Human(int id, String name, int age, String job){
        humanID = id;
        this.name = name;
        this.age = age;
        this.job = job;
    }

    @Override
    public String toString() {
        return "Human{" +
                "humanID=" + humanID +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", job='" + job + '\'' +
                '}';
    }
}
