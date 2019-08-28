package com.derek.single;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

public class Main {

    public static void main(String [] argv){
        try {
            reflectionAttack();
            serializationAttack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射攻击
     *
     * @throws Exception
     */
    public static void reflectionAttack() throws Exception {
        Constructor constructor = InnerClassSingle.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InnerClassSingle s1 = (InnerClassSingle)constructor.newInstance();
        InnerClassSingle s2 = (InnerClassSingle)constructor.newInstance();
        s1.tellEveryone();
        s2.tellEveryone();
        System.out.println(s1 == s2);
    }

    /**
     * 序列化攻击
     *  这种攻击方式只对实现了Serializable接口的单例有效，但偏偏有些单例就是必须序列化的。
     *      为DCLSingle 添加上 Serializable 后
     * @throws Exception
     */
    public static void serializationAttack() throws Exception {

        Constructor constructor = DCLSingle.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        DCLSingle s1 = (DCLSingle) constructor.newInstance();
        outputStream.writeObject(s1);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        DCLSingle s2 = (DCLSingle)inputStream.readObject();
        s1.tellEveryone();
        s2.tellEveryone();
        System.out.println( s1 == s2 );
    }
}
