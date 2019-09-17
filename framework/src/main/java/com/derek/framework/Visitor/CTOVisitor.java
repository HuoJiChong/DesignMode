package com.derek.framework.Visitor;

public class CTOVisitor implements Visitor {

    @Override
    public void visite(Engineer engineer) {
        System.out.println("工程师："+engineer.name + " , 代码函数:" + engineer.getCodeLines());
    }

    @Override
    public void visite(Manager manager) {
        System.out.println("经理："+manager.name + " ,新产品数量："+manager.getProducts());
    }
}
