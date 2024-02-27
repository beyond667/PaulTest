package com.paul.test;
// note 1 局部反转单链表
//   题目：
//        将一个节点数为 size 链表 m 位置到 n 位置之间的区间反转，要求时间复杂度O(n)，空间复杂度 O(1)。
//        数据范围：
//        0 -1000
//        0≤n≤1000
//        要求：空间复杂度 O(1) ，时间复杂度O(n) 。
//        输入：
//        {1,2,3,4,5},2,4
//        返回值：
//        {1,4,3,2,5}


class FanNode {
    int val;
    FanNode next = null;

    public FanNode(int val) {
        this.val = val;
    }
}

public class FanZhuanLianBiao2 {
    public FanNode ReverseList(FanNode head, int start, int end) {
        if (head == null || head.next == null) {
            return head;
        }

        FanNode newNode = new FanNode(-1);
        newNode.next = head;
        FanNode node = newNode;
        //0-start-1的直接拼上去
        for (int i = 0; i < start - 1; i++) {
            node = node.next;
        }

        //start-end的反转
        FanNode cur = node.next;
        FanNode next = null;
        for (int i = 0; i < end - start; i++) {
            next = cur.next;
            cur.next = next.next;
            next.next = node.next;
            node.next = next;
        }


        // note 这种不行 cur.next = cacheNode; 这里 需要指定之前的Node而不是null
//        FanNode cur = node.next;
//        FanNode cacheNode = null;
//        FanNode next = null;
//        while (head != null) {
//            next = cur.next;
//            cur.next = cacheNode; // 这里 需要指定之前的Node而不是null
//            cacheNode = head;
//            head = next;
//        }



        return newNode.next;
    }

    public static void main(String[] args) {
        FanZhuanLianBiao2 fanZhuanLianBiao = new FanZhuanLianBiao2();
        //构建测试数据：单链表0->1..->9
        FanNode listNode = new FanNode(0);
        FanNode temp = null;
        for (int i = 1; i < 10; i++) {
            if (i == 1) {
                temp = new FanNode(i);
                listNode.next = temp;
            } else {
                FanNode cache = new FanNode(i);
                temp.next = cache;
                temp = cache;
            }
        }
        FanNode qianNode = listNode;
        while (qianNode != null) {
            System.out.println("转换前：" + qianNode.val);
            qianNode = qianNode.next;

        }
        FanNode newList = fanZhuanLianBiao.ReverseList(listNode, 2, 4);
        FanNode houNode = newList;
        while (houNode != null) {
            System.out.println("转换后：" + houNode.val);
            houNode = houNode.next;
        }
    }
}


