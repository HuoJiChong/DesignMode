package com.derek.framework.Visitor;

/**
 * CEO访问者，只关注业绩
 */
public class CEOVisitor implements Visitor {
    @Override
    public void visite(Engineer engineer) {
        System.out.println("工程师："+engineer.name + " , KPI:" + engineer.kpi);
    }

    @Override
    public void visite(Manager manager) {
        System.out.println("经理："+manager.name + " , KPI:" + manager.kpi + " ,新产品数量："+manager.getProducts());
    }
}
