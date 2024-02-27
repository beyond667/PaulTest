package com.paul.test;
// note 1 反转单链表
//   题目：
//        给定一个单链表的头结点pHead(该头节点是有值的，比如在下图，它的val是1)，长度为n，反转该链表后，返回新链表的表头。
//        数据范围：
//        0 -1000
//        0≤n≤1000
//        要求：空间复杂度 O(1) ，时间复杂度O(n) 。
//        如当输入链表{1,2,3}时，
//        经反转后，原链表变为{3,2,1}，所以对应的输出为{3,2,1}。

import java.util.HashMap;
import java.util.Map;

class ListNode {
    int val;
    ListNode next = null;

    public ListNode(int val) {
        this.val = val;
    }
}


public class FanZhuanLianBiao1 {
    public ListNode ReverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode newNode = null;
        ListNode next = null;
        while (head != null) {
            next = head.next;
            head.next = newNode;
            newNode = head;
            head = next;
        }

        return newNode;
    }
    public static void main(String[] args) {
        FanZhuanLianBiao1 fanZhuanLianBiao = new FanZhuanLianBiao1();
        //构建测试数据：单链表0->1..->9
        ListNode listNode = new ListNode(0);
        ListNode temp = null;
        for(int i =1;i<10;i++){
            if(i==1){
                temp= new ListNode(i);
                listNode.next = temp;
            }else{
                ListNode cache= new ListNode(i);
                temp.next =cache;
                temp = cache;
            }
        }
        ListNode qianNode = listNode;
        while (qianNode!=null){
            System.out.println("转换前："+qianNode.val);
            qianNode = qianNode.next;

        }
        ListNode newList = fanZhuanLianBiao.ReverseList(listNode);
        ListNode houNode = newList;
        while (houNode!=null){
            System.out.println("转换后："+houNode.val);
            houNode = houNode.next;
        }
    }
}


