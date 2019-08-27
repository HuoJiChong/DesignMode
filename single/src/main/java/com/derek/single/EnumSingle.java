package com.derek.single;

/**
 * 枚举类 实现单例模式
 *
 * 枚举单例可以有效防御两种破坏单例（即使单例产生多个实例）的行为：反射攻击与序列化攻击
 *
 */
public enum EnumSingle {
    INSTANCE;

    public void tellEveryone() {
        System.out.println("This is a EnumSingle  " + this.hashCode());
    }

    EnumSingle() {
    }
}
