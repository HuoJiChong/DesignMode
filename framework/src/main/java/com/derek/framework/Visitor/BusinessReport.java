package com.derek.framework.Visitor;

import java.util.LinkedList;
import java.util.List;

public class BusinessReport {
    private List<Staff> mStaffs = new LinkedList<>();

    public BusinessReport() {
        addStaff(new Manager("王经理"));
        addStaff(new Engineer("工程师-Derek"));
        addStaff(new Engineer("工程师-Chaos"));
        addStaff(new Engineer("工程师-John"));
        addStaff(new Engineer("工程师-Roger"));
    }

    public void addStaff(Staff staff){
        mStaffs.add(staff);
    }

    public void showReport(Visitor visitor){
        for (Staff staff : mStaffs){
            staff.accept(visitor);
        }
    }
}
