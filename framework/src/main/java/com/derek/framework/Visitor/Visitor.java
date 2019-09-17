package com.derek.framework.Visitor;

/**
 * 访问者接口
 */
public interface Visitor {
    void visite(Engineer engineer);

    void visite(Manager manager);
}
