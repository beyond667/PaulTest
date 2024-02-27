package com.paul.test;


//1 判断链表中是否有环
//追击问题，搞两个指针 一个单步执行，一个每次走两步 直到两个相遇即返回true，有一个走到头即为false

//2 判断链表环的入口

import java.util.HashMap;
import java.util.Map;

public class NodeHasRound {
    public boolean hasRound(FanNode node) {
       if(node==null ||node.next==null){
           return false;
       }

       FanNode pre = node;
       FanNode next = node;

       while (next!=null){
           pre = pre.next;
           if(next.next==null){
               return false;
           }
           next = next.next.next;
           if(pre ==next){
               return true;
           }
       }
        return false;
    }

    //note 错误
    public FanNode findRound(FanNode node) {
        if(node==null ||node.next==null){
            return node;
        }

        FanNode pre = node;
        FanNode next = node;

        while (next!=null){
            pre = pre.next;
            next = next.next.next;
            if(pre ==next){
                return next.next;
            }
        }
        return node;
    }

    public FanNode findRoundNew(FanNode node) {
        if(node==null ||node.next==null){
            return node;
        }
        Map<FanNode,FanNode> fanHash =new HashMap<>();

        FanNode pre = node;

        while (pre!=null){
            pre = pre.next;
            if(fanHash.get(pre)!=null){
                return fanHash.get(pre);
            }
            fanHash.put(pre,pre);

        }
        return node;
    }

    public static void main(String[] args) {
        NodeHasRound hasRound = new NodeHasRound();
        //构建测试数据：单链表0->1..->9
        FanNode node1 = new FanNode(1);
        FanNode node2 = new FanNode(2);
        FanNode node3 = new FanNode(3);
        FanNode node4 = new FanNode(4);
        FanNode node5 = new FanNode(5);
        FanNode node6 = new FanNode(6);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node5.next = node6;
        node6.next = node3;

        System.out.println("有环：" + hasRound.hasRound(node1));
        System.out.println("环的入口：" + hasRound.findRound(node1).val); //note 错误
        System.out.println("环的入口：" + hasRound.findRoundNew(node1).val);
    }
}
