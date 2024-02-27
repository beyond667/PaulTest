package com.paul.test;


//将给出的链表中的节点每 k 个一组翻转，返回翻转后的链表
//        如果链表中的节点数不是 k 的倍数，将最后剩下的节点保持原样
//        你不能更改节点中的值，只能更改节点本身。

public class FanZhuanLianBiao3 {
    public FanNode ReverseList(FanNode head, int k) {
        if (head == null || head.next == null||k<2) {
            return head;
        }

        FanNode newNode = new FanNode(-1);
        newNode.next = head;
        FanNode node = newNode;

        //start-end的反转
        FanNode cur = node.next;
        FanNode next = null;
        while (cur!=null &&cur.next!=null){
            for (int i = 0; i < k-1; i++) {
                next = cur.next;
                cur.next = next.next;
                next.next = node.next;
                node.next = next;
            }
            node = cur;
            cur = cur.next;
        }

        return newNode.next;
    }

    public static void main(String[] args) {
        FanZhuanLianBiao3 fanZhuanLianBiao = new FanZhuanLianBiao3();
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
        FanNode newList = fanZhuanLianBiao.ReverseList(listNode, 3);
        FanNode houNode = newList;
        while (houNode != null) {
            System.out.println("转换后：" + houNode.val);
            houNode = houNode.next;
        }
    }
}
