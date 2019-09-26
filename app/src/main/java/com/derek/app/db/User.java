package com.derek.app.db;

import com.derek.db.annotation.DbFiled;
import com.derek.db.annotation.DbTable;

@DbTable("tb_user")
public class User {

    @DbFiled("user_Id")
    public Integer user_Id;

    @DbFiled("name")
    public String name;
    //123456
    @DbFiled("password")
    public String password;

    public User() {
    }

    public User(int user_Id, String name, String password) {
        this.user_Id = user_Id;
        this.name = name;
        this.password = password;
    }

    public int getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(int user_Id) {
        this.user_Id = user_Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "BeanUser{" +
                "user_Id=" + user_Id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
