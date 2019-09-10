package com.derek.framework.Prototype.Login;

public class User implements Cloneable {
    public String name;
    public int age;
    public String phoneNum;
    public Address address;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", address=" + address.toString() +
                '}';
    }

    @Override
    protected User clone() {
        User user = null;
        try {

            user = (User) super.clone();
            user.address = this.address.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return user;
    }
}
